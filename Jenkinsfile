// app CI: 바뀐 서비스만 이미지 빌드 -> (web은 스모크 테스트) -> Harbor push -> gitops dev 태그 갱신 -> (ECR 있으면) ECR push
// 서비스별 태그 = 그 서비스 경로를 마지막으로 바꾼 커밋 SHA 7자리 (latest 미사용)
// Harbor에 그 태그가 이미 있으면 빌드를 건너뛴다. 강제 재빌드는 Build with Parameters에서 체크한다.
// Jenkins 준비물:
//   - 에이전트 라벨 'docker' (jenkins-02, Docker 설치됨)
//   - Username/Password 자격 증명 'harbor-robot-push' (robot$ecommerce+push)
//   - Username/Password 자격 증명 'gitops-push' (GitHub fine-grained PAT, gitops Contents RW)
//   - Username/Password 자격 증명 'aws-ecr-push' (IAM 사용자 osi7-jenkins-ecr-push의 Access key ID / Secret)
//   - Secret text 자격 증명 'slack-webhook' (Slack Webhook URL)

// 서비스별로 이미지 내용에 영향을 주는 경로 (백엔드는 공통 모듈과 루트 pom.xml 포함)
def SERVICE_PATHS = [
  web      : 'frontend',
  auth     : 'auth-service common-lib pom.xml',
  product  : 'product-service common-lib pom.xml',
  order    : 'order-service common-lib pom.xml',
  promotion: 'promotion-service common-lib pom.xml',
]

// Slack Incoming Webhook으로 빌드 결과를 보낸다 (웹훅 URL은 자격 증명 'slack-webhook', Secret text)
// 알림 전송이 실패해도 빌드 결과는 바꾸지 않는다
def notifySlack(String title) {
  // 이번 빌드의 마지막 커밋 (누구의 어떤 변경에서 깨졌는지 바로 보이게)
  // (checkout 전에 실패해도 알림은 가도록 || true)
  def commit = sh(script: "git log -1 --format='%h %an: %s' 2>/dev/null || true", returnStdout: true).trim()
  def text = "${title}: ${env.JOB_NAME} #${env.BUILD_NUMBER}\n" +
             "${currentBuild.description ?: ''}\n" +
             "커밋 ${commit}\n" +
             "${env.BUILD_URL}console"
  // 커밋 메시지에 따옴표가 있어도 깨지지 않게 JSON은 Groovy로 만들어 파일로 넘긴다
  writeFile file: 'slack.json', text: groovy.json.JsonOutput.toJson([text: text])
  withCredentials([string(credentialsId: 'slack-webhook', variable: 'SLACK_WEBHOOK')]) {
    sh '''
      curl -sS --fail --max-time 10 -H 'Content-Type: application/json' \
        --data @slack.json "$SLACK_WEBHOOK" \
        || echo "Slack 알림 전송 실패 (빌드 결과에는 영향 없음)"
      rm -f slack.json
    '''
  }
}

pipeline {
  agent { label 'docker' }

  options {
    disableConcurrentBuilds()
    buildDiscarder(logRotator(numToKeepStr: '20'))
  }

  // GitHub webhook(push)으로 바로 시작하고, webhook이 놓친 push는 15분 폴링으로 잡는다
  triggers {
    githubPush()
    pollSCM('H/15 * * * *')
  }

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
          def result = [:]     // 서비스 이름 -> [tag: 태그, build: 빌드 여부]
          def summary = []
          SERVICE_PATHS.each { svc, paths ->
            // 이 서비스 경로를 마지막으로 바꾼 커밋의 SHA 앞 7자리
            def tag = sh(script: "git log -1 --format=%H -- ${paths} | cut -c1-7",
                         returnStdout: true).trim()
            // Harbor에 이 태그가 이미 있으면 0
            def exists = sh(script: "docker manifest inspect ${env.REGISTRY}/ecommerce/${svc}:${tag} > /dev/null 2>&1",
                            returnStatus: true) == 0
            def forced = params["FORCE_${svc.toUpperCase()}".toString()]   // GString 키는 Map에서 못 찾으므로 String으로
            def needBuild = forced || !exists
            result[svc] = [tag: tag, build: needBuild]
            summary << "${svc}:${tag} ${needBuild ? (forced ? '강제 빌드' : '빌드') : '건너뜀'}"
          }

          // Jenkins 보안 샌드박스가 env["이름"] = 값 같은 동적 대입을 막으므로 하나씩 대입한다
          env.TAG_WEB         = result.web.tag
          env.TAG_AUTH        = result.auth.tag
          env.TAG_PRODUCT     = result.product.tag
          env.TAG_ORDER       = result.order.tag
          env.TAG_PROMOTION   = result.promotion.tag
          env.BUILD_WEB       = result.web.build ? 'true' : 'false'
          env.BUILD_AUTH      = result.auth.build ? 'true' : 'false'
          env.BUILD_PRODUCT   = result.product.build ? 'true' : 'false'
          env.BUILD_ORDER     = result.order.build ? 'true' : 'false'
          env.BUILD_PROMOTION = result.promotion.build ? 'true' : 'false'

          // Maven에 넘길 백엔드 모듈 목록 (예: auth-service,order-service)
          def modules = []
          for (svc in ['auth', 'product', 'order', 'promotion']) {
            if (result[svc].build) { modules << "${svc}-service" }
          }
          env.MVN_MODULES = modules.join(',')

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
                  docker build --provenance=false -t "$REGISTRY/ecommerce/auth:$TAG_AUTH" auth-service
                  docker push "$REGISTRY/ecommerce/auth:$TAG_AUTH"
                '''
              }
            }
            stage('product') {
              when { environment name: 'BUILD_PRODUCT', value: 'true' }
              steps {
                sh '''
                  docker build --provenance=false -t "$REGISTRY/ecommerce/product:$TAG_PRODUCT" product-service
                  docker push "$REGISTRY/ecommerce/product:$TAG_PRODUCT"
                '''
              }
            }
            stage('order') {
              when { environment name: 'BUILD_ORDER', value: 'true' }
              steps {
                sh '''
                  docker build --provenance=false -t "$REGISTRY/ecommerce/order:$TAG_ORDER" order-service
                  docker push "$REGISTRY/ecommerce/order:$TAG_ORDER"
                '''
              }
            }
            stage('promotion') {
              when { environment name: 'BUILD_PROMOTION', value: 'true' }
              steps {
                sh '''
                  docker build --provenance=false -t "$REGISTRY/ecommerce/promotion:$TAG_PROMOTION" promotion-service
                  docker push "$REGISTRY/ecommerce/promotion:$TAG_PROMOTION"
                '''
              }
            }
          }
        }
      }
    }

    stage('Update gitops (dev)') {
      // 앞 단계가 모두 성공했을 때만 실행된다 (빌드나 push가 실패하면 여기까지 오지 않음)
      environment {
        GITOPS_REPO = 'https://github.com/osi7layers/gitops.git'
      }
      steps {
        withCredentials([usernamePassword(credentialsId: 'gitops-push',
                                          usernameVariable: 'GIT_USER',
                                          passwordVariable: 'GIT_TOKEN')]) {
          // 토큰을 URL에 넣지 않고 credential helper로 넘긴다 (로그와 .git/config에 남지 않게)
          // pollSCM이 gitops를 감시하지 않도록 git 스텝이 아니라 sh로 clone 한다
          sh '''
            HELPER='!f() { echo "username=$GIT_USER"; echo "password=$GIT_TOKEN"; }; f'
            rm -rf gitops
            git -c credential.helper="$HELPER" clone --depth 1 "$GITOPS_REPO" gitops
            cd gitops

            # 5개 서비스 모두 이번 빌드에서 계산한 태그로 맞춘다 (빠진 갱신이 있어도 다음 빌드가 복구)
            for svc in web auth product order promotion; do
              key=$(echo "$svc" | tr a-z A-Z)
              tag=$(printenv "TAG_$key")
              [ -n "$tag" ] || { echo "TAG_$key 가 비어 있음" >&2; exit 1; }
              sed -i "s/newTag: .*/newTag: \\"$tag\\"/" "apps/$svc/overlays/onprem-dev/kustomization.yaml"
            done

            if git diff --quiet; then
              echo "onprem-dev 태그 변경 없음"
              exit 0
            fi
            git diff

            APP_SHA=$(git -C .. rev-parse --short=7 HEAD)
            git -c user.name="osi7-jenkins" -c user.email="jenkins@osi7.shop" \
              commit -am "chore(onprem-dev): 이미지 태그 갱신 (app $APP_SHA)"

            # 그사이 누가 gitops main에 push했으면 받아서 다시 시도
            for i in 1 2 3; do
              git -c credential.helper="$HELPER" push origin HEAD:main && exit 0
              git -c credential.helper="$HELPER" pull --rebase origin main
            done
            echo "gitops push 실패" >&2
            exit 1
          '''
        }
      }
      post {
        always { sh 'rm -rf gitops' }
      }
    }

    stage('Push ECR') {
      // AWS용 이중 push: Harbor에 올린 같은 이미지에 ECR 이름을 붙여 올린다 (다시 빌드하지 않음)
      // ECR 리포지토리는 EKS를 띄울 때만 생기므로, 없으면 건너뛴다
      // 여기서 실패해도 온프레미스 흐름(Harbor, gitops)은 이미 끝났으므로 빌드를 UNSTABLE로만 표시한다
      environment {
        ECR_REGISTRY       = '918633123181.dkr.ecr.ap-northeast-2.amazonaws.com'
        AWS_DEFAULT_REGION = 'ap-northeast-2'
        AWS_CLI_IMAGE      = 'amazon/aws-cli:2.38.2'   // jenkins-02에 AWS CLI가 없어 컨테이너로 실행
      }
      steps {
        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
          withCredentials([usernamePassword(credentialsId: 'aws-ecr-push',
                                            usernameVariable: 'AWS_ACCESS_KEY_ID',
                                            passwordVariable: 'AWS_SECRET_ACCESS_KEY')]) {
            sh '''
              # 키는 -e 이름만 넘겨서 명령줄과 로그에 값이 남지 않게 한다
              aws() {
                docker run --rm -e AWS_ACCESS_KEY_ID -e AWS_SECRET_ACCESS_KEY -e AWS_DEFAULT_REGION \
                  "$AWS_CLI_IMAGE" "$@"
              }

              # ECR 리포지토리가 없으면 (EKS를 띄우지 않은 날) 건너뛴다. 다른 오류는 실패로 처리
              if ! out=$(aws ecr describe-repositories --repository-names ecommerce/web 2>&1); then
                if echo "$out" | grep -q RepositoryNotFoundException; then
                  echo "ECR 리포지토리가 없어 건너뜀 (EKS 미기동)"
                  exit 0
                fi
                echo "$out" >&2
                exit 1
              fi

              aws ecr get-login-password | docker login "$ECR_REGISTRY" -u AWS --password-stdin

              for svc in web auth product order promotion; do
                key=$(echo "$svc" | tr a-z A-Z)
                tag=$(printenv "TAG_$key")
                src="$REGISTRY/ecommerce/$svc:$tag"
                dst="$ECR_REGISTRY/ecommerce/$svc:$tag"

                # ECR 태그는 IMMUTABLE이라 이미 있으면 건너뛴다
                if aws ecr describe-images --repository-name "ecommerce/$svc" \
                     --image-ids imageTag="$tag" > /dev/null 2>&1; then
                  echo "$svc:$tag ECR에 이미 있음, 건너뜀"
                  continue
                fi

                # 이번 빌드에서 만들지 않은 이미지는 로컬에 없으므로 Harbor에서 받는다
                docker image inspect "$src" > /dev/null 2>&1 || docker pull "$src"
                docker tag "$src" "$dst"
                docker push "$dst"
              done
            '''
          }
        }
      }
      post {
        always { sh 'docker logout "$ECR_REGISTRY" || true' }
      }
    }
  }

  post {
    // 실패, 불안정(지금은 ECR push 실패가 여기에 해당), 실패 후 첫 성공일 때만 알린다
    failure  { script { notifySlack(':x: 빌드 실패') } }
    unstable { script { notifySlack(':warning: 빌드 불안정 (ECR push 등 일부 단계 실패)') } }
    fixed    { script { notifySlack(':white_check_mark: 빌드 복구') } }
    always   { sh 'docker logout "$REGISTRY" || true' }
  }
}
