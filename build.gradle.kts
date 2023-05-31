plugins {
    id("java")
    id("application")
}

tasks.jar {
    manifest {
        attributes(
            "Manifest-Version" to archiveVersion,
            "Main-Class" to "Core"
        )
    }
}

group = "xyz.cheesetron"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("org.eclipse.angus:angus-mail:2.0.1")
    implementation("org.apache.logging.log4j:log4j-api:2.20.0")
    runtimeOnly("org.apache.logging.log4j:log4j-core:2.20.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("Core")
}