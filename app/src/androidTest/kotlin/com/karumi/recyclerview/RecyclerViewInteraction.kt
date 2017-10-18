package com.karumi.recyclerview

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.Matcher

class RecyclerViewInteraction<A> private constructor(private val viewMatcher: Matcher<View>) {

    companion object {
        fun <A> onRecyclerView(viewMatcher: Matcher<View>): RecyclerViewInteraction<A> =
                RecyclerViewInteraction(viewMatcher)
    }

    private lateinit var items: List<A>

    fun withItems(items: List<A>): RecyclerViewInteraction<A> {
        this.items = items
        return this
    }

    fun check(assertion: (item: A, view: View, e: NoMatchingViewException?) -> Unit): RecyclerViewInteraction<A> {
        items.indices.map {
            onView(viewMatcher)
                    .perform(scrollToPosition<RecyclerView.ViewHolder>(it))
                    .check(RecyclerItemViewAssertion(it, items[it], assertion))
        }
        return this
    }
}