package com.karumi.recyclerview

import android.support.test.espresso.NoMatchingViewException
import android.support.test.espresso.PerformException
import android.support.test.espresso.ViewAssertion
import android.support.test.espresso.util.HumanReadables
import android.support.v7.widget.RecyclerView
import android.view.View

class RecyclerItemViewAssertion<A>(
        private val position: Int,
        private val item: A,
        private val assertion: (item: A, view: View, e: NoMatchingViewException?) -> Unit) : ViewAssertion {

    override fun check(view: View, e: NoMatchingViewException?) {
        val recyclerView = view as RecyclerView
        val viewHolderForPosition = recyclerView.findViewHolderForLayoutPosition(position)
        if (viewHolderForPosition == null) {
            throw PerformException.Builder()
                    .withActionDescription(toString())
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(IllegalStateException("No view holder at position: " + position))
                    .build()
        } else {
            val viewAtPosition = viewHolderForPosition.itemView
            assertion(item, viewAtPosition, e)
        }
    }
}
