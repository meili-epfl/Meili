package com.github.epfl.meili.database

import java.util.*
import kotlin.collections.HashSet
import kotlin.collections.Map
import kotlin.collections.Set
import kotlin.collections.forEach
import kotlin.collections.plus


abstract class Database<T>: Observable() {
    abstract var elements: Map<String, T>

    abstract fun addElement(key: String, element: T?)

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