package simulation

import core.routing.Route
import core.routing.Topology
import core.simulator.Advertisement
import core.simulator.DelayGenerator
import core.simulator.Engine
import core.simulator.Time
import io.KeyValueWriter
import ui.Application
import java.io.File
import java.time.Instant

/**
 * Created on 29-08-2017
 *
 * @author David Fialho
 */
class RepetitionRunner<R: Route>(
        private val application: Application,
        private val topology: Topology<R>,
        private val advertisement: Advertisement<R>,
        private val threshold: Time,
        private val repetitions: Int,
        private val messageDelayGenerator: DelayGenerator,
        private val metadataFile: File

): Runner<R> {

    /**
     * Runs the specified execution the number of times specified in the [repetitions] property.
     *
     * The engine configurations may be modified during the run. At the end of this method the engine is always
     * reverted to its defaults.
     *
     * @param execution        the execution that will be executed in each run
     */
    override fun run(execution: Execution<R>, metadata: Metadata) {

        val startInstant = Instant.now()
        Engine.messageDelayGenerator = messageDelayGenerator

        application.run {

            try {
                repeat(times = repetitions) { repetition ->

                    application.execute(repetition + 1, advertisement, messageDelayGenerator.seed) {
                        execution.execute(topology, advertisement, threshold)
                    }

                    // Cleanup for next execution
                    topology.reset()
                    advertisement.advertiser.reset()
                    Engine.messageDelayGenerator.generateNewSeed()
                }

            } finally {
                // Make sure that the engine is always reverted to the defaults after running
                Engine.resetToDefaults()
            }
        }

        metadata["Start Time"] = startInstant
        metadata["Finish Time"] = Instant.now()

        // TODO add application method for writing the metadata file - writing may fail

        KeyValueWriter(metadataFile).use {
            for ((key, value) in metadata) {
                it.write(key, value)
            }
        }

    }
}