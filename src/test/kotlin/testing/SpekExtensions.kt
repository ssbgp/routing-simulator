package testing

import org.jetbrains.spek.api.dsl.ActionBody
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.TestBody
import org.jetbrains.spek.api.dsl.TestContainer

/**
 * Created on 01-08-2017
 *
 * @author David Fialho
 *
 * This file contains methods used to extend the Spek testing framework.
 * Most of this methods are wrappers around the common methods such as 'given', 'on', and 'it' that
 * replace the name with something more appropriate for some tests.
 */

//region Group methods

/**
 * Creates a [group][SpecBody.group].
 */
fun SpecBody.`when`(description: String, body: ActionBody.() -> Unit) {
    action("when $description", body = body)
}

//endregion

//region Test methods

/**
 * Creates a [test][SpecBody.test].
 */
fun TestContainer.then(description: String, body: TestBody.() -> Unit) {
    test("then $description", body = body)
}

/**
 * Creates a [test][SpecBody.test].
 */
fun TestContainer.its(description: String, body: TestBody.() -> Unit) {
    test("its $description", body = body)
}

//endregion