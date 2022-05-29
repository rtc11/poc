plugins {
    id("io.gitlab.arturbosch.detekt") version "1.18.0"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.10")

    detektPlugins("com.github.fredgeorge.detektmethodmcc:detekt-method-mcc:1.1")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

detekt {
    toolVersion = "1.18.0"
    config = files("detekt.yml")
    buildUponDefaultConfig = true
}
