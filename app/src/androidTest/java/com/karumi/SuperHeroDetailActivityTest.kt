package com.karumi

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.IdlingRegistry
import android.support.test.espresso.action.ViewActions.scrollTo
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.karumi.data.repository.SuperHeroRepository
import com.karumi.domain.model.SuperHero
import com.karumi.matchers.ToolbarMatcher.onToolbarWithTitle
import com.karumi.mockito.on
import com.karumi.ui.view.SuperHeroDetailActivity
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations

@RunWith(AndroidJUnit4::class)
class SuperHeroDetailActivityTests {

    @Rule
    @JvmField
    var activityRule: ActivityTestRule<SuperHeroDetailActivity> =
            ActivityTestRule(SuperHeroDetailActivity::class.java, true, false)

    @Mock lateinit var repository: SuperHeroRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @After
    fun tearDown() {
        val idlingResources = IdlingRegistry.getInstance().resources
        for (resource in idlingResources) {
            IdlingRegistry.getInstance().unregister(resource)
        }
    }

    @Test
    fun showsSuperHeroNameAsToolbarTitle() {
        val superHero = givenThereIsASuperHero()

        startActivity(superHero)

        onToolbarWithTitle(superHero.name).check(matches(isDisplayed()))
    }

    @Test
    fun hidesProgressBarOnSuperHeroLoaded() {
        val superHero = givenThereIsASuperHero()

        startActivity(superHero)

        onView(withId(R.id.progress_bar)).check(matches(not(isDisplayed())))
    }

    @Test
    fun showsSuperHeroName() {
        val superHero = givenThereIsASuperHero()

        startActivity(superHero)
        scrollToView(R.id.tv_super_hero_name)

        onView(allOf(withId(R.id.tv_super_hero_name), withText(superHero.name))).check(
                matches(isDisplayed()))
    }

    @Test
    fun showsSuperHeroDescription() {
        val superHero = givenThereIsASuperHero()

        startActivity(superHero)
        scrollToView(R.id.tv_super_hero_description)

        onView(withText(superHero.description)).check(matches(isDisplayed()))
    }

    @Test
    fun doesNotShowAvengersBadgeIfSuperHeroIsNotPartOfTheAvengersTeam() {
        val superHero = givenThereIsASuperHero(false)

        startActivity(superHero)

        onView(withId(R.id.iv_avengers_badge)).check(matches(not(isDisplayed())))
    }

    @Test
    fun showsAvengersBadgeIfSuperHeroIsPartOfTheAvengersTeam() {
        val superHero = givenAnAvenger()

        startActivity(superHero)

        onView(withId(R.id.iv_avengers_badge)).check(matches(isDisplayed()))
    }

    private fun givenAnAvenger(): SuperHero = givenThereIsASuperHero(true)

    private fun givenThereIsASuperHero(isAvenger: Boolean = false): SuperHero {
        val superHeroName = "SuperHero"
        val superHeroPhoto = "https://i.annihil.us/u/prod/marvel/i/mg/c/60/55b6a28ef24fa.jpg"
        val superHeroDescription = "Super Hero Description"
        val superHero = SuperHero(superHeroName, superHeroPhoto, isAvenger, superHeroDescription)
        on(repository.getByName(superHeroName)).thenReturn(superHero)
        return superHero
    }

    private fun startActivity(superHero: SuperHero): SuperHeroDetailActivity {
        val intent = Intent()
        intent.putExtra("super_hero_name_key", superHero.name)
        return activityRule.launchActivity(intent)
    }

    private fun scrollToView(viewId: Int) {
        onView(withId(viewId)).perform(scrollTo())
    }
}