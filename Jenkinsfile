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
          docker stop my-app redis || true
          docker rm my-app redis || true

          # Redis 실행
          docker run -d --name redis -p 6379:6379 redis

          # Spring Boot 앱 실행 (같은 네트워크 사용하면 더 안전)
          docker run -d -p 80:8080 --name my-app --link redis:redis jaeyong36/JYWeb:latest
        '''
      }
    }
  }
}
