package com.github.epfl.meili.models

data class Review (
    var rating: Int = 5,
    var title: String = "",
    var summary: String = "",
)
