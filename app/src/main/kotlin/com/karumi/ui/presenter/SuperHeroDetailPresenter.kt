package com.karumi.ui.presenter

import com.karumi.common.weak
import com.karumi.domain.model.SuperHero
import com.karumi.domain.usecase.GetSuperHeroByName
import com.karumi.ui.LifecycleSubscriber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class SuperHeroDetailPresenter(
    view: View,
    private val getSuperHeroByName: GetSuperHeroByName
) : LifecycleSubscriber, CoroutineScope by MainScope() {

    private val view: View? by weak(view)

    private lateinit var name: String

    fun preparePresenter(name: String?) {
        if (name != null) {
            this.name = name
        } else {
            view?.close()
        }
    }

    override fun update() {
        view?.showLoading()
        refreshSuperHeroes()
    }

    override fun destroy() {
        cancel()
    }

    private fun refreshSuperHeroes() = launch {
        val result = coroutineScope { getSuperHeroByName(name) }
        view?.hideLoading()
        view?.showSuperHero(result)
    }

    interface View {
        fun close()
        fun showLoading()
        fun hideLoading()
        fun showSuperHero(superHero: SuperHero)
    }
}