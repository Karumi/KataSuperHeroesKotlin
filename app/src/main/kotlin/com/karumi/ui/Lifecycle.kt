package com.karumi.ui

val lifeCycleLinker = LifecycleLinker()

class LifecycleLinker : LifecyclePublisher {

    private val receivers = ArrayList<LifecycleSubscriber>()

    override fun register(subscriber: LifecycleSubscriber) {
        receivers.add(subscriber)
    }

    override fun unregister(subscriber: LifecycleSubscriber) {
        receivers.remove(subscriber)
    }

    override fun update() {
        receivers.forEach(LifecycleSubscriber::update)
    }

    override fun initialize() {
        receivers.forEach(LifecycleSubscriber::initialize)
    }
}

interface LifecyclePublisher {
    fun initialize()
    fun register(subscriber: LifecycleSubscriber)
    fun unregister(subscriber: LifecycleSubscriber)
    fun update()
}

interface LifecycleSubscriber {
    fun initialize() {}
    fun update() {}
}
