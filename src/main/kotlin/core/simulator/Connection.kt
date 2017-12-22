package core.simulator

import core.routing.Message
import core.routing.Route

/**
 * Created on 22-07-2017
 *
 * @author David Fialho
 *
 * This class abstracts a connection through which messages can flow. A connection can carry
 * messages in a single direction.
 *
 * The [Connection] class provides a [send] method, which abstracts the process of sending a
 * message across the connection. A message sent through a connection is subjected to a random
 * delay obtained from the delay generator [Engine.messageDelayGenerator]. Despite the delays
 * generated by this generator, routing messages are always delivered in first-in-first-out order.
 *
 */
class Connection<R: Route> {

    /**
     * Keeps track of the deliver time of the last message sent through this connection.
     */
    private var lastDeliverTime = 0

    /**
     * Sends a [message]through this connection. It subjects the message to a random delay
     * obtained from the delay generator [Engine.messageDelayGenerator].
     *
     * !! It adds an event to the scheduler [Engine.scheduler] !!
     *
     * @return the deliver time of [message]
     */
    fun send(message: Message<R>): Time {

        val delay = Engine.messageDelayGenerator.nextDelay()
        val deliverTime = maxOf(Engine.scheduler.time + delay, lastDeliverTime) + 1

        Engine.scheduler.schedule(ExportEvent(message), deliverTime)
        lastDeliverTime = deliverTime

        return deliverTime
    }

    /**
     * Resets the connection.
     *
     * After being reset, the connection will not remember previous messages it may have sent.
     * Consequently, new messages sent through this connection may be delivered before the
     * messages sent before calling [reset]. To avoid unexpected behavior, call this method only
     * after ensuring all sent messages have been delivered.
     */
    fun reset() {
        lastDeliverTime = 0
    }

}