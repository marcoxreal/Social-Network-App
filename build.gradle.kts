plugins {
    java
    application
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "21"
    modules = listOf("javafx.controls", "javafx.fxml")
}

application {
    // aici spui OU mainClass-ul pentru GUI-ul tău
    mainClass.set("com.ubb.DuckApplication")
    // dacă vrei să pornești consola în loc:
    // mainClass.set("com.ubb.Main")
}

dependencies {
    runtimeOnly("org.postgresql:postgresql:42.7.8")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.mindrot:jbcrypt:0.4")
}

tasks.test {
    useJUnitPlatform()
}