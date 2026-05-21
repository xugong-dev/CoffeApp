package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_coffee_shops")
data class FavoriteCoffeeShop(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
    val rating: Double,
    val reviewCount: Int,
    val outdoorSeatingType: String,
    val seatingDescription: String,
    val vibeScore: Int,
    val description: String,
    val imageUrl: String,
    val savedAt: Long = System.currentTimeMillis()
)
