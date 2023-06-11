package com.afzal.budgetify.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {

    @Insert
    suspend fun insert(item: Item)
    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM item_name ORDER BY id ASC")
    fun getAllSubject(): LiveData<List<Item>>
}