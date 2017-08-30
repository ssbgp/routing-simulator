package core.routing

import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is` as Is
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import testing.invalidRoute
import testing.via
import testing.route
import testing.node

/**
 * Created on 21-07-2017

 * @author David Fialho
 */
object RoutingTableTest: Spek({

    given("an empty routing table") {

        val table = RoutingTable.empty(invalidRoute())

        on("getting the route for any neighbor") {

            val neighborRoute = table[node(1)]

            it("returns an invalid route") {
                assertThat(neighborRoute, Is(invalidRoute()))
            }
        }

        on("disabling neighbor with ID 1") {

            val neighbor = node(1)
            table.setEnabled(neighbor, false)

            it("indicates neighbor 1 is disabled") {
                assertThat(table.isEnabled(neighbor),
                        Is(false))
            }
        }

    }

    given("a routing table with invalid route via neighbor with ID 1") {

        val table = RoutingTable.of(invalidRoute(),
                invalidRoute() via node(1)
        )

        on("getting the route for neighbor 1") {

            val neighborRoute = table[node(1)]

            it("returns an invalid route") {
                assertThat(neighborRoute, Is(invalidRoute()))
            }
        }

        on("setting route with preference 10 for neighbor 1") {

            table[node(1)] = route(preference = 10)

            it("returns a route with preference 10 when getting the route for neighbor 1") {
                assertThat(table[node(1)], Is(route(preference = 10)))
            }
        }

    }

    given("a routing table with two invalid routes via neighbors 1 and 2") {

        val table = RoutingTable.of(invalidRoute(),
                invalidRoute() via node(1),
                invalidRoute() via node(2)
        )

        on("setting route with preference 10 for neighbor 1") {

            table[node(1)] = route(preference = 10)

            it("returns a valid route with preference 10 when getting the route for neighbor 1") {
                assertThat(table[node(1)], Is(route(preference = 10)))
            }

            it("returns an invalid route when getting the route for neighbor 2") {
                assertThat(table[node(2)], Is(invalidRoute()))
            }
        }

        on("setting a route with preference 10 for neighbor not yet included in the table") {

            table[node(5)] = route(preference = 10)

            it("returns the route with preference 10") {
                // returning an invalid route indicates the neighbor was not added to the table
                assertThat(table[node(5)], Is(route(preference = 10)))
            }
        }

        on("clearing the table") {
            table.clear()

            it("returns an invalid route when getting the route for neighbor 1") {
                assertThat(table[node(1)], Is(invalidRoute()))
            }

            it("returns an invalid route when getting the route for neighbor 2") {
                assertThat(table[node(2)], Is(invalidRoute()))
            }
        }

        on("setting route with preference 10 to neighbor 1") {

            table[node(1)] = route(preference = 10)

            it("returns a valid route with preference 10 when getting the route for neighbor 1") {
                // this indicates the neighbors were not removed from the table when the table was cleared
                assertThat(table[node(1)], Is(route(preference = 10)))
            }
        }

    }

    given("routing table containing valid routes via 4 neighbors: only nodes 1 and 3 are disabled") {

        val table = RoutingTable.of(invalidRoute(),
                route(preference = 10) via node(1),
                route(preference = 5) via node(2),
                route(preference = 15) via node(3),
                route(preference = 30) via node(4)
        )

        table.setEnabled(node(1), false)
        table.setEnabled(node(3), false)

        on("enabling node 1") {

            val route = table.setEnabled(node(1), true)

            it("enabled node 1") {
                assertThat(table.isEnabled(node(1)), Is(true))
            }

            it("kept node 2 enabled") {
                assertThat(table.isEnabled(node(2)), Is(true))
            }

            it("kept node 3 disabled") {
                assertThat(table.isEnabled(node(3)), Is(false))
            }

            it("kept node 4 enabled") {
                assertThat(table.isEnabled(node(4)), Is(true))
            }

            it("returns the candidate route via node 1") {
                assertThat(route, Is(route(preference = 10)))
            }
        }

        on("enabling node 3") {

            val route = table.setEnabled(node(3), true)

            it("kept node 1 enabled") {
                assertThat(table.isEnabled(node(1)), Is(true))
            }

            it("kept node 2 enabled") {
                assertThat(table.isEnabled(node(2)), Is(true))
            }

            it("enabled node 3") {
                assertThat(table.isEnabled(node(3)), Is(true))
            }

            it("kept node 4 enabled") {
                assertThat(table.isEnabled(node(4)), Is(true))
            }

            it("returns the candidate route via node 3") {
                assertThat(route, Is(route(preference = 15)))
            }
        }

        on("enabling node 10") {

            val route = table.setEnabled(node(10), true)

            it("returns invalid route") {
                assertThat(route, Is(invalidRoute()))
            }
        }
    }

})