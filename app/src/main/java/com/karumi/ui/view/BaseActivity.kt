package com.karumi.ui.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import com.github.salomonbrys.kodein.Kodein.Module
import com.github.salomonbrys.kodein.android.KodeinAppCompatActivity
import com.karumi.asApp
import com.karumi.ui.LifecyclePublisher
import com.karumi.ui.LifecycleSubscriber
import com.karumi.ui.lifeCycleLinker

abstract class BaseActivity : KodeinAppCompatActivity(), LifecyclePublisher by lifeCycleLinker {

    abstract val layoutId: Int
    abstract val presenter: LifecycleSubscriber
    abstract val toolbarView: Toolbar
    abstract val activityModules: Module

    override fun onCreate(savedInstanceState: Bundle?) {
        applicationContext.asApp().addModule(activityModules)
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
        setSupportActionBar(toolbarView)
        register(presenter)
        preparePresenter(intent)
        initialize()
    }

    open fun preparePresenter(intent: Intent?) {}

    override fun onResume() {
        super.onResume()
        update()
    }

    override fun onDestroy() {
        unregister(presenter)
        super.onDestroy()
    }
}