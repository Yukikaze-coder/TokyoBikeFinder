package com.luigi.motoparktokyo.api

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun addFavorite(client: HttpClient, idToken: String, spotId: Int): Boolean {
    val response = client.post("http://10.0.2.2:4000/api/favorites") {
        header("Authorization", "Bearer $idToken")
        contentType(ContentType.Application.Json)
        setBody(mapOf("spot_id" to spotId))
    }
    return response.status == HttpStatusCode.Created
}

suspend fun removeFavorite(client: HttpClient, idToken: String, spotId: Int): Boolean {
    val response = client.delete("http://10.0.2.2:4000/api/favorites/$spotId") {
        header("Authorization", "Bearer $idToken")
    }
    return response.status == HttpStatusCode.NoContent
}
