package ks

import kstream.topology.KStreamsUML
import org.junit.jupiter.api.Test

class GenerateDiagrams {

    @Test
    fun `create UML diagram of Topology`() {
        KStreamsUML.file(createTopology()).also {
            println("Generated UML to ${it.absoluteFile}. Use in https://plantuml-editor.kkeisuke.dev")
        }
    }

//    @Test
//    fun `create topology description`() {
//        val filename = "topology.txt"
//        File(filename).apply {
//            writeText(createTopology().describe().toString())
//        }
//        println("Topology description file generated: $filename")
//    }
}