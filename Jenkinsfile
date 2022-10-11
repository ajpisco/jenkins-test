/* groovylint-disable NestedBlockDepth */
def CI_REGISTRY_WORKER_BE_IMAGE = 'worker-be'
def CI_REGISTRY_WORKER_UI_IMAGE = 'worker-ui'
def CI_REGISTRY_INDUSTRY_BE_IMAGE = 'industry-be'
def CI_REGISTRY_INDUSTRY_UI_IMAGE = 'industry-ui'
def CI_REGISTRY_ANCILLARY_IMAGE = 'postman-be'
def CI_REGISTRY_API_IMAGE = 'api-be'
def CI_REGISTRY_LATEST = 'latest'
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

pipeline {
    agent any

    stages {
        stage('openMr') {
            stages {
                stage('open-merge'){
                    when {
                        branch comparator: 'REGEXP', pattern: '^feature\\/*.'
                    }
                    steps {
                        script {
                            // try block to prevent the build to stop in case of error
                            try {
                                buildTrigger()
                                sh 'export GRADLE_USER_HOME=$(pwd)/.gradle'

                                // HOST="${CI_PROJECT_URL}"
                                // CI_PROJECT_ID=${CI_PROJECT_ID}
                                // GITLAB_USER_ID=${GITLAB_USER_ID}
                                // PRIVATE_TOKEN=${PRIVATE_TOKEN}

                                // get the latest commit ref (example: refs/heads/somebranch)
                                CI_COMMIT_REF_NAME = sh(
                                    script: 'git show-ref --heads | cut -d " " -f2-',
                                    returnStdout: true
                                ).trim()
                                sh 'aio/env-scope/auto-merge-request.sh' // The name of the script
                            } catch (err) {
                                echo "Error on ${STAGE_NAME} stage: " + err.getMessage()
                            }
                        }
                    }
                }
            }
        }
        stage('install') {
            stages {
                stage('install-dependencies'){
                    when {
                        branch comparator: 'REGEXP', pattern: '^feature\\/*.'
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
                        anyOf {
                            changeset 'services/core/worker/**/*'
                            changeset 'services/core/worker-public/**/*'
                            changeset 'services/core/commons/**/*'
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
                        anyOf {
                            changeset 'web-apps/projects/worker/**/*'
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
                        anyOf {
                            changeset 'services/core/admin/**/*'
                            changeset 'services/core/industry/**/*'
                            changeset 'services/core/commons/**/*'
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
                        anyOf {
                            changeset 'web-apps/projects/industry/**/*'
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
                        anyOf {
                            changeset 'services/ancillary/**/*'
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
                        anyOf {
                            changeset 'services/core/api/**/*'
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
                        anyOf {
                            changeset 'services/core/api-lighthouse/**/*'
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
                        anyOf {
                            changeset 'plugins/neo4j-mypass/**/*'
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
                anyOf {
                    changeset 'services/shared/**/*'
                    changeset 'services/shared/**/*'
                    changeset 'web-apps/shared-global/**/*'
                }
                not {
                    changeset 'build.gradle'
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



        // stage('test') {
        //     steps {
        //         echo "test ${env.CI_COMMIT_REF_NAME}"
        //     }
        // }
    }
}

void buildTrigger() {
    println currentBuild.getBuildCauses()
    // // started by commit
    // echo currentBuild.getBuildCauses('jenkins.branch.BranchEventCause')
    // // started by timer
    // echo currentBuild.getBuildCauses('hudson.triggers.TimerTrigger$TimerTriggerCause')
    // // started by user
    // echo currentBuild.getBuildCauses('hudson.model.Cause$UserIdCause')
}
