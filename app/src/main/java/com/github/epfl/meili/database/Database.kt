package com.github.epfl.meili.database

import java.util.*
import kotlin.collections.HashSet

abstract class Database<T>: Observable() {
    abstract var elements: Map<String, T>

    /**
     * Adds the pair (key, element) to the database
     */
    abstract fun addElement(key: String, element: T?)

    abstract fun removeElement(key: String)

    private var observers: Set<Observer> = HashSet()

    override fun addObserver(o: Observer?) {
        super.addObserver(o)
        if (o != null) {
            observers = observers.plus(o)
        }
    }

    override fun notifyObservers() {
        super.notifyObservers()
        observers.forEach { it.update(this, elements) }
    }

    abstract fun onDestroy()
}