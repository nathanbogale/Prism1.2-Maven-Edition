import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    kotlin("jvm") version "1.5.30"
    application
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

val versionDetails: groovy.lang.Closure<org.gradle.tooling.internal.consumer.versioning.VersionDetails> by extra
val prismVersion = "1.2.0"

//group = "io.iohk.atala.prism.example"
group = "io.iohk.atala"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    google()
    jcenter()
    maven("https://plugins.gradle.org/m2/")
    maven("https://vlad107.jfrog.io/artifactory/default-maven-virtual/")
    maven("https://jitpack.io")
    maven {
        url = uri("https://maven.pkg.github.com/input-output-hk/atala-prism-sdk")
        credentials {
           // username = "NA"
           // password = "NA"
            username = "nathanbogale"
            password = "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"
        }
    }

}
apply(plugin= "maven")

configurations { create("externalLibs") }

dependencies {

    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")

    implementation("io.iohk.atala:prism-api-jvm:1.2.0")
    implementation("io.iohk.atala:prism-credentials-jvm:1.2.0")
    implementation("io.iohk.atala:prism-crypto-jvm:1.2.0")
    implementation("io.iohk.atala:prism-identity-jvm:1.2.0")
    implementation("io.iohk.atala:prism-protos-jvm:1.2.0")
    //added to handle
    implementation("org.bouncycastle:bcprov-jdk15on:1.68")
    // addin the GSON plugin to handle JSON data
    implementation("com.google.code.gson:gson:2.8.5")

// needed for the credential content, bring the latest version
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
// needed for dealing with dates, bring the latest version
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")

    implementation("org.json:json:20210307")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.0")
    implementation("junit:junit:4.13.2")

    implementation("com.squareup.moshi:moshi:1.12.0")

}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

kotlin.sourceSets.all {
    languageSettings.optIn("io.iohk.atala.prism.common.PrismSdkInternal")
}

application {
    mainClassName = "io.iohk.atala.prism.example.MainKt"
}

apply(plugin = "org.jlleitschuh.gradle.ktlint")

ktlint {
    verbose.set(true)
    outputToConsole.set(true)

    // Exclude generated proto classes
    filter {
        exclude { element ->
            element.file.path.contains("generated/") or
                element.file.path.contains("externals/")
        }
    }
}
