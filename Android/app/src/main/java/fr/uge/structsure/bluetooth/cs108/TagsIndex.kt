package fr.uge.structsure.bluetooth.cs108

import androidx.annotation.Keep


class TagsIndex @Keep constructor(
    @get:Keep val address: String,
    @get:Keep val position: Int
) : Comparable<TagsIndex?> {
    override fun compareTo(other: TagsIndex?): Int {
        return address.compareTo(other!!.address)
    }
}
