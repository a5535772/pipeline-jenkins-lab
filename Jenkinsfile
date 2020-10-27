pipeline {
    agent any
    environment {
        BUILD_USER_EMAIL = "214529283@qq.com"
        BUILD_USER = "li.zhang"
    }

    parameters {
        string(name: 'pomPath', defaultValue: 'pom.xml', description: 'pom.xml的相对路径')
        string(name: 'minLineCoverage', defaultValue: '20', description: '单元测试代码覆盖率要求(%)，如果任何一个维度的当前覆盖率小于最小覆盖率阈值，则构建状态为失败！')
        string(name: 'maxLineCoverage', defaultValue: '50', description: '单元测试代码覆盖率要求(%)，如果当前覆盖率在最大阈值和最小阈值之间，则当前构建状态为不稳定,如果当前覆盖率大于最大阈值，则构建成功！')
    }

    //pipeline运行结果通知给触发者
    post {
        success {
            script {
                wrap([$class: 'BuildUser']) {
                    mail to: "${BUILD_USER_EMAIL}",
                            subject: "PineLine '${JOB_NAME}' (${BUILD_NUMBER}) result",
                            body: "${BUILD_USER}'s pineline '${JOB_NAME}' (${BUILD_NUMBER}) run success\n请及时前往${env.BUILD_URL}进行查看"
                }
            }
        }
        failure {
            script {
                wrap([$class: 'BuildUser']) {
                    mail to: "${BUILD_USER_EMAIL}",
                            subject: "PineLine '${JOB_NAME}' (${BUILD_NUMBER}) result",
                            body: "${BUILD_USER}'s pineline  '${JOB_NAME}' (${BUILD_NUMBER}) run failure\n请及时前往${env.BUILD_URL}进行查看"
                }
            }

        }
        unstable {
            script {
                wrap([$class: 'BuildUser']) {
                    mail to: "${BUILD_USER_EMAIL}",
                            subject: "PineLine '${JOB_NAME}' (${BUILD_NUMBER})结果",
                            body: "${BUILD_USER}'s pineline '${JOB_NAME}' (${BUILD_NUMBER}) run unstable\n请及时前往${env.BUILD_URL}进行查看"
                }
            }
        }
        aborted {
            script {
                wrap([$class: 'BuildUser']) {
                    echo 'I will always say Hello again!'
                    mail to: "${BUILD_USER_EMAIL}",
                            subject: "PineLine '${JOB_NAME}' (${BUILD_NUMBER})结果",
                            body: "${BUILD_USER}'s pineline '${JOB_NAME}' (${BUILD_NUMBER}) run unstable\n请及时前往${env.BUILD_URL}进行查看"
                }
            }
        }
    } 
	stages {
		stage('单元测试') {
            steps {
                echo "starting unitTest......"
                echo "BUILD_USER_EMAIL:${BUILD_USER_EMAIL}"
                echo "JOB_NAME:${JOB_NAME}"
                echo "BUILD_NUMBER:${BUILD_NUMBER}"
                echo "BUILD_USER:${BUILD_USER}"
                echo "JOB_NAME:${JOB_NAME}"
                echo "BUILD_NUMBER:${BUILD_NUMBER}"
                //注入jacoco插件配置,clean test执行单元测试代码. All tests should pass.
                sh "mvn clean"
                sh "mvn org.jacoco:jacoco-maven-plugin:prepare-agent -f ${params.pomPath} clean test -DfailIfNoTests=false -Dtest=com.leo.labs.jenkins.qps.unit.* -Dautoconfig.skip=true -Dmaven.test.skip=false -Dmaven.test.failure.ignore=true"
                junit '**/target/surefire-reports/*.xml'
                //配置单元测试覆盖率要求，未达到要求pipeline将会fail,code coverage.LineCoverage>20%.
                jacoco changeBuildStatus: true, minimumLineCoverage: "${params.minLineCoverage}",maximumLineCoverage: "${params.maxLineCoverage}"
            }
        }
		stage('静态检查') {
            //when { anyOf{branch 'feature/ci';branch 'feature/testing';branch 'main'} }
            steps {
                echo "starting codeAnalyze with SonarQube......"
                //sonar:sonar.QualityGate should pass
                
                 withSonarQubeEnv('sonarserver') {
                    //固定使用项目根目录${basedir}下的pom.xml进行代码检查
                   echo "222222222222      ${WORKSPACE}"
                   sh "mvn sonar:sonar  -Dsonar.host.url=http://192.168.33.13:9000 -Dsonar.exclusions=src/test/** -Dsonar.core.codeCoveragePlugin=jacoco -Dsonar.jacoco.reportPaths=$WORKSPACE/pipeline-jenkins-lab/target/jacoco.exec -Dsonar.dynamicAnalysis=reuseReports"
                	error "11111111111111111111111111111111"
                }               

                script {
                    timeout(2) {
                        //利用sonar webhook功能通知pipeline代码检测结果，未通过质量阈，pipeline将会fail
                        def qg = waitForQualityGate('sonarserver')
                        echo "${qg.status}"
                        if (qg.status != 'OK') {
                            echo "${qg.status}"
                            error "未通过Sonarqube的代码质量阈检查，请及时修改！failure: ${qg.status}"
                        }
                    }
                }
            }
        }
        stage('Package') {
            steps {
                sh "mvn clean install -Dmaven.test.skip=true "
            }
        }
	}

}