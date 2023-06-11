package com.afzal.budgetify.database

import androidx.lifecycle.LiveData

class ItemRepository(private val itemDao : ItemDao) {

    val allSubjects: LiveData<List<Item>> = itemDao.getAllSubject()

    suspend fun insert(item : Item){
        itemDao.insert(item)
    }

    suspend fun delete(item: Item){
        itemDao.delete(item)
    }

}