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
        var res = date.toString()
        var splitted_res = res.split(" ")
        return splitted_res[MONTH] + " " + splitted_res[DAY_OF_WEEK] + " " + splitted_res[DAY_OF_MONTH]
    }

    fun getTime(date: Date): String {
        var res = date.toString()
        var splitted_res = res.split(" ")

        // Return only hours:minutes without seconds (originally hh:mm:ss)
        return splitted_res[TIME_OF_DAY].substring(0, splitted_res[TIME_OF_DAY].length - 3)
    }
}