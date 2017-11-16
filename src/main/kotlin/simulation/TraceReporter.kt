package simulation

import bgp.BGPRoute
import bgp.notifications.*
import bgp.policies.interdomain.*
import core.routing.Node
import core.routing.Path
import core.routing.Route
import core.simulator.notifications.BasicNotifier
import core.simulator.notifications.StartListener
import core.simulator.notifications.StartNotification
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

/**
 * Created on 15-11-2017
 *
 * @author David Fialho
 *
 * TODO @doc
 * TODO @optimization - try different methods of writing that may speedup the simulation process
 * TODO @refactor - print much nicer messages for each event!!
 */
class TraceReporter(outputFile: File): DataCollector, StartListener,
        LearnListener, ExportListener, SelectListener, DetectListener {

    private val baseOutputFile = outputFile

    /**
     * The reporter outputs the trace of each simulation to its own file.
     * This variable stores the output file for the current simulation. It is updated every time a new simulation
     * starts.
     */
    private var simulationWriter: BufferedWriter? = null
    private var simulationNumber = 0

    /**
     * Adds the collector as a listener for notifications the collector needs to listen to collect data.
     */
    override fun register() {
        BasicNotifier.addStartListener(this)
        BGPNotifier.addLearnListener(this)
        BGPNotifier.addExportListener(this)
        BGPNotifier.addSelectListener(this)
        BGPNotifier.addDetectListener(this)
    }

    /**
     * Removes the collector from all notifiers
     */
    override fun unregister() {
        BasicNotifier.removeStartListener(this)
        BGPNotifier.removeLearnListener(this)
        BGPNotifier.removeExportListener(this)
        BGPNotifier.removeSelectListener(this)
        BGPNotifier.removeDetectListener(this)
        simulationWriter?.close()
    }

    /**
     * Reports the currently collected data.
     */
    override fun report() {
        // nothing to do
        // reporting is done during the execution
    }

    /**
     * Clears all collected data.
     */
    override fun clear() {
        // nothing to do
    }

    /**
     * Invoked to notify the listener of a new start notification.
     */
    override fun notify(notification: StartNotification) {
        simulationNumber++

        val simulationOutputFile = File(baseOutputFile.parent, baseOutputFile.nameWithoutExtension +
                "$simulationNumber.${baseOutputFile.extension}")

        simulationWriter?.close()
        simulationWriter = BufferedWriter(FileWriter(simulationOutputFile))
    }

    /**
     * Invoked to notify the listener of a new learn notification.
     */
    override fun notify(notification: LearnNotification) {
        simulationWriter?.apply {
            notification.apply {
                write("${align(time)}: LEARN  node=${node.pretty()} route=${route.pretty()} " +
                        "neighbor=${neighbor.pretty()}\n")
            }
        }
    }

    /**
     * Invoked to notify the listener of a new export notification.
     */
    override fun notify(notification: ExportNotification) {
        simulationWriter?.apply {
            notification.apply {
                write("${align(time)}: EXPORT node=${node.pretty()} route=${route.pretty()}\n")
            }
        }
    }

    /**
     * Invoked to notify the listener of a new learn notification.
     */
    override fun notify(notification: SelectNotification) {
        simulationWriter?.apply {
            notification.apply {
                write("${align(time)}: SELECT node=${node.pretty()} new-route=${selectedRoute.pretty()} " +
                        "old-route=${previousRoute.pretty()}\n")
            }
        }
    }

    /**
     * Invoked to notify the listener of a new detect notification.
     */
    override fun notify(notification: DetectNotification) {
        simulationWriter?.apply {
            notification.apply {
                write("${align(time)}: DETECT node=${node.pretty()}\n")
            }
        }
    }

    private fun <R: Route> Node<R>.pretty(): String = id.toString()

    private fun BGPRoute.pretty(): String {

        if (this === BGPRoute.invalid() || this === BGPRoute.self()) {
            return toString()
        }

        val cost = when (localPref) {
            LOCAL_PREF_PEERPLUS -> "r+"
            LOCAL_PREF_PEERSTAR -> "r*"
            LOCAL_PREF_CUSTOMER -> "c"
            LOCAL_PREF_PEER     -> "r"
            LOCAL_PREF_PROVIDER -> "p"
            else -> localPref.toString()
        }

        return "($cost, ${asPath.pretty()})"
    }

    private fun Path.pretty(): String = joinToString(transform = {it.pretty()})

    private fun align(value: Any): String = value.toString().padEnd(7)

}