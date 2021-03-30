package com.github.epfl.meili.review

import com.github.epfl.meili.models.Review
import java.util.*

abstract class ReviewService(): Observable() {
    abstract var reviews: Map<String, Review>
    abstract var averageRating: Float

    abstract fun addReview(uid: String, review: Review)

    private var observers: Set<Observer> = HashSet()

    override fun addObserver(o: Observer?) {
        super.addObserver(o)
        if (o != null) {
            observers = observers.plus(o)
        }
    }

    override fun notifyObservers() {
        super.notifyObservers()
        observers.forEach {o: Observer -> o.update(this, reviews)}
    }
}