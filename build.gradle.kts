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
    maven {
        url = uri("https://maven.pkg.github.com/input-output-hk/better-parse")
        credentials {
            username = "atala-dev"
            password = "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"
        }
    }
}
apply(plugin= "maven")

configurations { create("externalLibs") }

dependencies {

    implementation(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))
/*
    implementation(files("src/lib/prism-api-jvm-1.2.0.jar"))
    implementation(files("src/lib/prism-common-jvm-1.2.0.jar"))
    implementation(files("src/lib/prism-credentials-jvm-1.2.0.jar"))
    implementation(files("src/lib/prism-crypto-jvm-1.2.0.jar"))
    implementation(files("src/lib/prism-identity-jvm-1.2.0.jar"))

    implementation(kotlin("compile"))
    "externalLibs"(files("src/lib/prism-api-jvm-1.2.0.jar"))
    "externalLibs"(files("src/lib/prism-common-jvm-1.2.0.jar"))
    "externalLibs"(files("src/lib/prism-credentials-jvm-1.2.0.jar"))
    "externalLibs"(files("src/lib/prism-crypto-jvm-1.2.0.jar"))
    "externalLibs"(files("src/lib/prism-identity-jvm-1.2.0.jar"))
*/
    //implementation("io.iohk.atala:prism-api:$prismVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")

    implementation("io.iohk.atala:prism-api-jvm:1.2.0")
    implementation("io.iohk.atala:prism-credentials-jvm:1.2.0")
    implementation("io.iohk.atala:prism-crypto-jvm:1.2.0")
    implementation("io.iohk.atala:prism-identity-jvm:1.2.0")
    implementation("io.iohk.atala:prism-protos-jvm:1.2.0")
    implementation("org.bouncycastle:bcprov-jdk15on:1.68")

/*
    //fetching the dependencies online
    // needed for the credential payloads defined in protobuf as well as to interact with our backend services
    implementation("io.iohk.atala.prism:protos-jvm:1.2.0")
// needed for cryptography primitives implementation
    implementation("io.iohk.atala.prism:crypto:1.2.0")
// needed to deal with DIDs
    implementation("io.iohk.atala.prism:identities:1.2.0")
// needed to deal with credentials
    implementation("io.iohk.atala.prism:credentials:1.2.0")
// used to avoid some boilerplate
    implementation("io.iohk.atala.prism:extras:1.2.0")
*/
// needed for the credential content, bring the latest version
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
// needed for dealing with dates, bring the latest version
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.2.1")

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
