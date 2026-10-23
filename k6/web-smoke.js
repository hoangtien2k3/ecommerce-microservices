// web 스모크/부하 테스트. 5xx·연결 실패 0건과 p95 응답 시간을 확인한다.
// 실행: k6 run -e BASE_URL=http://<호스트>:3000 k6/web-smoke.js
//       (시간·VU 변경: -e VUS=20 -e DURATION=2m)
// 배포 중 버전 전환 확인: --out csv=out.csv 로 저장하면 web_version 행의 version 태그로 버전별 응답 수를 셀 수 있다.
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:3000';
const versions = new Counter('web_version');

export const options = {
  vus: Number(__ENV.VUS || 10),
  duration: __ENV.DURATION || '1m',
  thresholds: {
    http_req_failed: ['rate==0'], // 5xx·연결 실패 0건
    http_req_duration: ['p(95)<500'],
    checks: ['rate==1'],
  },
};

export default function () {
  const health = http.get(`${BASE_URL}/api/health`);
  check(health, { 'health 200': (r) => r.status === 200 });

  const version = http.get(`${BASE_URL}/api/version`);
  check(version, { 'version 200': (r) => r.status === 200 });
  if (version.status === 200) {
    versions.add(1, { version: version.json('version') });
  }

  const home = http.get(`${BASE_URL}/`);
  check(home, { 'home 200': (r) => r.status === 200 });

  sleep(1);
}
