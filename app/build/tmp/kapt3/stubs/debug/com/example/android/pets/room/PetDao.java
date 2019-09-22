package com.example.android.pets.room;

import java.lang.System;

@androidx.room.Dao()
@kotlin.Metadata(mv = {1, 1, 13}, bv = {1, 0, 3}, k = 1, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'J\b\u0010\u0006\u001a\u00020\u0003H\'J\u0010\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\'J\u0014\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\f0\u000bH\'J\u0010\u0010\r\u001a\u00020\u00052\u0006\u0010\b\u001a\u00020\tH\'J\u000e\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\fH\'J\u0010\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u0016\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00050\f2\u0006\u0010\u0012\u001a\u00020\u0013H\'J\u0016\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00050\f2\u0006\u0010\u0015\u001a\u00020\tH\'J\u0010\u0010\u0016\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\'\u00a8\u0006\u0017"}, d2 = {"Lcom/example/android/pets/room/PetDao;", "", "delete", "", "pet", "Lcom/example/android/pets/room/Pet;", "deleteAll", "deletePet", "petId", "", "getAll", "Landroidx/lifecycle/LiveData;", "", "getPet", "getPets", "insert", "", "searchPet", "text", "", "searchType", "type", "update", "app_debug"})
public abstract interface PetDao {
    
    @androidx.room.Insert()
    public abstract long insert(@org.jetbrains.annotations.NotNull()
    com.example.android.pets.room.Pet pet);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM Pet")
    public abstract androidx.lifecycle.LiveData<java.util.List<com.example.android.pets.room.Pet>> getAll();
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM Pet")
    public abstract java.util.List<com.example.android.pets.room.Pet> getPets();
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM Pet WHERE id = :petId")
    public abstract com.example.android.pets.room.Pet getPet(int petId);
    
    @androidx.room.Delete()
    public abstract void delete(@org.jetbrains.annotations.NotNull()
    com.example.android.pets.room.Pet pet);
    
    @androidx.room.Query(value = "DELETE FROM Pet WHERE id = :petId")
    public abstract void deletePet(int petId);
    
    @androidx.room.Update()
    public abstract void update(@org.jetbrains.annotations.NotNull()
    com.example.android.pets.room.Pet pet);
    
    @androidx.room.Query(value = "DELETE FROM Pet")
    public abstract void deleteAll();
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM Pet WHERE name || breed LIKE :text")
    public abstract java.util.List<com.example.android.pets.room.Pet> searchPet(@org.jetbrains.annotations.NotNull()
    java.lang.String text);
    
    @org.jetbrains.annotations.NotNull()
    @androidx.room.Query(value = "SELECT * FROM Pet WHERE type LIKE :type")
    public abstract java.util.List<com.example.android.pets.room.Pet> searchType(int type);
}