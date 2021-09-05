package ru.andrewkir.developerslifegifclient.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostInfo(
    @SerialName("description")
    val description: String?,
    @SerialName("gifURL")
    val gifURL: String?,
    @SerialName("id")
    val id: Int?
)