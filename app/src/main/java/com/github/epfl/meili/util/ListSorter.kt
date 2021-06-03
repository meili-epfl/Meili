package com.github.epfl.meili.util

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.github.epfl.meili.models.User
import com.github.epfl.meili.profile.UserProfileLinker
import com.github.epfl.meili.profile.friends.UserInfoService

interface ListSorter<T> : AdapterView.OnItemSelectedListener, UserProfileLinker<T> {
    companion object {
        const val NEWEST = "Newest"
        const val OLDEST = "Oldest"
        const val POPULAR = "Popularity"

        const val TAG = "ListSorter"
        var serviceProvider: () -> UserInfoService = { UserInfoService() }
    }

    var sortOrder: String
    var listMap: Map<String, T>

    /** Gets the current activity */
    fun getActivity(): AppCompatActivity

    /** Gets the item's authorUid */
    fun getAuthorUid(item: T): String

    /** Orders the list */
    fun orderList(list: List<Pair<String, Pair<T, User>>>): List<Pair<String, Pair<T, User>>>

    /** Listens to changes in comments */
    fun sortListener(map: Map<String, T>) {
        serviceProvider().getUserInformation(map.values.map { getAuthorUid(it) }) {
            onUsersInfoReceived(it, map)
        }
    }

    /** Initializes the sorting activity */
    fun initSorting(sortSpinner: Spinner, sort_array: Int) {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            getActivity(), sort_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            sortSpinner.adapter = adapter
        }
        sortSpinner.onItemSelectedListener = this
    }

    override fun onUsersInfoReceived(users: Map<String, User>, map: Map<String, T>) {
        this.listMap = map
        usersMap = HashMap(usersMap) + users

        val listAndUsersMap = HashMap<String, Pair<T, User>>()
        for ((id, item) in map) {
            val user = usersMap[getAuthorUid(item)]
            if (user != null) {
                listAndUsersMap[id] = Pair(item, user)
            }
        }

        recyclerAdapter.submitList(orderList(listAndUsersMap.toList()))
        recyclerAdapter.notifyDataSetChanged()
    }

    private fun sortList(order: String) {
        sortOrder = order
        onUsersInfoReceived(HashMap(), listMap)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
        sortList(parent?.getItemAtPosition(pos) as String)
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {}

}