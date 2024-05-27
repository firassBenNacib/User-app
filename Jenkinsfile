pipeline {
    agent any
    
     environment {
        DOCKER_CREDENTIALS_ID = 'docker-hub-creds'  
        DOCKER_IMAGE_NAME = 'firaskill12/user-app-Prod'
        IMAGE_TAG = 'latest'
}
    stages {
        stage('CheckOut') {
            steps {
                git branch: 'Prod', url: 'https://github.com/firassBenNacib/User-app.git', credentialsId: 'github-creds'
            }
        }
        
        stage('Compile') {
            steps {
                sh "mvn clean compile"
            }
        }
        
        stage('Build') {
            steps {
                sh "mvn package"
            }
        }
        
        stage('Test') {
            steps {
                sh "mvn test jacoco:report"
            }
        }
        
             stage('File System Scan') {
            steps {
                sh "trivy fs --format table -o trivy-fs-report.html ."
            }
        }
        
        stage('SonarQube Analysis') {
            steps {
                script {
                    withSonarQubeEnv(credentialsId: 'jenkins-sonarqube-token') { 
                        sh "mvn sonar:sonar"
                    }
                }    
            }
        }
        
        stage('Quality Gate') {
            steps {
                timeout(time: 4, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }
        
          stage("Deploy to Nexus") {
            steps {
                sh "mvn deploy"
            }
        }

    }
        
    }