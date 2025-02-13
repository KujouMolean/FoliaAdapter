plugins {
    id("java")
    `maven-publish`
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

group = "com.molean"
version = "1.0-SNAPSHOT"
//paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.moliatopia.icu/repository/maven-snapshots/")
    }
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    paperweight.foliaDevBundle("1.21.4-R0.1-SNAPSHOT")
}

tasks.test {
    useJUnitPlatform()
}
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.molean"
            artifactId = "FoliaAdapter"
            version = "1.0-SNAPSHOT"

            from(components["java"])
        }
    }
}
