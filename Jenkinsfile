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
    stage('Push Image') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
          sh "echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin"
          sh "docker push $DOCKER_IMAGE"
        }
      }
    }
    stage('Deploy to K8s') {
      steps {
        sh "kubectl apply -f k8s/deployment.yaml"
        sh "kubectl apply -f k8s/service.yaml"
      }
    }
  }
}
