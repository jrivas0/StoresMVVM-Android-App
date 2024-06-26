package com.example.stores.common.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "StoreEntity")
data class StoreEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var name: String,
    var phone: String = "",
    var website: String = "",
    var photoUrl: String,
    var isFavorite: Boolean = false) {

    constructor() : this(name = "", phone = "", photoUrl = "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as StoreEntity

        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

