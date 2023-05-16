plugins {
    id("java")
    id("application")
}

group = "xyz.cheesetron"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jsoup:jsoup:1.16.1")
    implementation("org.eclipse.angus:angus-mail:2.0.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("Core")
}