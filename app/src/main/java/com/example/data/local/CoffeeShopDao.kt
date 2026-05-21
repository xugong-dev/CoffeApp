package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeShopDao {
    @Query("SELECT * FROM favorite_coffee_shops ORDER BY savedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteCoffeeShop>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shop: FavoriteCoffeeShop)

    @Delete
    suspend fun delete(shop: FavoriteCoffeeShop)

    @Query("SELECT EXISTS(SELECT * FROM favorite_coffee_shops WHERE id = :id)")
    suspend fun isFavorite(id: String): Boolean
}
