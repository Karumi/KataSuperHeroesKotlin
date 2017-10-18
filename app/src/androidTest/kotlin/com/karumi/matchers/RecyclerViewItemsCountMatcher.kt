package com.karumi.matchers

import android.support.v7.widget.RecyclerView
import android.view.View
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

class RecyclerViewItemsCountMatcher(private val expectedItemCount: Int) : BaseMatcher<View>() {

    companion object {
        fun recyclerViewHasItemCount(itemCount: Int): Matcher<View> = RecyclerViewItemsCountMatcher(itemCount)
    }

    override fun matches(item: Any): Boolean {
        val recyclerView = item as RecyclerView
        return recyclerView.adapter.itemCount == expectedItemCount
    }

    override fun describeTo(description: Description) {
        description.appendText("recycler view does not contains $expectedItemCount items")
    }
}
