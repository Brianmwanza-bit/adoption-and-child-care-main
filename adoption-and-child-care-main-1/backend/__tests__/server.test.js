const request = require('supertest');
const app = require('../server');

describe('GET /analytics/summary', () => {
  it('should return analytics summary', async () => {
    const res = await request(app)
      .get('/analytics/summary')
      .set('Authorization', 'Bearer testtoken'); // Replace with a valid token for real test
    expect(res.statusCode).toBe(200);
    expect(res.body).toHaveProperty('user_count');
    expect(res.body).toHaveProperty('family_count');
    expect(res.body).toHaveProperty('task_count');
    expect(res.body).toHaveProperty('match_count');
    expect(res.body).toHaveProperty('background_check_count');
  });
}); 