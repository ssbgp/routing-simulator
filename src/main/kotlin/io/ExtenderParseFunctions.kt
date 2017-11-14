package io

import bgp.BGPRoute
import bgp.policies.interdomain.*
import core.routing.Extender

/**
 * Created on 31-08-2017
 *
 * @author David Fialho
 *
 * This file contains functions to parse extenders from labels.
 */

/**
 * Parses an Interdomain Extender. The supported labels are:
 *
 *  R+ - parsed as a PeerplusExtender
 *  R* - parsed as a PeerstarExtender
 *  C  - parsed as a CustomerExtender
 *  R  - parsed as a PeerExtender
 *  P  - parsed as a ProviderExtender
 *  S  - parsed as a SiblingExtender
 *
 * This function is NOT case sensitive!
 *
 * @param label      the label of the extender
 * @param lineNumber the number of the line in which the label was found (used for the parse exception message only)
 * @return the extender parsed from the label
 * @throws ParseException if the label is not recognized
 */
@Throws(ParseException::class)
fun parseInterdomainExtender(label: String, lineNumber: Int): Extender<BGPRoute> {

    return when (label.toLowerCase()) {
        "r+" -> PeerplusExtender
        "r*" -> PeerstarExtender
        "c" -> CustomerExtender
        "r" -> PeerExtender
        "p" -> ProviderExtender
        "s" -> SiblingExtender
        else -> throw ParseException("Extender label `$label` was not recognized: " +
                "must be either R+, R*, C, R, P, or S", lineNumber)
    }
}

fun parseInterdomainExtender(label: String): Extender<BGPRoute> {
    return parseInterdomainExtender(label, lineNumber = 0)
}

/**
 * Parses an Interdomain Cost to a Local Preference. The supported labels are: r+, r*, c, r, and p.
 * The returned values are based on the ones defined in the BGP interdomain policies.
 *
 * This function is case sensitive!
 *
 * @param costLabel  the label of the cost
 * @param lineNumber the number of the line in which the label was found (used for the parse exception message only)
 * @return the local preference corresponding to the parsed cost
 * @throws ParseException if the label is not recognized
 */
@Throws(ParseException::class)
fun parseInterdomainCost(costLabel: String, lineNumber: Int): Int {

    return when (costLabel) {
        "r+" -> LOCAL_PREF_PEERPLUS
        "r*" -> LOCAL_PREF_PEERSTAR
        "c" -> LOCAL_PREF_CUSTOMER
        "r" -> LOCAL_PREF_PEER
        "p" -> LOCAL_PREF_PROVIDER
        else -> throw ParseException("cost label `$costLabel` was not recognized: " +
                "must be either r+, r*, c, r, or p", lineNumber)
    }
}
