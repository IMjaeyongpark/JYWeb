pipeline {
  agent any

  environment {
    DOCKER_IMAGE = 'jaeyong36/jyweb:latest'
    JAVA_TOOL_OPTIONS = "-Djava.io.tmpdir=/mnt/big_disk/tmp"
  }

  stages {

    stage('Inject Config File') {
      steps {
        sh 'mkdir -p src/main/resources'
        configFileProvider([
          configFile(fileId: 'app-properties', targetLocation: 'src/main/resources/application.properties')
        ]) {
          echo 'application.properties injected'
        }
      }
    }

    stage('Build') {
      steps {
        sh '''
          echo "===== java version ====="
          java -version
          echo "===== javac version ====="
          javac -version
          ./gradlew clean build
        '''
      }
    }

    stage('Docker Build') {
      steps {
        sh '''
          docker build -t $DOCKER_IMAGE .
        '''
      }
    }

    stage('Push to DockerHub') {
      steps {
        withCredentials([usernamePassword(credentialsId: 'docker-hub-creds', usernameVariable: 'USER', passwordVariable: 'PASS')]) {
          sh '''
            echo $PASS | docker login -u $USER --password-stdin
            docker push $DOCKER_IMAGE
          '''
        }
      }
    }

    stage('Deploy') {
      steps {
        sh '''
          docker stop my-app || true
          docker rm my-app || true
          docker run -d -p 80:8080 --name my-app $DOCKER_IMAGE
        '''
      }
    }
  }
}
