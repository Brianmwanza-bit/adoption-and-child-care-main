import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  vus: 50,
  duration: '30s',
};

export default function () {
  const url = 'http://localhost:50000/login';
  const payload = JSON.stringify({ username: 'admin', password: 'admin' });
  const params = { headers: { 'Content-Type': 'application/json' } };
  let res = http.post(url, payload, params);
  check(res, { 'status is 200': (r) => r.status === 200 });
  sleep(1);
} 