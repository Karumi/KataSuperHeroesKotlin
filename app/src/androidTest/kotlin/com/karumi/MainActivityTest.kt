package com.karumi

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.test.espresso.intent.matcher.IntentMatchers.hasExtra
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.hasDescendant
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.instance
import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.SuperHero
import com.karumi.matchers.RecyclerViewItemsCountMatcher.Companion.recyclerViewHasItemCount
import com.karumi.recyclerview.RecyclerViewInteraction
import com.karumi.ui.view.MainActivity
import com.karumi.ui.view.SuperHeroDetailActivity
import com.nhaarman.mockitokotlin2.whenever
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock

@RunWith(AndroidJUnit4::class)
class MainActivityTest : AcceptanceTest<MainActivity>(MainActivity::class.java) {

    companion object {
        private const val ANY_NUMBER_OF_SUPER_HEROES = 10
    }

    @Mock
    lateinit var repository: SuperHeroRepository

    @Test
    fun showsEmptyCaseIfThereAreNoSuperHeroes() {
        givenThereAreNoSuperHeroes()

        startActivity()

        onView(withText("¯\\_(ツ)_/¯")).check(matches(isDisplayed()))
    }

    @Test
    fun showsSuperHeroesNameIfThereAreSuperHeroes() {
        val superHeroes = givenThereAreSomeSuperHeroes(ANY_NUMBER_OF_SUPER_HEROES)

        startActivity()

        RecyclerViewInteraction.onRecyclerView<SuperHero>(withId(R.id.recycler_view))
            .withItems(superHeroes)
            .check { (name), view, exception ->
                matches(hasDescendant(withText(name))).check(
                    view,
                    exception
                )
            }
    }

    @Test
    fun showsAvengersBadgeIfASuperHeroIsPartOfTheAvengersTeam() {
        val superHeroes = givenThereAreSomeAvengers(ANY_NUMBER_OF_SUPER_HEROES)

        startActivity()

        RecyclerViewInteraction.onRecyclerView<SuperHero>(withId(R.id.recycler_view))
            .withItems(superHeroes)
            .check { _, view, exception ->
                matches(
                    hasDescendant(
                        allOf<View>(
                            withId(R.id.iv_avengers_badge),
                            withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)
                        )
                    )
                ).check(view, exception)
            }
    }

    @Test
    fun doesNotShowAvengersBadgeIfASuperHeroIsNotPartOfTheAvengersTeam() {
        val superHeroes = givenThereAreSomeSuperHeroes(ANY_NUMBER_OF_SUPER_HEROES, avengers = false)

        startActivity()

        RecyclerViewInteraction.onRecyclerView<SuperHero>(withId(R.id.recycler_view))
            .withItems(superHeroes)
            .check { _, view, exception ->
                matches(
                    hasDescendant(
                        allOf<View>(
                            withId(R.id.iv_avengers_badge),
                            withEffectiveVisibility(ViewMatchers.Visibility.GONE)
                        )
                    )
                ).check(view, exception)
            }
    }

    @Test
    fun doesNotShowEmptyCaseIfThereAreSuperHeroes() {
        givenThereAreSomeSuperHeroes(ANY_NUMBER_OF_SUPER_HEROES)

        startActivity()

        onView(withId(R.id.tv_empty_case)).check(matches(not(isDisplayed())))
    }

    @Test
    fun doesNotShowLoadingViewOnceSuperHeroesAreShown() {
        givenThereAreSomeSuperHeroes(ANY_NUMBER_OF_SUPER_HEROES)

        startActivity()

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun opensSuperHeroDetailActivityOnRecyclerViewItemTapped() {
        val superHeroes = givenThereAreSomeSuperHeroes()
        val superHeroIndex = 0
        startActivity()

        onView(withId(R.id.recycler_view))
            .perform(actionOnItemAtPosition<RecyclerView.ViewHolder>(superHeroIndex, click()))

        val superHeroSelected = superHeroes[superHeroIndex]
        intended(hasComponent(SuperHeroDetailActivity::class.java.canonicalName))
        intended(hasExtra("super_hero_name_key", superHeroSelected.name))
    }

    @Test
    fun showsTheExactNumberOfSuperHeroes() {
        givenThereAreSomeSuperHeroes(ANY_NUMBER_OF_SUPER_HEROES)

        startActivity()

        onView(withId(R.id.recycler_view)).check(
            matches(recyclerViewHasItemCount(ANY_NUMBER_OF_SUPER_HEROES))
        )
    }

    private fun givenThereAreSomeAvengers(numberOfAvengers: Int): List<SuperHero> =
        givenThereAreSomeSuperHeroes(numberOfAvengers, avengers = true)

    private fun givenThereAreSomeSuperHeroes(
        numberOfSuperHeroes: Int = ANY_NUMBER_OF_SUPER_HEROES,
        avengers: Boolean = false
    ): List<SuperHero> {
        val superHeroes = IntRange(0, numberOfSuperHeroes - 1).map {
            val superHeroName = "SuperHero - $it"
            val superHeroPhoto = "https://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg"
            val superHeroDescription = "Description Super Hero - $it"
            val superHero = SuperHero(superHeroName, superHeroPhoto, avengers, superHeroDescription)
            superHero
        }

        superHeroes.forEach { whenever(repository.getByName(it.name)).thenReturn(it) }
        whenever(repository.getAllSuperHeroes()).thenReturn(superHeroes)
        return superHeroes
    }

    private fun givenThereAreNoSuperHeroes() {
        whenever(repository.getAllSuperHeroes()).thenReturn(emptyList())
    }

    override val testDependencies = Kodein.Module(allowSilentOverride = true) {
        bind<SuperHeroRepository>() with instance(repository)
    }
}