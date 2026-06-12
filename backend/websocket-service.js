# WebSocket Service for Real-Time Updates
# Add this to your backend for real-time sync

const WebSocket = require('ws');
const jwt = require('jsonwebtoken');

class WebSocketService {
  constructor(server) {
    this.wss = new WebSocket.Server({ server });
    this.clients = new Map(); // userId -> Set of connections
    
    this.wss.on('connection', (ws, req) => {
      this.handleConnection(ws, req);
    });
    
    console.log('✅ WebSocket server initialized');
  }
  
  handleConnection(ws, req) {
    console.log('🔌 New WebSocket connection');
    
    // Extract token from query params
    const url = new URL(req.url, `http://${req.headers.host}`);
    const token = url.searchParams.get('token');
    
    if (!token) {
      ws.close(1008, 'Authentication required');
      return;
    }
    
    // Verify token
    try {
      const decoded = jwt.verify(token, process.env.JWT_SECRET);
      const userId = decoded.userId;
      
      // Add to clients
      if (!this.clients.has(userId)) {
        this.clients.set(userId, new Set());
      }
      this.clients.get(userId).add(ws);
      
      console.log(`✅ User ${userId} connected (${this.clients.get(userId).size} connections)`);
      
      // Send welcome message
      ws.send(JSON.stringify({
        type: 'connected',
        userId: userId,
        timestamp: new Date().toISOString()
      }));
      
      // Handle messages
      ws.on('message', (message) => {
        this.handleMessage(userId, message);
      });
      
      // Handle disconnection
      ws.on('close', () => {
        this.handleDisconnection(userId, ws);
      });
      
      // Handle errors
      ws.on('error', (error) => {
        console.error(`❌ WebSocket error for user ${userId}:`, error.message);
      });
      
    } catch (error) {
      console.error('❌ Invalid token:', error.message);
      ws.close(1008, 'Invalid token');
    }
  }
  
  handleMessage(userId, message) {
    try {
      const data = JSON.parse(message);
      console.log(`📨 Message from user ${userId}:`, data.type);
      
      // Handle different message types
      switch (data.type) {
        case 'ping':
          this.sendToUser(userId, { type: 'pong', timestamp: Date.now() });
          break;
        case 'subscribe':
          console.log(`User ${userId} subscribed to: ${data.channels}`);
          break;
        default:
          console.log(`Unknown message type: ${data.type}`);
      }
    } catch (error) {
      console.error('❌ Error handling message:', error.message);
    }
  }
  
  handleDisconnection(userId, ws) {
    const userClients = this.clients.get(userId);
    if (userClients) {
      userClients.delete(ws);
      if (userClients.size === 0) {
        this.clients.delete(userId);
        console.log(`❌ User ${userId} disconnected`);
      } else {
        console.log(`⚠️  User ${userId} has ${userClients.size} remaining connections`);
      }
    }
  }
  
  // Broadcast to all connected clients
  broadcast(data) {
    const message = JSON.stringify(data);
    this.clients.forEach((connections, userId) => {
      connections.forEach(ws => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.send(message);
        }
      });
    });
  }
  
  // Send to specific user
  sendToUser(userId, data) {
    const message = JSON.stringify(data);
    const connections = this.clients.get(userId);
    if (connections) {
      connections.forEach(ws => {
        if (ws.readyState === WebSocket.OPEN) {
          ws.send(message);
        }
      });
    }
  }
  
  // Send real-time update when data changes
  notifyDataChange(tableName, recordId, operation, userId = null) {
    const data = {
      type: 'data_change',
      table: tableName,
      recordId: recordId,
      operation: operation, // 'insert', 'update', 'delete'
      timestamp: new Date().toISOString()
    };
    
    if (userId) {
      this.sendToUser(userId, data);
    } else {
      this.broadcast(data);
    }
  }
  
  // Get connected users count
  getConnectedCount() {
    return this.clients.size;
  }
  
  // Get all connected user IDs
  getConnectedUsers() {
    return Array.from(this.clients.keys());
  }
}

module.exports = WebSocketService;

// Usage in server.js:
// const WebSocketService = require('./websocket-service');
// const wss = new WebSocketService(server);
// 
// // Notify clients when data changes
// wss.notifyDataChange('children', childId, 'insert');
