package com.github.epfl.meili.models

data class Review (
    var rating: Int,
    var title: String,
    var summary: String,
    val uid: String
)
