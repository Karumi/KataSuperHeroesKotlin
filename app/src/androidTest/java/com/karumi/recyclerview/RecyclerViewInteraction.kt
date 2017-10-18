package com.karumi.recyclerview

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.contrib.RecyclerViewActions.scrollToPosition
import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.Matcher

class RecyclerViewInteraction<A> private constructor(
        private val viewMatcher: Matcher<View>) {

    private lateinit var items: List<A>

    fun withItems(items: List<A>): RecyclerViewInteraction<A> {
        this.items = items
        return this
    }

    fun check(itemViewAssertion: ItemViewAssertion<A>): RecyclerViewInteraction<A> {
        for (i in items.indices) {
            onView(viewMatcher)
                    .perform(scrollToPosition<RecyclerView.ViewHolder>(i))
                    .check(RecyclerItemViewAssertion(i, items[i], itemViewAssertion))
        }
        return this
    }

    interface ItemViewAssertion<in A> {
        fun check(item: A, view: View, e: NoMatchingViewException)
    }

    companion object {
        fun <A> onRecyclerView(viewMatcher: Matcher<View>): RecyclerViewInteraction<A> =
                RecyclerViewInteraction(viewMatcher)
    }
}