#!/usr/bin/env bash
# 프론트엔드 이미지를 팀 규칙(ecommerce/<앱>:<git SHA 7자리>)으로 빌드한다.
# 사용법 (frontend/ 밖에서 실행해도 됨):
#   scripts/build-image.sh                 # 로컬 빌드만
#   REGISTRIES="harbor.osi7.shop" scripts/build-image.sh push
set -euo pipefail

cd "$(dirname "$0")/.."                       # frontend/ 로 이동 (빌드 컨텍스트)

APP="${APP:-web}"                             # web 또는 admin
TAG="${TAG:-$(git rev-parse --short=7 HEAD)}" # 태그: 밖에서 TAG를 주면 그 값, 없으면 HEAD SHA 7자리
IMAGE="ecommerce/${APP}:${TAG}"

if [ -n "$(git status --porcelain -- .)" ]; then
  echo "경고: frontend/에 커밋 안 된 변경이 있어 이미지 내용과 태그 ${TAG}가 다를 수 있음" >&2
fi

docker build -f "apps/${APP}/Dockerfile" --build-arg APP_VERSION="${TAG}" -t "${IMAGE}" .

for REGISTRY in ${REGISTRIES:-}; do           # 레지스트리마다 같은 경로·태그로 붙인다
  docker tag "${IMAGE}" "${REGISTRY}/${IMAGE}"
  if [ "${1:-}" = "push" ]; then
    docker push "${REGISTRY}/${IMAGE}"
  fi
done

echo "완료: ${IMAGE}"