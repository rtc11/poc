plugins {
    `java-library`
}

dependencies {
    api(project(":libs:kafka:kafka"))
    api(kotlin("test"))
}
