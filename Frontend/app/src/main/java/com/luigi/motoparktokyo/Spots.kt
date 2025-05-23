package com.luigi.motoparktokyo

import kotlinx.serialization.Serializable

@Serializable
data class Spot(
    val id: Int,
    val name: String,
    val address: String,
    val lat: Double? = null,
    val lng: Double? = null,
    val photo_url: String? = null,
    val price: String? = null,
    val type: String? = null,
    val capacity: Int? = null,
    val creator_user_id: Int? = null,
    val created_at: String? = null
)
