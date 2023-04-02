plugins {
    java
    application
    id("org.openjfx.javafxplugin") version("0.0.13")
}

group = "de.articdive.jnoise"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
    }
    withType<Test> {
        useJUnitPlatform()
    }
}

application {
    mainClass.set("de.articdive.jnoise.visualizer.Visualizer")
}

javafx {
    version = "18.0.2"
    modules = arrayListOf("javafx.controls", "javafx.fxml")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    implementation("de.articdive:jnoise-pipeline:4.1.0-SNAPSHOT")
}