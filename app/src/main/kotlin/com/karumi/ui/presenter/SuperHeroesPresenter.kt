package com.karumi.ui.presenter

import com.karumi.common.weak
import com.karumi.domain.model.SuperHero
import com.karumi.domain.usecase.GetSuperHeroes
import com.karumi.ui.LifecycleSubscriber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SuperHeroesPresenter(
    view: View,
    private val getSuperHeroes: GetSuperHeroes
) : LifecycleSubscriber, CoroutineScope by MainScope() {

    private val view: View? by weak(view)

    override fun update() {
        view?.showLoading()
        refreshSuperHeroes()
    }

    override fun destroy() {
        cancel()
    }

    private fun refreshSuperHeroes() = launch {
        val result = coroutineScope { getSuperHeroes() }
        view?.hideLoading()
        when {
            result.isEmpty() -> view?.showEmptyCase()
            else -> view?.showSuperHeroes(result)
        }
    }

    fun onSuperHeroClicked(superHero: SuperHero) = view?.openDetail(superHero.name)

    interface View {
        fun hideLoading()
        fun showSuperHeroes(superHeroes: List<SuperHero>)
        fun showLoading()
        fun showEmptyCase()
        fun openDetail(name: String)
    }
}