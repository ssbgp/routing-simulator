package bgp.notifications

import bgp.BGPRoute
import core.routing.Node
import core.simulator.notifications.Notification

/**
 * Created on 26-07-2017
 *
 * @author David Fialho
 *
 * Notification sent when a node detects a recurrent routing loop. This is valid only for SS-BGP or ISS-BGP.
 *
 * @property node             the node that detected a recurrent routing loop.
 * @property learnedRoute     the route that was learned
 * @property alternativeRoute the alternative route
 * @property neighbor         the neighbor from which the route was learned
 */
data class DetectNotification(val node: Node<BGPRoute>, val learnedRoute: BGPRoute, val alternativeRoute: BGPRoute,
                              val neighbor: Node<BGPRoute>) : Notification()