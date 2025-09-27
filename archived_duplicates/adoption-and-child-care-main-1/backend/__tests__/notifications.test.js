const request = require('supertest');
const app = require('../server');

describe('Notifications API', () => {
  let token;
  let notificationId;
  const testUserId = 1; // Use a valid user_id from your DB or mock

  beforeAll(async () => {
    // Login to get a token (replace with valid credentials)
    const res = await request(app)
      .post('/login')
      .send({ username: 'admin', password: 'admin' });
    token = res.body.token;
  });

  it('should fail to create notification without auth', async () => {
    const res = await request(app)
      .post('/notifications')
      .send({ user_id: testUserId, message: 'Test notification' });
    expect(res.statusCode).toBe(401);
  });

  it('should fail to create notification with missing fields', async () => {
    const res = await request(app)
      .post('/notifications')
      .set('Authorization', `Bearer ${token}`)
      .send({ user_id: testUserId });
    expect(res.statusCode).toBe(400);
  });

  it('should create a notification', async () => {
    const res = await request(app)
      .post('/notifications')
      .set('Authorization', `Bearer ${token}`)
      .send({ user_id: testUserId, message: 'Test notification' });
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('notification_id');
    notificationId = res.body.notification_id;
  });

  it('should fetch notifications for user', async () => {
    const res = await request(app)
      .get('/notifications')
      .set('Authorization', `Bearer ${token}`);
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('data');
    expect(Array.isArray(res.body.data)).toBe(true);
  });

  it('should mark notification as read', async () => {
    const res = await request(app)
      .put(`/notifications/${notificationId}/read`)
      .set('Authorization', `Bearer ${token}`);
    expect(res.statusCode).toBe(200);
    expect(res.body.success).toBe(true);
  });

  it('should get unread count', async () => {
    const res = await request(app)
      .get('/notifications/unread-count')
      .set('Authorization', `Bearer ${token}`);
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('unread');
  });
}); 