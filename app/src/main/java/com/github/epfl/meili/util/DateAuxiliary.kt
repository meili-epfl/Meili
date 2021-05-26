package com.github.epfl.meili.util

import java.util.*

object DateAuxiliary {
    private const val MONTH = 0
    private const val DAY_OF_WEEK = 1
    private const val DAY_OF_MONTH = 2
    private const val TIME_OF_DAY = 3

    fun getDateFromTimestamp(timestamp: Long): Date {
        return Date(timestamp * 1000)
    }

    fun getDay(date: Date): String {
        val res = date.toString()
        val splitRes = res.split(" ")
        return splitRes[MONTH] + " " + splitRes[DAY_OF_WEEK] + " " + splitRes[DAY_OF_MONTH]
    }

    fun getTime(date: Date): String {
        val res = date.toString()
        val splitRes = res.split(" ")

        // Return only hours:minutes without seconds (originally hh:mm:ss)
        return splitRes[TIME_OF_DAY].substring(0, splitRes[TIME_OF_DAY].length - 3)
    }
}