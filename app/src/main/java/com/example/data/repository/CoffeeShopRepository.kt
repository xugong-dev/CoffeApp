package com.example.data.repository

import com.example.data.local.CoffeeShopDao
import com.example.data.local.FavoriteCoffeeShop
import com.example.data.network.CafeSearchResponse
import com.example.data.network.Content
import com.example.data.network.GenerateContentRequest
import com.example.data.network.GenerationConfig
import com.example.data.network.Part
import com.example.data.network.RetrofitClient
import com.example.util.ImageHelper
import com.example.BuildConfig
import com.squareup.moshi.JsonAdapter
import kotlinx.coroutines.flow.Flow

class CoffeeShopRepository(private val coffeeShopDao: CoffeeShopDao) {

    val allFavorites: Flow<List<FavoriteCoffeeShop>> = coffeeShopDao.getAllFavorites()

    suspend fun toggleFavorite(shop: FavoriteCoffeeShop) {
        if (coffeeShopDao.isFavorite(shop.id)) {
            coffeeShopDao.delete(shop)
        } else {
            coffeeShopDao.insert(shop.copy(savedAt = System.currentTimeMillis()))
        }
    }

    suspend fun isFavorite(id: String): Boolean {
        return coffeeShopDao.isFavorite(id)
    }

    suspend fun insertFavorite(shop: FavoriteCoffeeShop) {
        coffeeShopDao.insert(shop.copy(savedAt = System.currentTimeMillis()))
    }

    suspend fun deleteFavorite(shop: FavoriteCoffeeShop) {
        coffeeShopDao.delete(shop)
    }

    suspend fun searchLocalCoffeeShops(query: String): List<FavoriteCoffeeShop> {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            throw IllegalStateException("The Gemini API Key is missing or is set to placeholder. Please configure your GEMINI_API_KEY in the AI Studio Secrets panel.")
        }

        val prompt = """
            Search for local coffee shops in and around "$query" that have outstanding, notable outdoor seating options.
            This can include spacious patios, beautiful cafe gardens, quiet heated sidewalk setups, sunny rooftop terraces, or scenic balconies.
            You MUST return a list of popular local coffee shops for this specific location.
            For each coffee shop, you must provide:
            - id: Create a URL-safe, lowercase, unique ID (e.g. "vivace_broadway_seattle")
            - name: The exact, real name of the coffee shop
            - address: The real or highly accurate street address in $query
            - rating: A plausible, reliable rating (e.g., 4.2 to 4.9)
            - reviewCount: An approximate real review volume (e.g., 80 to 3000)
            - outdoorSeatingType: Exactly one of these exact values: "Patio", "Garden", "Rooftop", "Sidewalk", "Deck", "Balcony"
            - seatingDescription: Describe the outdoor seating setup (e.g. heaters, trees, tables, umbrellas, ambiance)
            - vibeScore: A score from 1 to 10 for the outdoor experience visual appeal
            - description: A brief summary (2-3 sentences) about this coffee shop's specialty coffee, vibe, and why locals love it.

            Ensure you output EXACTLY valid JSON matching this schema:
            {
              "coffeeShops": [
                {
                  "id": "string",
                  "name": "string",
                  "address": "string",
                  "rating": double,
                  "reviewCount": int,
                  "outdoorSeatingType": "string",
                  "seatingDescription": "string",
                  "vibeScore": int,
                  "description": "string"
                }
              ]
            }
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(
                responseMimeType = "application/json",
                temperature = 0.4f
            )
        )

        try {
            val response = RetrofitClient.apiService.searchCoffeeShops(apiKey, request)
            val rawJson = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: throw IllegalStateException("Empty reply returned from the AI Search Assistant.")

            // Clean markdown formatting wrappers if model accidentally included any
            val cleanedJson = rawJson.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val adapter: JsonAdapter<CafeSearchResponse> = RetrofitClient.jsonParser.adapter(CafeSearchResponse::class.java)
            val parsedResponse = adapter.fromJson(cleanedJson)
                ?: throw IllegalStateException("Failed to parse coffee shop results from assistant reply.")

            return parsedResponse.coffeeShops.map { networkModel ->
                val imageUrl = ImageHelper.getCoffeeShopImageUrl(networkModel.id, networkModel.name)
                FavoriteCoffeeShop(
                    id = networkModel.id,
                    name = networkModel.name,
                    address = networkModel.address,
                    rating = networkModel.rating,
                    reviewCount = networkModel.reviewCount,
                    outdoorSeatingType = networkModel.outdoorSeatingType,
                    seatingDescription = networkModel.seatingDescription,
                    vibeScore = networkModel.vibeScore,
                    description = networkModel.description,
                    imageUrl = imageUrl
                )
            }
        } catch (e: Exception) {
            throw e
        }
    }
}
