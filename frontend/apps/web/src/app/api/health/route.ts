// k8s probe 대상. 외부 의존성을 넣지 않는다:
// 백엔드 장애로 프론트 파드가 재시작되거나 NotReady가 되면 안 된다.
export const dynamic = 'force-dynamic';

export function GET() {
  return Response.json(
    { status: 'ok' },
    { headers: { 'Cache-Control': 'no-store' } },
  );
}
