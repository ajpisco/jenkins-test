def CI_REGISTRY_WORKER_BE_IMAGE = 'worker-be'
def CI_REGISTRY_WORKER_UI_IMAGE = 'worker-ui'
def CI_REGISTRY_INDUSTRY_BE_IMAGE = 'industry-be'
def CI_REGISTRY_INDUSTRY_UI_IMAGE = 'industry-ui'
def CI_REGISTRY_ANCILLARY_IMAGE = 'postman-be'
def CI_REGISTRY_API_IMAGE = 'api-be'
def CI_REGISTRY_LATEST = 'latest'
def CI_REGISTRY = ''
def CI_REGISTRY_NAMESPACE = ''
def CI_COMMIT_REF_SLUG = slugify(BRANCH_NAME)
def CI_COMMIT_REF_NAME = BRANCH_NAME
def CI_COMMIT_SHORT_SHA = ''
// def K8_CLUSTER_NAME = "mgd"
def K8_DEV_CLUSTER_NAME = 'mgd'
def K8_DEV_CLUSTER_NAMESPACE = 'dev-ns'
def K8_DEV_CLUSTER_STATE = 'mgd-mypass-com'
def K8_SIT_CLUSTER_NAME = 'mgd'
def K8_SIT_CLUSTER_NAMESPACE = 'sit-ns'
// def K8_SIT_CLUSTER_STATE = "mgd-mypass-com"
def K8_QA_CLUSTER_NAME = 'mgd'
def K8_QA_CLUSTER_NAMESPACE = 'qa-ns'
// def K8_QA_CLUSTER_STATE = "mgd-mypass-com"
def K8_PIH_CLUSTER_NAME = 'mgu'
def K8_PIH_CLUSTER_NAMESPACE = 'pih-ns'
// def K8_PIH_CLUSTER_STATE = "mgu-mypass-com"
def K8_MIA_CLUSTER_NAME = 'mgd'
def K8_MIA_CLUSTER_NAMESPACE = 'mia-ns'
// def K8_MIA_CLUSTER_STATE = "mgd-mypass-com"
def K8_EAP_CLUSTER_NAME = 'mgu'
def K8_EAP_CLUSTER_NAMESPACE = 'eap-ns'
// def K8_EAP_CLUSTER_STATE = "mgu-mypass-com"
def K8_TSB_CLUSTER_NAME = 'mgu'
def K8_TSB_CLUSTER_NAMESPACE = 'tsb-ns'
// def K8_TSB_CLUSTER_STATE = "mgu-mypass-com"
def K8_UAT_CLUSTER_NAME = 'mgu'
def K8_UAT_CLUSTER_NAMESPACE = 'uat-ns'
// def K8_UAT_CLUSTER_STATE = "mgu-mypass-com"
def K8_BHP1B_UAT_CLUSTER_NAME = 'mgu'
def K8_BHP1B_UAT_CLUSTER_NAMESPACE = 'bhp1buat-ns'
// def K8_BHP1B_UAT_CLUSTER_STATE = "mgu-mypass-com"
def K8_UAT2_CLUSTER_NAME = 'mgu'
def K8_UAT2_CLUSTER_NAMESPACE = 'uat2-ns'
// def K8_UAT2_CLUSTER_STATE = "mgu-mypass-com"
def K8_MGU_CLUSTER_NAME = 'mgu'
def K8_MGU_CLUSTER_NAMESPACE = 'mgu-ns'
// def K8_MGU_CLUSTER_STATE = "mgu-mypass-com"
def K8_DEV2_CLUSTER_NAME = 'mgu'
def K8_DEV2_CLUSTER_NAMESPACE = 'dev2-ns'
// def K8_DEV2_CLUSTER_STATE = "mgu-mypass-com"
def K8_BHPUAT_CLUSTER_NAME = 'mgu'
def K8_BHPUAT_CLUSTER_NAMESPACE = 'bhpuat-ns'
// def K8_BHPUAT_CLUSTER_STATE = "mgu-mypass-com"
def K8_MGP_CLUSTER_NAME = 'mgp'
def K8_MGP_CLUSTER_NAMESPACE = 'mgp-ns'
// def K8_MGP_CLUSTER_STATE = "mgp-mypass-com"
def K8_KUBECTL_VERSION = '1.1.7'

// @Library('dummy') _
// openMrStage ''

pipeline {
    agent any

    stages {
        stage('openMr') {
            stages {
                stage('open-merge'){
                    when {
                        // Run stage if branch match the regex pattern
                        branch comparator: 'REGEXP', pattern: '^feature\\/*.'
                    }
                    steps {
                        script {
                            // try block to prevent the build to stop in case of error
                            try {
                                sh 'printenv'

                                sh 'export GRADLE_USER_HOME=$(pwd)/.gradle'

                                // HOST="${CI_PROJECT_URL}"
                                // CI_PROJECT_ID=${CI_PROJECT_ID}
                                // GITLAB_USER_ID=${GITLAB_USER_ID}
                                // PRIVATE_TOKEN=${PRIVATE_TOKEN}

                                sh "export CI_COMMIT_SHORT_SHA="+ GIT_COMMIT.substring(0,7)

                                sh 'aio/env-scope/auto-merge-request.sh' // The name of the script
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                            }
                        }
                    }
                }
            }
            post {
                always {
                    echo "post stage"
                }
            }
        }
        
        stage('install') {
            stages {
                stage('install-dependencies'){
                    when {
                        anyOf{
                            // Run stage if build was triggered by user or push
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }
                    }
                    steps {
                        script {
                            try {
                                sh 'cd web-apps'
                                sh(
                                    script: 'npm install',
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
            }
        }

        stage('format-check'){
            steps {
                script {
                    try {
                        sh 'cd web-apps'
                        sh(
                            script: 'npm run "format:check"',
                            returnStdout: true
                        ).trim()
                    } catch (err) {
                        echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                    }
                }
            }
        }

        stage('package') {
            when {
                not {
                    anyOf {
                        changeset 'build.gradle'
                        changeset 'services/shared/**/*'
                        changeset 'web-apps/projects/shared/**/*'
                        changeset 'web-apps/shared-global/**/*'
                    }
                }
            }
            stages {
                stage('package-worker-be'){
                    when {
                        allOf{
                            anyOf {
                                changeset 'services/core/worker/**/*'
                                changeset 'services/core/worker-public/**/*'
                                changeset 'services/core/commons/**/*'
                            }
                            anyOf {
                                triggeredBy cause: "UserIdCause"
                                triggeredBy cause: "BranchEventCause"
                            }
                        }
                    }
                    steps {
                        script {
                            try {
                                sh(
                                    script: 'aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 591674360001.dkr.ecr.ap-southeast-2.amazonaws.com',
                                    returnStdout: true
                                ).trim()
                                
                                sh(
                                    script: 'gradle :services:core:worker:build -DskipTests=true -Dcheckstyle.skip=true -x test',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: 'gradle :services:core:worker-public:build -DskipTests=true -Dcheckstyle.skip=true -x test',
                                    returnStdout: true
                                ).trim()
                                
                                sh 'cd aio/env-scope'
                                sh(
                                    script: 'docker-compose -f docker-compose-all.yml build worker-be',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                          
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('package-worker-ui'){
                    when {
                        allOf{
                            anyOf {
                                changeset 'web-apps/projects/worker/**/*'
                            }
                            anyOf {
                                triggeredBy cause: "UserIdCause"
                                triggeredBy cause: "BranchEventCause"
                            }
                        }
                    }
                    steps {
                        script {
                            try {
                                sh(
                                    script: 'aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 591674360001.dkr.ecr.ap-southeast-2.amazonaws.com',
                                    returnStdout: true
                                ).trim()
                                
                                sh(
                                    script: 'gradle :web-apps:buildWorker',
                                    returnStdout: true
                                ).trim()
                                
                                sh 'cd aio/env-scope'
                                sh(
                                    script: 'docker-compose -f docker-compose-all.yml build worker-ui',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                          
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('package-industry-be'){
                    when {
                        allOf{
                            anyOf {
                                changeset 'services/core/admin/**/*'
                                changeset 'services/core/industry/**/*'
                                changeset 'services/core/commons/**/*'
                            }
                            anyOf {
                                triggeredBy cause: "UserIdCause"
                                triggeredBy cause: "BranchEventCause"
                            }
                        }
                    }
                    steps {
                        script {
                            try {
                                sh(
                                    script: 'aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 591674360001.dkr.ecr.ap-southeast-2.amazonaws.com',
                                    returnStdout: true
                                ).trim()
                                
                                sh(
                                    script: 'gradle :services:core:industry:build -DskipTests=true -Dcheckstyle.skip=true -x test',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: 'gradle :services:core:admin:build -DskipTests=true -Dcheckstyle.skip=true -x test',
                                    returnStdout: true
                                ).trim()
                                
                                sh 'cd aio/env-scope'
                                sh(
                                    script: 'docker-compose -f docker-compose-all.yml build industry-be',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                          
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('package-industry-ui'){
                    when {
                        allOf{
                            anyOf {
                                changeset 'web-apps/projects/industry/**/*'
                            }
                            anyOf {
                                triggeredBy cause: "UserIdCause"
                                triggeredBy cause: "BranchEventCause"
                            }
                        }
                    }
                    steps {
                        script {
                            try {
                                sh(
                                    script: 'aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 591674360001.dkr.ecr.ap-southeast-2.amazonaws.com',
                                    returnStdout: true
                                ).trim()
                                
                                sh(
                                    script: 'gradle :web-apps:buildIndustry',
                                    returnStdout: true
                                ).trim()
                                
                                sh 'cd aio/env-scope'
                                sh(
                                    script: 'docker-compose -f docker-compose-all.yml build industry-ui',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                          
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('package-ancillary'){
                    when {
                        allOf{
                            anyOf {
                                changeset 'services/ancillary/**/*'
                            }
                            anyOf {
                                triggeredBy cause: "UserIdCause"
                                triggeredBy cause: "BranchEventCause"
                            }
                        }
                    }
                    steps {
                        script {
                            try {
                                sh(
                                    script: 'aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 591674360001.dkr.ecr.ap-southeast-2.amazonaws.com',
                                    returnStdout: true
                                ).trim()
                                
                                sh(
                                    script: 'gradle :services:ancillary:messaging:build -DskipTests=true -Dcheckstyle.skip=true -x test',
                                    returnStdout: true
                                ).trim()
                                
                                sh 'cd aio/env-scope'
                                sh(
                                    script: 'docker-compose -f docker-compose-all.yml build ancillary',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                          
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('package-api'){
                    when {
                        allOf{
                            anyOf {
                                changeset 'services/core/api/**/*'
                            }
                            anyOf {
                                triggeredBy cause: "UserIdCause"
                                triggeredBy cause: "BranchEventCause"
                            }
                        }
                    }
                    steps {
                        script {
                            try {
                                sh(
                                    script: 'aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 591674360001.dkr.ecr.ap-southeast-2.amazonaws.com',
                                    returnStdout: true
                                ).trim()
                                sh 'rm -rf aio/env-scope/service/*.* aio/env-scope/web-apps/*.* aio/env-scope/api-docs/*.*'
                                
                                sh(
                                    script: 'gradle :services:core:api:build -DskipTests=true -Dcheckstyle.skip=true -x test',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: 'gradle :services:core:api:generateOpenApiDocs',
                                    returnStdout: true
                                ).trim()
                                
                                sh 'cd aio/env-scope'
                                sh(
                                    script: 'docker-compose -f docker-compose-all.yml build api',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_API_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_API_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_API_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_API_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_API_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_API_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                          
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('package-lighthouse'){
                    when {
                        allOf{
                            anyOf {
                                changeset 'services/core/api-lighthouse/**/*'
                            }
                            triggeredBy cause: "BranchEventCause"
                        }
                    }
                    steps {
                        script {
                            try {
                                sh(
                                    script: 'aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 591674360001.dkr.ecr.ap-southeast-2.amazonaws.com',
                                    returnStdout: true
                                ).trim()
                                sh 'rm -rf aio/env-scope/services/core/api-lighthouse/*.* aio/env-scope/api-lighthouse-docs/*.*'
                                
                                sh(
                                    script: 'gradle :services:core:api-lighthouse:build -DskipTests=true -Dcheckstyle.skip=true -x test',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: 'gradle :services:core:api-lighthouse:generateOpenApiDocs',
                                    returnStdout: true
                                ).trim()
                                
                                sh 'cd aio/env-scope'
                                sh(
                                    script: 'docker-compose -f docker-compose-all.yml build api-lighthouse',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_LIGHTHOUSE_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_LIGHTHOUSE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker tag ${CI_REGISTRY_LIGHTHOUSE_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_LIGHTHOUSE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_LIGHTHOUSE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_LIGHTHOUSE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                                    returnStdout: true
                                ).trim()
                          
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('package-plugins'){
                    when {
                        allOf{
                            anyOf {
                                changeset 'plugins/neo4j-mypass/**/*'
                            }
                            anyOf {
                                triggeredBy cause: "UserIdCause"
                                triggeredBy cause: "BranchEventCause"
                            }
                        }
                    }
                    steps {
                        script {
                            try {
                                sh 'rm -rf aio/env-scope/api-docs/*.*'
                                
                                sh(
                                    script: 'gradle :plugins:neo4j-mypass:build -DskipTests=true -Dcheckstyle.skip=true -x test',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: 'gradle publish -Ds3_plugin_location=s3://plugins.example.com -Dplugin_version=$CI_COMMIT_REF_SLUG-$CI_COMMIT_SHORT_SHA -DAWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -DAWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY',
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: 'gradle publish -Ds3_plugin_location=s3://plugins.example.com -Dplugin_version=$CI_COMMIT_REF_SLUG-$CI_REGISTRY_LATEST -DAWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -DAWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY',
                                    returnStdout: true
                                ).trim()
                          
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
            }
        }

        stage('package-all-shared'){
            when {
                allOf{
                    anyOf {
                        changeset 'services/shared/**/*'
                        changeset 'web-apps/projects/shared/**/*'
                        changeset 'web-apps/shared-global/**/*'
                    }
                    anyOf {
                        triggeredBy cause: "BranchEventCause"
                    }
                    not {
                        changeset 'build.gradle'
                    }
                }
            }
            steps {
                script {
                    try {
                        sh(
                            script: 'aws ecr get-login-password --region ap-southeast-2 | docker login --username AWS --password-stdin 591674360001.dkr.ecr.ap-southeast-2.amazonaws.com',
                            returnStdout: true
                        ).trim()
                        
                        sh(
                            script: 'gradle build --profile -DskipTests=true -Dcheckstyle.skip=true -x test',
                            returnStdout: true
                        ).trim()
                        sh(
                            script: 'gradle :services:core:api:generateOpenApiDocs',
                            returnStdout: true
                        ).trim()
                        sh(
                            script: 'gradle publish -Ds3_plugin_location=s3://plugins.example.com -Dplugin_version=$CI_COMMIT_REF_SLUG-$CI_COMMIT_SHORT_SHA -DAWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -DAWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY',
                            returnStdout: true
                        ).trim()
                        sh(
                            script: 'gradle publish -Ds3_plugin_location=s3://plugins.example.com -Dplugin_version=$CI_COMMIT_REF_SLUG-$CI_REGISTRY_LATEST -DAWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID -DAWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY',
                            returnStdout: true
                        ).trim()
                        
                        sh 'cd aio/env-scope'
                        sh(
                            script: 'docker-compose -f docker-compose-all.yml build --force-rm --no-cache',
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_WORKER_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_BE_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_INDUSTRY_UI_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_ANCILLARY_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_API_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_API_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker tag ${CI_REGISTRY_API_IMAGE}:${CI_REGISTRY_LATEST} ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_API_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_API_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
                            returnStdout: true
                        ).trim()
                        sh(
                            script: "docker push ${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/${CI_REGISTRY_API_IMAGE}:${CI_COMMIT_REF_SLUG}-${CI_REGISTRY_LATEST}",
                            returnStdout: true
                        ).trim()
                    
                    } catch (err) {
                        echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                        // throw err
                    }
                }
            }
        }

        stage('test-ui') {
            stages {
                stage('pre-test-ui'){
                    steps {
                        script {
                            // sh(
                            //     script: "apk add chromium",
                            //     returnStdout: true
                            // ).trim()
                            sh 'export CHROME_BIN=/usr/bin/chromium-browser'
                        }
                    }
                }
                stage('test-industry'){
                    when {
                        allOf {
                            changeset 'web-apps/projects/industry/**/*'
                            triggeredBy cause: "BranchEventCause"
                        }
                    }
                    steps {
                        script {
                            try {
                                sh 'cd web-apps'
                                sh(
                                    script: "npm run test-industry -- --no-watch --no-progress --browsers=ChromeHeadlessCI",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                            }
                        }
                    }
                }
                stage('test-worker'){
                    when {
                        allOf {
                            changeset 'web-apps/projects/worker/**/*'
                            triggeredBy cause: "BranchEventCause"
                        }
                    }
                    steps {
                        script {
                            try {
                                sh 'cd web-apps'
                                sh(
                                    script: "npm run test-worker -- --no-watch --no-progress --browsers=ChromeHeadlessCI",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                            }
                        }
                    }
                }
            }
        }
        
        stage('clean') {
            // agent {
            //     docker {
            //         image "${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/kubectl:${K8_KUBECTL_VERSION}"
            //     }
            // }
            stages {
                stage('clean_deployment'){
                    when {
                        allOf{
                            anyOf {
                                branch comparator: 'REGEXP', pattern: '^feature[\\/\\#\\-0-9A-Za-z+]+$'
                                branch 'develop'
                            }
                            triggeredBy cause: "UserIdCause"
                        }                      
                    }
                    steps {
                        script {
                            try {
                                sh 'export GRADLE_USER_HOME=$(pwd)/.gradle'
                                echo "Cleaning development environment"
                                sh(
                                    script: "kops export kubecfg --name ${K8_DEV_CLUSTER_NAME} --state=s3://${K8_DEV_CLUSTER_STATE}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "kubectl delete statefulset neo4j-dev -n ${K8_DEV_CLUSTER_NAMESPACE} --ignore-not-found=true",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "kubectl delete pvc neo4j-pv-neo4j-dev-0 -n ${K8_DEV_CLUSTER_NAMESPACE} --ignore-not-found=true",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "cat aio/env-scope/control/develop/neo4j.yaml | sed \"s/\\${VERSION}/${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}/g\" | kubectl apply -n ${K8_DEV_CLUSTER_NAMESPACE} -f -",
                                    returnStdout: true
                                ).trim()
                                sh "sleep 120"
                                sh(
                                    script: "aws s3 cp s3://storage.example.com/ultron/dbstore/neo4j-dbk/neo4j-backup.zip .",
                                    returnStdout: true
                                ).trim()
                                sh "unzip neo4j-backup.zip"
                                sh(
                                    script: "kubectl cp neo4j-backup neo4j-dev-0:/var/lib/neo4j/ -n vision",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "kubectl exec neo4j-dev-0 -n vision  -- bash -c \"rm -rf /var/lib/neo4j/data/databases/graph.db/* && cp -r /var/lib/neo4j/neo4j-backup/* /var/lib/neo4j/data/databases/graph.db/ && chown -R neo4j:neo4j /var/lib/neo4j/data/databases/graph.db && rm -rf /var/lib/neo4j/neo4j-backup\"",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "kubectl delete statefulset neo4j-dev -n ${K8_DEV_CLUSTER_NAMESPACE} --ignore-not-found=true",
                                    returnStdout: true
                                ).trim()
                                // sh(
                                //     script: "kubectl create job --from=cronjob/omni-backup cj-${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} -n ${K8_DEV_CLUSTER_NAMESPACE}",
                                //     returnStdout: true
                                // ).trim()
                                // sh(
                                //     script: "bash aio/env-scope/job-completion-status.sh cj-${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_DEV_CLUSTER_NAMESPACE}",
                                //     returnStdout: true
                                // ).trim()
                                // sh(
                                //     script: "kubectl delete jobs cj-${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} -n ${K8_DEV_CLUSTER_NAMESPACE}",
                                //     returnStdout: true
                                // ).trim()
                                // sh(
                                //     script: "aws s3 rm s3://mypass-dev-public/industry/ --recursive",
                                //     returnStdout: true
                                // ).trim()
                                // sh(
                                //     script: "aws s3 rm s3://mypass-dev-public/workers/ --recursive",
                                //     returnStdout: true
                                // ).trim()
                                // sh(
                                //     script: "aws s3 rm s3://mypass-dev-private/ --recursive",
                                //     returnStdout: true
                                // ).trim()
                                // sh(
                                //     script: "bash aio/env-scope/clean-cognito-pool.sh develop ap-southeast-2_jDLHi2kpE",
                                //     returnStdout: true
                                // ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
            }
        }
        
        stage('backup') {
            // agent {
            //     docker {
            //         image "${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/kubectl:${K8_KUBECTL_VERSION}"
            //     }
            // }
            stages {
                stage('backup_production'){
                    when {
                        
                        allOf{
                            branch 'master'
                            triggeredBy cause: "UserIdCause"
                        }          
                    }
                    steps {
                        script {
                            try {
                                sh "export AWS_ACCESS_KEY_ID=${MGP_AWS_ACCESS_KEY_ID}"
                                sh "export AWS_SECRET_ACCESS_KEY=${MGP_AWS_SECRET_ACCESS_KEY}"
                                sh(
                                    script: "aws sts get-caller-identity",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "aws eks --region ${AWS_DEFAULT_REGION} update-kubeconfig --name ${K8_MGP_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                                echo "Backing up production evironment"
                                sh(
                                    script: "kubectl create job --from=cronjob/omni-backup backup-job-${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} -n ${K8_MGP_CLUSTER_NAMESPACE}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "bash job-completion-status.sh backup-job-${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_MGP_CLUSTER_NAMESPACE}",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "kubectl delete jobs backup-job-${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} -n ${K8_MGP_CLUSTER_NAMESPACE}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
            }
        }
        
        stage('deploy') {
            // agent {
            //     docker {
            //         image "${CI_REGISTRY}/${CI_REGISTRY_NAMESPACE}/kubectl:${K8_KUBECTL_VERSION}"
            //     }
            // }
            stages {
                stage('pre-deploy'){
                    steps {
                        script {
                            sh "cd aio/env-scope"
                        }
                    }
                }
                stage('deploy_develop'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "development"
                                url = "https://showcase.develop.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_DEV_CLUSTER_NAMESPACE} ${K8_DEV_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_DEV_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_staging'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "staging"
                                url = "https://showcase.sit.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_SIT_CLUSTER_NAMESPACE} ${K8_SIT_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_SIT_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_qa'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "qa"
                                url = "https://showcase.qa.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_QA_CLUSTER_NAMESPACE} ${K8_QA_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_QA_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_pih'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "pih"
                                url = "https://showcase.pih.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_PIH_CLUSTER_NAMESPACE} ${K8_PIH_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_PIH_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_mia'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "mia"
                                url = "https://showcase.mia.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_MIA_CLUSTER_NAMESPACE} ${K8_MIA_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_MIA_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_eap'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "eap"
                                url = "https://showcase.eap.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_EAP_CLUSTER_NAMESPACE} ${K8_EAP_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_EAP_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_tsb'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "Training and Sandbox"
                                url = "https://industry.tsb.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_TSB_CLUSTER_NAMESPACE} ${K8_TSB_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_TSB_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_uat'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "uat"
                                url = "https://mypass.uat.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_UAT_CLUSTER_NAMESPACE} ${K8_UAT_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_UAT_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_BHP-SIT'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "BHP Pilot Environment"
                                url = "https://industry.bhp1buat.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_BHP1B_UAT_CLUSTER_NAMESPACE} ${K8_BHP1B_UAT_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_BHP1B_UAT_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_BHP-UAT'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "BHP UAT Environment"
                                url = "https://industry.bhpuat.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_BHPUAT_CLUSTER_NAMESPACE} ${K8_BHPUAT_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_BHPUAT_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_UAT2'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "UAT2 Environment"
                                url = "https://industry.uat2.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_UAT2_CLUSTER_NAMESPACE} ${K8_UAT2_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_UAT2_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_mgu'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "MGU"
                                url = "https://industry.mgu.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_MGU_CLUSTER_NAMESPACE} ${K8_MGU_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_MGU_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_BHP-DEV'){
                    when {
                        anyOf {
                            triggeredBy cause: "UserIdCause"
                            triggeredBy cause: "BranchEventCause"
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "BHP DEV Environment"
                                url = "https://industry.dev2.example.com"
                                sh(
                                    script: "bash deploy.sh dev ${K8_DEV2_CLUSTER_NAMESPACE} ${K8_DEV2_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_DEV2_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }
                stage('deploy_mgp'){
                    when {
                        allOf {
                            triggeredBy cause: "UserIdCause"
                            branch 'master'
                        }           
                    }
                    steps {
                        script {
                            try {
                                name = "Production-MGP"
                                url = "https://mypass.example.com"
                                echo "Deploying to production evironment"
                                sh(
                                    script: "aws s3 ls \"s3://plugins.example.com/com/mypass/neo4j-mypass/${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}/neo4j-mypass-${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}.jar\" && aws s3 cp s3://plugins.example.com/com/mypass/neo4j-mypass/${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}/neo4j-mypass-${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}.jar s3://plugins.example.com/com/mypass/neo4j-mypass/latest/neo4j-mypass-latest.jar",
                                    returnStdout: true
                                ).trim()
                                sh(
                                    script: "bash deploy.sh mgp ${K8_MGP_CLUSTER_NAMESPACE} ${K8_MGP_CLUSTER_NAME} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
                                    returnStdout: true
                                ).trim()
                                sh 'sleep 90'
                                sh(
                                    script: "bash deploy-apigateway.sh dev ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA} ${K8_MGP_CLUSTER_NAME}",
                                    returnStdout: true
                                ).trim()
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                                // throw err
                            }
                        }
                    }
                }

//                 // stage('deploy_production'){
//                 //     when {
//                 //         allOf {
//                 //             triggeredBy cause: "UserIdCause"
//                 //             branch 'master'
//                 //         }           
//                 //     }
//                 //     steps {
//                 //         script {
//                 //             try {
//                 //                 name = "production"
//                 //                 url = "https://mypass.example.com"
//                 //                 echo "Deploy to production evironment"
//                 //                 sh(
//                 //                     script: "aws s3 cp s3://plugins.example.com/com/mypass/neo4j-mypass/${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}/neo4j-mypass-${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}.jar s3://plugins.example.com/com/mypass/neo4j-mypass/latest/neo4j-mypass-latest.jar",
//                 //                     returnStdout: true
//                 //                 ).trim()
//                 //                 sh(
//                 //                     script: "kops export kubecfg --name ${K8_PRO_CLUSTER_NAME} --state=s3://${K8_PRO_CLUSTER_STATE}",
//                 //                     returnStdout: true
//                 //                 ).trim()
//                 //                 sh(
//                 //                     script: "bash deploy.sh ${K8_PRO_CLUSTER_NAME} production ${K8_PRO_CLUSTER_NAMESPACE} ${CI_COMMIT_REF_SLUG} ${CI_COMMIT_REF_NAME} ${CI_COMMIT_SHORT_SHA}",
//                 //                     returnStdout: true
//                 //                 ).trim()
//                 //                 sh 'sleep 90'

//                 //                 sh(
//                 //                     script: "bash deploy-apigateway.sh pro ${CI_COMMIT_REF_SLUG}-${CI_COMMIT_SHORT_SHA}",
//                 //                     returnStdout: true
//                 //                 ).trim()
//                 //             } catch (err) {
//                 //                 echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
//                 //                 // throw err
//                 //             }
//                 //         }
//                 //     }
//                 // }

            }
        }
    }
}


String slugify(String origin){
    String slug = origin.toLowerCase()
    slug = slug.replaceAll("[^\\w\\s-/]", "")
    slug = slug.replaceAll("[\\s/_-]+", "-")
    slug = slug.replaceAll("^-+|-+\$", "")

    return slug
}