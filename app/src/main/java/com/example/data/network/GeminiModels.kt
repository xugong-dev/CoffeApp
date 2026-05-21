package com.example.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    @Json(name = "contents") val contents: List<Content>,
    @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class Content(
    @Json(name = "parts") val parts: List<Part>
)

@JsonClass(generateAdapter = true)
data class Part(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    @Json(name = "responseMimeType") val responseMimeType: String? = null,
    @Json(name = "temperature") val temperature: Float? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(
    @Json(name = "candidates") val candidates: List<Candidate>? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
    @Json(name = "content") val content: Content? = null
)

@JsonClass(generateAdapter = true)
data class CafeSearchResponse(
    @Json(name = "coffeeShops") val coffeeShops: List<CafeNetworkModel>
)

@JsonClass(generateAdapter = true)
data class CafeNetworkModel(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "address") val address: String,
    @Json(name = "rating") val rating: Double,
    @Json(name = "reviewCount") val reviewCount: Int,
    @Json(name = "outdoorSeatingType") val outdoorSeatingType: String,
    @Json(name = "seatingDescription") val seatingDescription: String,
    @Json(name = "vibeScore") val vibeScore: Int,
    @Json(name = "description") val description: String
)
