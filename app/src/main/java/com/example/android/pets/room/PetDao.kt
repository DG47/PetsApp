package com.example.android.pets.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PetDao {

    @Insert
    fun insert(pet: Pet): Long

    @Query("SELECT * FROM Pet")
    fun getAll(): LiveData<List<Pet>>

    @Query("SELECT * FROM Pet")
    fun getPets(): List<Pet>

    @Query("SELECT * FROM Pet WHERE id = :petId")
    fun getPet(petId: Int): Pet

    @Delete
    fun delete(pet: Pet)

    @Query("DELETE FROM Pet WHERE id = :petId")
    fun deletePet(petId: Int)

    @Update
    fun update(pet: Pet)

    @Query("DELETE FROM Pet")
    fun deleteAll()

    @Query("SELECT * FROM Pet WHERE name || breed LIKE :text")
    fun searchPet(text: String): List<Pet>

    @Query("SELECT * FROM Pet WHERE type LIKE :type")
    fun searchType(type: Int): List<Pet>
}