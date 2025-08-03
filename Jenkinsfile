pipeline {
    agent any

    options {
        githubProjectProperty(projectUrlStr: "https://github.com/SirBlobman/ShulkerPackX")
    }

    environment {
        DISCORD_URL = credentials('PUBLIC_DISCORD_WEBHOOK')
        MAVEN_DEPLOY = credentials('MAVEN_DEPLOY')
    }

    triggers {
        githubPush()
    }

    tools {
        jdk "JDK 21"
    }

    stages {
        stage ("Gradle: Publish") {
            steps {
                withGradle {
                    script {
                        if (env.BRANCH_NAME == "main") {
                            sh("./gradlew clean build publish --refresh-dependencies --no-daemon")
                        } else {
                            sh("./gradlew clean build --refresh-dependencies --no-daemon")
                        }
                    }
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts artifacts: 'build/libs/ShulkerPackX-*.jar', fingerprint: true
        }

        always {
            script {
                discordSend webhookURL: DISCORD_URL, title: "ShulkerPackX", link: "${env.BUILD_URL}",
                    result: currentBuild.currentResult,
                    description: """\
                        **Branch:** ${env.GIT_BRANCH}
                        **Build:** ${env.BUILD_NUMBER}
                        **Status:** ${currentBuild.currentResult}""".stripIndent(),
                    enableArtifactsList: false, showChangeset: true
            }
        }
    }
}
