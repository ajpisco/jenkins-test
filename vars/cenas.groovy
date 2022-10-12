def call(String default = '') {
    stages {
        stage("CENAS") {
            steps {
                echo "hello cenas"
            }
        }
    }
}