// app CI: 바뀐 서비스만 이미지 빌드 -> (web은 스모크 테스트) -> Harbor push
// 서비스별 태그 = 그 서비스 경로를 마지막으로 바꾼 커밋 SHA 7자리 (latest 미사용)
// Harbor에 그 태그가 이미 있으면 빌드를 건너뛴다. 강제 재빌드는 Build with Parameters에서 체크한다.
// Jenkins 준비물:
//   - 에이전트 라벨 'docker' (jenkins-02, Docker 설치됨)
//   - Username/Password 자격 증명 'harbor-robot-push' (robot$ecommerce+push)

// 서비스별로 이미지 내용에 영향을 주는 경로 (백엔드는 공통 모듈과 루트 pom.xml 포함)
def SERVICE_PATHS = [
  web      : 'frontend',
  auth     : 'auth-service common-lib pom.xml',
  product  : 'product-service common-lib pom.xml',
  order    : 'order-service common-lib pom.xml',
  promotion: 'promotion-service common-lib pom.xml',
]

pipeline {
  agent { label 'docker' }

  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  // Jenkins가 새싹 PC(tailnet) 안에 있어 GitHub webhook이 닿지 않으므로 폴링한다
  triggers { pollSCM('H/5 * * * *') }

  // 체크한 서비스는 Harbor에 같은 태그가 있어도 다시 빌드한다
  parameters {
    booleanParam(name: 'FORCE_WEB',       defaultValue: false, description: 'web 강제 재빌드')
    booleanParam(name: 'FORCE_AUTH',      defaultValue: false, description: 'auth 강제 재빌드')
    booleanParam(name: 'FORCE_PRODUCT',   defaultValue: false, description: 'product 강제 재빌드')
    booleanParam(name: 'FORCE_ORDER',     defaultValue: false, description: 'order 강제 재빌드')
    booleanParam(name: 'FORCE_PROMOTION', defaultValue: false, description: 'promotion 강제 재빌드')
  }

  environment {
    REGISTRY = 'harbor.osi7.shop'
  }

  stages {
    stage('Prepare') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'harbor-robot-push',
                                          usernameVariable: 'HARBOR_USR',
                                          passwordVariable: 'HARBOR_PSW')]) {
          // robot 계정 이름의 $가 풀리지 않도록 작은따옴표 sh에서 환경 변수로 쓴다
          sh 'echo "$HARBOR_PSW" | docker login "$REGISTRY" -u "$HARBOR_USR" --password-stdin'
        }
        script {
          def summary = []
          def mvnModules = []
          SERVICE_PATHS.each { svc, paths ->
            def key = svc.toUpperCase()
            // 이 서비스 경로를 마지막으로 바꾼 커밋의 SHA 앞 7자리
            def tag = sh(script: "git log -1 --format=%H -- ${paths} | cut -c1-7",
                         returnStdout: true).trim()
            // Harbor에 이 태그가 이미 있으면 0
            def exists = sh(script: "docker manifest inspect ${env.REGISTRY}/ecommerce/${svc}:${tag} > /dev/null 2>&1",
                            returnStatus: true) == 0
            def forced = params["FORCE_${key}".toString()]   // GString 키는 Map에서 못 찾으므로 String으로
            def needBuild = forced || !exists

            env["TAG_${key}".toString()] = tag
            env["BUILD_${key}".toString()] = needBuild ? 'true' : 'false'
            if (needBuild && svc != 'web') { mvnModules << "${svc}-service" }
            summary << "${svc}:${tag} ${needBuild ? (forced ? '강제 빌드' : '빌드') : '건너뜀'}"
          }
          env.MVN_MODULES = mvnModules.join(',')
          currentBuild.description = summary.join(', ')
          echo summary.join('\n')
        }
      }
    }

    stage('Web') {
      when { environment name: 'BUILD_WEB', value: 'true' }
      environment {
        IMAGE = 'ecommerce/web'
        TAG   = "${env.TAG_WEB}"
      }
      stages {
        stage('Build web') {
          steps {
            // 빌드 규칙은 로컬과 같은 스크립트를 쓴다 (frontend/scripts/build-image.sh)
            sh 'APP=web REGISTRIES="$REGISTRY" frontend/scripts/build-image.sh'
          }
        }

        stage('Smoke test') {
          steps {
            // 빌드한 이미지를 띄워 헬스체크와 버전(= 이번 태그)을 확인한다
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

        stage('Push web') {
          steps {
            sh 'docker push "$REGISTRY/$IMAGE:$TAG"'
          }
        }
      }
    }

    stage('Backend') {
      when { expression { env.MVN_MODULES?.trim() } }
      stages {
        stage('Maven package') {
          steps {
            // jenkins-02에 Maven이 없으므로 Maven 컨테이너로 jar를 만든다.
            // -u: 결과 파일이 root 소유가 되지 않게, -am: 공통 모듈(common-lib)도 함께 빌드
            sh '''
              mkdir -p "$HOME/.m2"
              docker run --rm -u "$(id -u):$(id -g)" \
                -v "$PWD":/ws -w /ws \
                -v "$HOME/.m2":/var/maven/.m2 -e MAVEN_CONFIG=/var/maven/.m2 \
                maven:3.9-eclipse-temurin-21 \
                mvn -B -Duser.home=/var/maven -pl "$MVN_MODULES" -am clean package -DskipTests
            '''
          }
        }

        stage('Images') {
          // 빌드 컨텍스트는 서비스 폴더 (루트 .dockerignore가 target/을 제외하므로)
          parallel {
            stage('auth') {
              when { environment name: 'BUILD_AUTH', value: 'true' }
              steps {
                sh '''
                  docker build -t "$REGISTRY/ecommerce/auth:$TAG_AUTH" auth-service
                  docker push "$REGISTRY/ecommerce/auth:$TAG_AUTH"
                '''
              }
            }
            stage('product') {
              when { environment name: 'BUILD_PRODUCT', value: 'true' }
              steps {
                sh '''
                  docker build -t "$REGISTRY/ecommerce/product:$TAG_PRODUCT" product-service
                  docker push "$REGISTRY/ecommerce/product:$TAG_PRODUCT"
                '''
              }
            }
            stage('order') {
              when { environment name: 'BUILD_ORDER', value: 'true' }
              steps {
                sh '''
                  docker build -t "$REGISTRY/ecommerce/order:$TAG_ORDER" order-service
                  docker push "$REGISTRY/ecommerce/order:$TAG_ORDER"
                '''
              }
            }
            stage('promotion') {
              when { environment name: 'BUILD_PROMOTION', value: 'true' }
              steps {
                sh '''
                  docker build -t "$REGISTRY/ecommerce/promotion:$TAG_PROMOTION" promotion-service
                  docker push "$REGISTRY/ecommerce/promotion:$TAG_PROMOTION"
                '''
              }
            }
          }
        }
      }
    }
  }

  post {
    always { sh 'docker logout "$REGISTRY" || true' }
  }
}