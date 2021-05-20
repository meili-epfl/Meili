package com.github.epfl.meili.util

/**
 * Interface implemented by activities that have Recycler Views in order that the subviews of the
 * recycler view can signal which specific subview has been clicked. For example, in the FriendsListActivity
 * the friends labels from the recycler view have to be able to communicate to the main activity
 * which specific label was clicked and not just that one of the labels was clicked
 */
interface ClickListener {
    /**
     * @param buttonId Id of the button that was clicked (R.id.button)
     * @param info extra information to be passed to the main activity inheriting from ClickListener
     */
    fun onClicked(buttonId: Int, info: String)
}