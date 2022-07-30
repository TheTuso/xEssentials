plugins {
    id("java-library")
    id("io.papermc.paperweight.userdev") version "1.3.8"
}

group = "pl.tuso.essentials"
version = "1.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/thetuso/xcore")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
        }
    }
}

dependencies {
    paperDevBundle("1.19.1-R0.1-SNAPSHOT")
    implementation("pl.tuso.core:xcore:1.6")
    compileOnly("net.luckperms:api:5.4")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }
}
