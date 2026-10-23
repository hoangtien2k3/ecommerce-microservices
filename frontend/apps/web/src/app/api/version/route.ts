import os from 'node:os';

// Canary·무중단 배포 검증 시 어느 버전·파드가 응답했는지 확인하는 용도 (k6 집계, curl 확인)
// pod/env는 실행 시점 값이라 요청마다 읽어야 한다.
export const dynamic = 'force-dynamic';

export function GET() {
  return Response.json(
    {
      // 빌드 시 Dockerfile ARG로 주입된 git SHA (실행 중에는 바뀌지 않음)
      version: process.env.NEXT_PUBLIC_APP_VERSION ?? 'unknown',
      // k8s에서는 hostname이 파드 이름과 같아서 downward API 없이도 충분하다
      pod: process.env.POD_NAME ?? os.hostname(),
      env: process.env.APP_ENV ?? 'local', // onprem | aws
    },
    { headers: { 'Cache-Control': 'no-store' } },
  );
}
