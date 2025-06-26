pipeline {
  agent any

  environment {
    DOCKER_IMAGE = 'jaeyong36/JYWeb:latest'
  }

  stages {
    stage('Inject Config File') {
      steps {
        configFileProvider([
          configFile(fileId: 'app-properties', targetLocation: 'src/main/resources/application.properties')
        ]) {
          echo 'application.properties injected'
        }
      }
    }

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

          docker run -d --name redis -p 6379:6379 redis

          docker run -d -p 80:8080 --name my-app --link redis:redis jaeyong36/JYWeb:latest
        '''
      }
    }
  }
}
