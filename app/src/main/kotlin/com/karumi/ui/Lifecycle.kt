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

    override fun destroy() {
        receivers.forEach(LifecycleSubscriber::destroy)
    }
}

interface LifecyclePublisher {
    fun initialize()
    fun register(subscriber: LifecycleSubscriber)
    fun unregister(subscriber: LifecycleSubscriber)
    fun update()
    fun destroy()
}

interface LifecycleSubscriber {
    fun initialize() {}
    fun update() {}
    fun destroy() {}
}
