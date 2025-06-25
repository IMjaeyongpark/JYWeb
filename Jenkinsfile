pipeline {
  agent any
  environment {
    DOCKER_IMAGE = 'jaeyong36/JYWeb:latest'
  }
  stages {
    stage('Build') {
      steps {
        sh './gradlew clean build'
      }
    }
    stage('Docker Build') {
      steps {
        sh "docker build -t $DOCKER_IMAGE ."
      }
    }
    stage('Push to DockerHub') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
          sh "echo $PASS | docker login -u $USER --password-stdin"
          sh "docker push $DOCKER_IMAGE"
        }
      }
    }
    stage('Deploy') {
      steps {
        sh '''
          docker stop my-app || true
          docker rm my-app || true
          docker run -d -p 80:8080 --name my-app jaeyong36/JYWeb:latest
        '''
      }
    }
  }
}
