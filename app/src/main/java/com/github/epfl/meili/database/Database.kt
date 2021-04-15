package com.github.epfl.meili.database

import java.util.*
import kotlin.collections.HashSet


abstract class Database<T>(private val path: String): Observable() {
    abstract var elements: Map<String, T>

    abstract fun addElement(uid: String, element: T?)

    private var observers: Set<Observer> = HashSet()

    override fun addObserver(o: Observer?) {
        super.addObserver(o)
        if (o != null) {
            observers = observers.plus(o)
        }
    }

    override fun notifyObservers() {
        super.notifyObservers()
        observers.forEach {o: Observer -> o.update(this, elements)}
    }

    abstract fun onDestroy()
}