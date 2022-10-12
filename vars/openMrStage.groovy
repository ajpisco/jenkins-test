def call(String name = '') {
    // pipeline {
    //     agent any
        
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
        }
    // }
}