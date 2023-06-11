package com.afzal.budgetify.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.NumberFormat
import java.util.*

@Entity(tableName = "item_name")

class Item(@NonNull @ColumnInfo(name = "item") val item: String,
           @NonNull @ColumnInfo(name = "price") val price: Float
){
    @PrimaryKey(autoGenerate = true) var id = 0

    fun getFormattedPrice(): String = formatter.format(price)
    companion object{
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance()
        init {
            formatter.currency = Currency.getInstance("INR")
        }
    }
}