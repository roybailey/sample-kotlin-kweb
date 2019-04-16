package me.roybailey.sample.kweb

import io.kweb.shoebox.Shoebox
import java.time.Instant

object TodoState {
    data class List(val uid: String, val title: String)

    data class Item(val uid: String, val created: Instant, val listUid: String, val text: String)

    val lists = Shoebox<List>()

    val items = Shoebox<Item>()

    fun itemsByList(listUid: String) = items.view("itemsByList", Item::listUid).orderedSet(listUid, compareBy(Item::created))
}