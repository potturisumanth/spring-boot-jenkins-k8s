pipeline{

agent any

tools
{
  maven 'maven3.6.3'

}

triggers{
 pollSCM('* * * * *')
}

options{
    timestamps()
    buildDiscarder(logRotator(artifactDaysToKeepStr: '', artifactNumToKeepStr: '2', daysToKeepStr: '', numToKeepStr: '2'))
}

 stages{
   
   stage('CheckoutCode')
   {
     steps{
	 git branch: 'master', credentialsId: '8e348aa2-9531-4dbe-863d-1adb64897470', url: 'https://github.com/potturi319/spring-mongoDb-K8s-jenkins.git'
	 }
   }
   
   stage('Build')
   {
    steps{
	  sh "mvn clean package"
	 }
   }
   
    stage('Build Docker image')
   {
     steps{
	   sh 'docker build -t potturi319/spring-boot-mongo:$BUILD_NUMBER .'
	 }
   }
   
   stage('UploadArtifactintoNexus')
   {
     steps{
	  sh "mvn deploy"
	 }
   }
   
  stage('Push image to docker registry')
  {
   steps{
   withCredentials([string(credentialsId: 'DOKCER_HUB_PASSWORD', variable: 'DOKCER_HUB_PASSWORD')]) {
          sh "docker login -u potturi319 -p ${DOKCER_HUB_PASSWORD}"
        }
        sh 'docker push potturi319/spring-boot-mongo:$BUILD_NUMBER'
 }
 }
	stage ('change tag name in spring deployment file') {
		steps {
			sh 'sed -i 's/Bulid/${BUILD_NUMBER}/g' spring-boot-mongo.yml'

		}
	
	}

	stage('UploadArtifactintoNexus')
   {
     steps{
	  sh "kubectl apply -f spring-boot-mongo.yml"
	 }
   }
 
 } 
 
 
 
 post{
 
 
 success{
 mail bcc: 'potturi2106@gmail.com', body: '''BuildOver!....

 Regards,
 Sumanth Potturi,
 +37125605971''', cc: 'potturi2106@gmail.com', from: '', replyTo: '', subject: 'BuildOver!!', to: 'potturi2106@gmail.com'
 }
 
 failure{
 mail bcc: 'potturi2106@gmail.com', body: '''BuildOver!....

 Regards,
 Sumanth Potturi,
 +37125605971''', cc: 'potturi2106@gmail.com', from: '', replyTo: '', subject: 'BuildOver!!', to: 'potturi2106@gmail.com'
 }
 
 }
}
