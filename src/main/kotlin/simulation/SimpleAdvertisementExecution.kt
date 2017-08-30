package simulation

import core.routing.Node
import core.routing.Topology
import core.simulator.Engine
import core.simulator.Time
import java.io.IOException

/**
 * Created on 29-08-2017
 *
 * @author David Fialho
 */
class SimpleAdvertisementExecution(val threshold: Time): Execution {

    val dataCollectors = DataCollectorGroup()

    /**
     * Executes a simulation, collects data from it, and reports the results.
     *
     * To collect data, before calling this method the data collectors to be used must be specified, by adding each
     * collector to the data collector group of this execution.
     *
     * @throws IOException If an I/O error occurs
     */
    @Throws(IOException::class)
    override fun execute(topology: Topology<*>, destination: Node<*>) {

        dataCollectors.clear()

        val data = dataCollectors.collect {
            Engine.simulate(topology, destination, threshold)
        }

        data.processData()
        data.report()
    }

}