def call(String name = 'human') {
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