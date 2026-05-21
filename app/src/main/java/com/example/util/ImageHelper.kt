package com.example.util

object ImageHelper {
    private val PHOTO_IDS = listOf(
        "photo-1554118811-1e0d58224f24", // Modern exterior cafe with outdoor seats
        "photo-1525648128230-c41612466a77", // Charming wooden tables on quiet patio
        "photo-1517248135467-4c7edcad34c4", // Spacious modern coffee spot with plants
        "photo-1507133750040-4a8f57021571", // Trendy urban espresso cafe
        "photo-1498804103079-a6351b050096", // Bright, sunny outdoor terrace seating
        "photo-1501339847302-ac426a4a7cbb", // Cozy brick wall facade cafe
        "photo-1447933601403-0c6688de566e", // Dark organic espresso beans and mug
        "photo-1495474472287-4d71bcdd2085", // Variety of iced lattes with wood accents
        "photo-1541167760496-1628856ab772"  // Fresh rich latte art in ceramic cup
    )

    fun getCoffeeShopImageUrl(id: String, name: String): String {
        val hash = Math.abs((id + name).hashCode())
        val index = hash % PHOTO_IDS.size
        val photoId = PHOTO_IDS[index]
        return "https://images.unsplash.com/$photoId?auto=format&fit=crop&q=80&w=600"
    }
}
