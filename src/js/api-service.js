// API Service for Adoption & Child Care System
const API_BASE_URL = 'http://192.168.43.197:50000';

class ApiService {
    constructor() {
        this.baseURL = API_BASE_URL;
        this.token = localStorage.getItem('authToken');
    }

    setToken(token) {
        this.token = token;
        localStorage.setItem('authToken', token);
    }

    clearToken() {
        this.token = null;
        localStorage.removeItem('authToken');
    }

    getHeaders() {
        const headers = {
            'Content-Type': 'application/json'
        };
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
        }
        return headers;
    }

    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const config = {
            ...options,
            headers: {
                ...this.getHeaders(),
                ...options.headers
            }
        };

        try {
            const response = await fetch(url, config);
            const data = await response.json();
            
            if (!response.ok) {
                throw new Error(data.message || 'API request failed');
            }
            
            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }

    // Generic CRUD operations for any table
    async getAll(table) {
        return this.request(`/${table}`);
    }

    async getById(table, id) {
        return this.request(`/${table}/${id}`);
    }

    async create(table, data) {
        return this.request(`/${table}`, {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async update(table, id, data) {
        return this.request(`/${table}/${id}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async delete(table, id) {
        return this.request(`/${table}/${id}`, {
            method: 'DELETE'
        });
    }

    // Authentication
    async login(email, password) {
        const response = await this.request('/login', {
            method: 'POST',
            body: JSON.stringify({ email, password })
        });
        
        if (response.token) {
            this.setToken(response.token);
        }
        
        return response;
    }

    async register(userData) {
        return this.request('/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData)
        });
    }

    // Specific endpoints for main entities
    
    // Children
    async getChildren() {
        return this.getAll('children');
    }

    async getChildById(id) {
        return this.getById('children', id);
    }

    async updateChild(id, data) {
        return this.update('children', id, data);
    }

    async deleteChild(id) {
        return this.delete('children', id);
    }

    // Users
    async getUsers() {
        return this.getAll('users');
    }

    async getUserById(id) {
        return this.getById('users', id);
    }

    async updateUser(id, data) {
        return this.update('users', id, data);
    }

    async deleteUser(id) {
        return this.delete('users', id);
    }

    // Families
    async getFamilies() {
        return this.getAll('families');
    }

    async getFamilyProfile() {
        return this.request('/family_profile');
    }

    async createFamilyProfile(data) {
        return this.request('/family_profile', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async updateFamilyProfile(id, data) {
        return this.request(`/family_profile/${id}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async deleteFamilyProfile(id) {
        return this.request(`/family_profile/${id}`, {
            method: 'DELETE'
        });
    }

    // Guardians
    async getGuardians() {
        return this.getAll('guardians');
    }

    async updateGuardian(id, data) {
        return this.update('guardians', id, data);
    }

    async deleteGuardian(id) {
        return this.delete('guardians', id);
    }

    // Placements
    async getPlacements() {
        return this.getAll('placements');
    }

    async updatePlacement(id, data) {
        return this.update('placements', id, data);
    }

    async deletePlacement(id) {
        return this.delete('placements', id);
    }

    // Adoption Applications
    async getAdoptionApplications() {
        return this.getAll('adoption_applications');
    }

    async createAdoptionApplication(data) {
        return this.create('adoption_applications', data);
    }

    async updateAdoptionApplication(id, data) {
        return this.update('adoption_applications', id, data);
    }

    async deleteAdoptionApplication(id) {
        return this.delete('adoption_applications', id);
    }

    // Foster Care
    async getFosterTasks() {
        return this.request('/foster_tasks');
    }

    async createFosterTask(data) {
        return this.request('/foster_tasks', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async updateFosterTask(id, data) {
        return this.request(`/foster_tasks/${id}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async deleteFosterTask(id) {
        return this.request(`/foster_tasks/${id}`, {
            method: 'DELETE'
        });
    }

    async getFosterMatches() {
        return this.request('/foster_matches');
    }

    async createFosterMatch(data) {
        return this.request('/foster_matches', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async updateFosterMatch(id, data) {
        return this.request(`/foster_matches/${id}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async deleteFosterMatch(id) {
        return this.request(`/foster_matches/${id}`, {
            method: 'DELETE'
        });
    }

    // Background Checks
    async getBackgroundChecks() {
        return this.request('/background_checks');
    }

    async createBackgroundCheck(data) {
        return this.request('/background_checks', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async updateBackgroundCheck(id, data) {
        return this.request(`/background_checks/${id}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async deleteBackgroundCheck(id) {
        return this.request(`/background_checks/${id}`, {
            method: 'DELETE'
        });
    }

    async triggerBackgroundCheck(userId) {
        return this.request(`/background_checks/${userId}/trigger`, {
            method: 'POST'
        });
    }

    async getBackgroundCheckForUser(userId) {
        return this.request(`/background_checks/${userId}`);
    }

    // Medical Records
    async getMedicalRecords() {
        return this.getAll('medical_records');
    }

    async updateMedicalRecord(id, data) {
        return this.update('medical_records', id, data);
    }

    async deleteMedicalRecord(id) {
        return this.delete('medical_records', id);
    }

    // Education Records
    async getEducationRecords() {
        return this.getAll('education_records');
    }

    async updateEducationRecord(id, data) {
        return this.update('education_records', id, data);
    }

    async deleteEducationRecord(id) {
        return this.delete('education_records', id);
    }

    // Documents
    async getDocuments() {
        return this.getAll('documents');
    }

    async updateDocument(id, data) {
        return this.update('documents', id, data);
    }

    async deleteDocument(id) {
        return this.delete('documents', id);
    }

    // Case Reports
    async getCaseReports() {
        return this.getAll('case_reports');
    }

    async updateCaseReport(id, data) {
        return this.update('case_reports', id, data);
    }

    async deleteCaseReport(id) {
        return this.delete('case_reports', id);
    }

    // Court Cases
    async getCourtCases() {
        return this.getAll('court_cases');
    }

    async updateCourtCase(id, data) {
        return this.update('court_cases', id, data);
    }

    async deleteCourtCase(id) {
        return this.delete('court_cases', id);
    }

    // Home Studies
    async getHomeStudies() {
        return this.getAll('home_studies');
    }

    async createHomeStudy(data) {
        return this.create('home_studies', data);
    }

    async updateHomeStudy(id, data) {
        return this.update('home_studies', id, data);
    }

    async deleteHomeStudy(id) {
        return this.delete('home_studies', id);
    }

    // Money Records
    async getMoneyRecords() {
        return this.getAll('money_records');
    }

    async updateMoneyRecord(id, data) {
        return this.update('money_records', id, data);
    }

    async deleteMoneyRecord(id) {
        return this.delete('money_records', id);
    }

    // Notifications
    async getNotifications() {
        return this.request('/notifications');
    }

    async createNotification(data) {
        return this.request('/notifications', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async markNotificationAsRead(id) {
        return this.request(`/notifications/${id}/read`, {
            method: 'PUT'
        });
    }

    async getUnreadNotificationCount() {
        return this.request('/notifications/unread-count');
    }

    async sendNotification(data) {
        return this.request('/notifications/send', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    // Analytics
    async getAnalyticsSummary() {
        return this.request('/analytics/summary');
    }

    async getRoleAnalytics() {
        return this.request('/analytics/roles');
    }

    async getRecentActivity() {
        return this.request('/analytics/recent-activity');
    }

    async getPendingBackgroundChecks() {
        return this.request('/analytics/pending-background-checks');
    }

    // Audit Logs
    async getAuditLogs() {
        return this.getAll('audit_logs');
    }

    async updateAuditLog(id, data) {
        return this.update('audit_logs', id, data);
    }

    async deleteAuditLog(id) {
        return this.delete('audit_logs', id);
    }

    // Permissions
    async getPermissions() {
        return this.getAll('permissions');
    }

    async updatePermission(id, data) {
        return this.update('permissions', id, data);
    }

    async deletePermission(id) {
        return this.delete('permissions', id);
    }

    // User Permissions
    async updateUserPermission(userId, permissionId, data) {
        return this.request(`/user_permissions/${userId}/${permissionId}`, {
            method: 'PUT',
            body: JSON.stringify(data)
        });
    }

    async deleteUserPermission(userId, permissionId) {
        return this.request(`/user_permissions/${userId}/${permissionId}`, {
            method: 'DELETE'
        });
    }

    // Location Services
    async updateUserLocation(id, latitude, longitude) {
        return this.request(`/users/${id}/location`, {
            method: 'PUT',
            body: JSON.stringify({ latitude, longitude })
        });
    }

    async getAllUserLocations() {
        return this.request('/users/locations');
    }

    async updateFamilyLocation(id, latitude, longitude) {
        return this.request(`/family_profile/${id}/location`, {
            method: 'PUT',
            body: JSON.stringify({ latitude, longitude })
        });
    }

    async getAllFamilyLocations() {
        return this.request('/family_profile/locations');
    }

    // Matching System
    async findMatches(criteria) {
        return this.request('/match', {
            method: 'POST',
            body: JSON.stringify(criteria)
        });
    }

    // Photo Upload
    async uploadPhoto(formData) {
        return this.request('/upload-photo', {
            method: 'POST',
            headers: {}, // Let browser set Content-Type for FormData
            body: formData
        });
    }

    async updateUserPhoto(id, formData) {
        return this.request(`/users/${id}/photo`, {
            method: 'PUT',
            headers: {}, // Let browser set Content-Type for FormData
            body: formData
        });
    }

    // Emergency Services
    async sendEmergencyAlert(data) {
        return this.request('/api/v2/emergency/alert', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    async sendEmergencyLocation(data) {
        return this.request('/api/v2/emergency/location', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    // Sync Services
    async syncPull() {
        return this.request('/api/v2/sync/pull');
    }

    async syncPush(data) {
        return this.request('/api/v2/sync/push', {
            method: 'POST',
            body: JSON.stringify(data)
        });
    }

    // FCM Tokens
    async registerFCMToken(token) {
        return this.request('/api/fcm/token', {
            method: 'POST',
            body: JSON.stringify({ token })
        });
    }

    // Utility
    async checkUserExists(email) {
        return this.request('/user-exists', {
            method: 'POST',
            body: JSON.stringify({ email })
        });
    }

    async getDatabaseStatus() {
        return this.request('/admin-db-status');
    }
}

// Create singleton instance
const apiService = new ApiService();

// Make it available globally
window.apiService = apiService;