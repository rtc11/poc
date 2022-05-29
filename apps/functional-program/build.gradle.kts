plugins {
    application
}

application {
    mainClass.set("ks.AppKt")
}

dependencies {
    implementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
}
