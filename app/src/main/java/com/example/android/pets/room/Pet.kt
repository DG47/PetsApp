package com.example.android.pets.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pet (
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var name: String,
    var type: Int,
    var breed: String,
    var gender: Int,
    var weight: Int
)