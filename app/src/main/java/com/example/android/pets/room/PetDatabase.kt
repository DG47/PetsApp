package com.example.android.pets.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    version = 1, entities = [
        // list DB entities
        Pet::class
    ]
)

abstract class PetDatabase : RoomDatabase() {

    abstract fun petDao(): PetDao

    companion object {
        @Volatile private var INSTANCE: PetDatabase? = null

        fun getInstance(context: Context): PetDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): PetDatabase{
            return Room.databaseBuilder(context.applicationContext, PetDatabase::class.java, "pet-database")
                .build()
        }
    }
}