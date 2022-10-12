def call() {
  pipeline {
       agent any
       stages {
           stage("CENAS") {
               steps {
                   echo "hello cenas"
               }
           }
       }
   }
}