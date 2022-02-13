package ks

import kstream.topology.TopologyUML
import org.junit.jupiter.api.Test
import java.io.File

class GenerateDiagrams {

    @Test
    fun `create UML diagram of Topology`() {
        val topology = createTopology()
        val destination = TopologyUML.createFile(topology)
        println("UML file generated: $destination")
    }

    @Test
    fun `create topology description`() {
        File("topology.txt").apply {
            writeText(createTopology().describe().toString())
        }
    }
}