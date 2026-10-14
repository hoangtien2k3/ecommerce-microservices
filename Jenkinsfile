// 프론트엔드(web) CI: 이미지 빌드 -> 컨테이너 스모크 테스트 -> Harbor push
// 태그는 커밋 SHA 앞 7자리 (latest 미사용). 백엔드 서비스는 이후 stage로 추가한다.
// Jenkins 준비물:
//   - 에이전트 라벨 'docker' (jenkins-02, Docker 설치됨)
//   - Username/Password 자격 증명 'harbor-robot-push' (robot$ecommerce+push)
pipeline {
  agent { label 'docker' }

  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  // Jenkins가 학원 PC(tailnet) 안에 있어 GitHub webhook이 닿지 않으므로 폴링한다
  triggers { pollSCM('H/5 * * * *') }

  environment {
    REGISTRY = 'harbor.osi7.shop'
    IMAGE    = 'ecommerce/web'
  }

  stages {
    stage('Tag') {
      steps {
        script {
          env.TAG = sh(script: 'git rev-parse --short=7 HEAD', returnStdout: true).trim()
        }
        echo "이미지: ${REGISTRY}/${IMAGE}:${TAG}"
      }
    }

    stage('Build web') {
      steps {
        // 빌드 규칙은 로컬과 같은 스크립트를 쓴다 (frontend/scripts/build-image.sh)
        sh 'APP=web REGISTRIES="$REGISTRY" frontend/scripts/build-image.sh'
      }
    }

    stage('Smoke test') {
      steps {
        // 빌드한 이미지를 띄워 헬스체크와 버전(= 이번 커밋 SHA)을 확인한다
        sh '''
          docker run -d --name "web-smoke-$BUILD_NUMBER" -p 127.0.0.1::3000 "$IMAGE:$TAG"
          PORT=$(docker port "web-smoke-$BUILD_NUMBER" 3000 | head -1 | cut -d: -f2)
          for i in $(seq 1 30); do
            curl -fsS "http://127.0.0.1:$PORT/api/health" && break
            sleep 1
          done
          curl -fsS "http://127.0.0.1:$PORT/api/version" | tee version.json
          grep -q "\\"version\\":\\"$TAG\\"" version.json
        '''
      }
      post {
        always { sh 'docker rm -f "web-smoke-$BUILD_NUMBER" || true' }
      }
    }

    stage('Push to Harbor') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'harbor-robot-push',
                                          usernameVariable: 'HARBOR_USR',
                                          passwordVariable: 'HARBOR_PSW')]) {
          // robot 계정 이름의 $가 풀리지 않도록 작은따옴표 sh에서 환경 변수로 쓴다
          sh '''
            echo "$HARBOR_PSW" | docker login "$REGISTRY" -u "$HARBOR_USR" --password-stdin
            docker push "$REGISTRY/$IMAGE:$TAG"
          '''
        }
      }
      post {
        always { sh 'docker logout "$REGISTRY" || true' }
      }
    }
  }
}
