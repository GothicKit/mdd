plugins {
    id("java")
    id("com.gradleup.shadow") version "8.3.1"
}

group = "dev.gothickit.mdd"
version = "1.0.0"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("dev.gothickit:zenkit:1.0.2")
    implementation("net.java.dev.jna:jna:5.13.0")
    implementation("com.formdev:flatlaf:3.5.1")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("org.jetbrains:annotations:24.1.0")
}

tasks.withType<Jar> {
    manifest {
        attributes.put("Main-Class", "dev.gothickit.mdd.Main")
    }
}

tasks.test {
    useJUnitPlatform()
}