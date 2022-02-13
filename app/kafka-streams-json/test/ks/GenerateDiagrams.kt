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
        val filename = "topology.txt"
        File(filename).apply {
            writeText(createTopology().describe().toString())
        }
        println("Topology description file generated: $filename")
    }
}