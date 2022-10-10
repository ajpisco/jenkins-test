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

def GRADLE_USER_HOME="${env.WORKSPACE}/.gradle"

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
                                // HOST="${CI_PROJECT_URL}"
                                // CI_PROJECT_ID=${CI_PROJECT_ID}
                                // GITLAB_USER_ID=${GITLAB_USER_ID
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
                                throw err
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
                            echo "${STAGE_NAME}"
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
                            echo "${STAGE_NAME}"
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
                            echo "${STAGE_NAME}"
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
                            echo "${STAGE_NAME}"
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
                            echo "${STAGE_NAME}"
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
                            echo "${STAGE_NAME}"
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
                            echo "${STAGE_NAME}"
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
                            echo "${STAGE_NAME}"
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

String getGitBranchName() {
    return sh(returnStdout: true, script: 'git rev-parse --abbrev-ref HEAD').trim()
}
