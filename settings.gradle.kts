pluginManagement {
    repositories {

//        maven { url = uri("https://maven.aliyun.com/repository/public/") }
//        maven { url = uri("https://maven.aliyun.com/repository/google") }
//        maven { url = uri("http://maven.aliyun.com/nexus/content/repositories/jcenter") }
//        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
//        maven {url = uri("https://dl.bintray.com/jetbrains/anko")}
//        maven {url = uri("http://maven.aliyun.com/nexus/content/groups/public/")}
        maven { url = uri("https://jitpack.io") }
        google()
        gradlePluginPortal()
        mavenLocal()
        mavenCentral()
        jcenter()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {

//        maven { url = uri("https://maven.aliyun.com/repository/public/") }
//        maven { url = uri("https://maven.aliyun.com/repository/google") }
//        maven { url = uri("http://maven.aliyun.com/nexus/content/repositories/jcenter") }
//        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin") }
//        maven {url = uri("https://dl.bintray.com/jetbrains/anko")}
//        maven {url = uri("http://maven.aliyun.com/nexus/content/groups/public/")}
        maven { url = uri("https://jitpack.io") }
        google()
        mavenCentral()
        gradlePluginPortal()
        jcenter()
    }
}

rootProject.name = "thirdexperiment"
include(":app")
