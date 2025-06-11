package com.example.shoppinglistapp

data class LocationData(
    val latitude: Double,
    val longitude: Double
)

data class GecodingResponse(
    val results: List<GeocodingResult>,
    val status: String
)

data class GeocodingResult(
    val formatted_address: String
)
