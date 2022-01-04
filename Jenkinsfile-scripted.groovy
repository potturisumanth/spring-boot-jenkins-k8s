node{
     
    stage('SCM Checkout'){
        git credentialsId: 'GIT_CREDENTIALS', url:  'https://github.com/potturi319/spring-mongoDb-K8s-jenkins.git', branch: 'master'
    }
    
    stage(" Maven Clean Package"){
      def mavenHome =  tool name: "Maven-3.6.1", type: "maven"
      def mavenCMD = "${mavenHome}/bin/mvn"
      sh "${mavenCMD} clean package"
      
    } 
    
    
    stage('Build Docker Image'){
        sh 'docker build -t potturi319/spring-boot-mongo:$BUILD_NUMBER .'
    }
    
    stage('Push Docker Image'){
        withCredentials([string(credentialsId: 'DOKCER_HUB_PASSWORD', variable: 'DOKCER_HUB_PASSWORD')]) {
          sh "docker login -u potturi319 -p ${DOKCER_HUB_PASSWORD}"
        }
        sh 'docker push potturi319/spring-boot-mongo:$BUILD_NUMBER'
     }
     stage ("change image tag name in spring-boot-mongo file") {

	sh 'sed -i 's/Bulid/${BUILD_NUMBER}/g' spring-boot-mongo.yml'

     }
     
  /**   stage("Deploy To Kuberates Cluster"){
       kubernetesDeploy(
         configs: 'springBootMongo.yml', 
         kubeconfigId: 'KUBERNATES_CONFIG',
         enableConfigSubstitution: true
        )
     } **/
	 
	  
      stage("Deploy To Kuberates Cluster"){
        sh 'kubectl apply -f spring-boot-mongo.yml'
      } 
     
}

