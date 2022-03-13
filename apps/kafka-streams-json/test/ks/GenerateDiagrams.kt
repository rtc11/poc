package ks

import kstream.topology.KStreamsUML
import org.junit.jupiter.api.Test

class GenerateDiagrams {
    @Test
    fun `create UML diagram of Topology`() {
        KStreamsUML.file(createTopology(), "doc/topology.puml").also {
            println("Generated UML to ${it.absoluteFile}. Use in https://plantuml-editor.kkeisuke.dev")
        }
    }
}
