package org.reduxkotlin

/**
 * A Selector Subscriber - group of selectors that subscribe to store state changes.
 *
 * @param State is the type of the state object returned by the store.
 * @property store The redux store
 * @constructor creates an empty SelectorSubscriberBuilder
 */
class SelectorSubscriberBuilder<State : Any, Selection: Any>(val store: Store<State>) {

    val selectorList = mutableMapOf<Selector<State, Selection>, (Selection) -> Unit>()

    // state is here to make available to lambda with receiver in DSL
    val state: State
        get() = store.getState()

    var withAnyChangeFun: (() -> Unit)? = null

    fun withAnyChange(f: () -> Unit) {
        withAnyChangeFun = f
    }

    fun select(selector: (State) -> Selection, action: (Selection) -> Unit) {
        val selBuilder = SelectorBuilder<State>()
        val sel = selBuilder.withSingleField(selector)
        selectorList[sel] = action
    }
}
