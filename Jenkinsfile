pipeline {
  agent {
    docker {
      image 'gradle:7.3.3-jdk8'
      args '-v /root/.gradle:/root/.gradle'
    }
  }
  stages {
    stage('Build'){
      steps {
        sh 'gradle clean jar'
      }
      post {
        success {
          archiveArtifacts artifacts: 'build/libs/*.jar', fingerprint: true
        }
      }
    }
  }
}