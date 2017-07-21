package bgp

import core.routing.NodeID

/**
 * Created on 21-07-2017
 *
 * @author David Fialho
 */
class BGPTopologyBuilder {

    data class Link(val tailID: NodeID, val headID: NodeID, val extender: BGPExtender)

    private val ids = mutableSetOf<NodeID>()
    private val links = mutableSetOf<Link>()

    /**
     * Indicates to the builder that the topology to be built must include a node with the given ID.
     *
     * @return true if the ID was not added yet to the builder or false if otherwise
     */
    fun  addNode(id: NodeID): Boolean {
        return ids.add(id)
    }

    fun addLink(from: NodeID, to: NodeID, extender: BGPExtender): Boolean {
        return links.add(Link(from, to, extender))
    }

    /**
     * Returns a BGPTopology containing the nodes and relationships defined in the builder at the time the method is
     * called.
     */
    fun build(): BGPTopology {

        // Data structure that will hold the nodes
        val nodes = HashMap<NodeID, BGPNode>(ids.size)

        // Create a node for each ID
        ids.forEach { nodes.put(it, BGPNodeWith(it)) }

        // Establish relationships based on the links stored in the builder
        for ((tail, head, extender) in links) {
            nodes[head]!!.addRelationship(nodes[tail]!!, extender)
        }

        return BGPTopology(nodes)
    }

}