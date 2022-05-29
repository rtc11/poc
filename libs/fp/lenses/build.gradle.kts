plugins {
    `java-library`
}

dependencies {
    api(kotlin("reflect")) // runtimeOnly ?
    testImplementation(kotlin("test"))
}

