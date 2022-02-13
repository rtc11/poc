rootProject.name = "poc"

include(
    "app:aap-vilkår",
    "app:aap-grunnlag-saga",
    "app:aap-søknad-saga",
)

include(
    "app:kafka-streams-avro",
    "app:kafka-streams-json",
    "contract:aap-avro",
    "contract:aap-json",
)

include(
    "lib:kafka",
    "lib:ktor-essentials",
    "lib:kafka-mock",
)

include("lib:kafka-streams")
include("lib:kotlinx-serde")
include("lib:lenses")
include("lib:exposed")
include("lib:utils")

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://dl.bintray.com/gradle/gradle-plugins")
    }
}