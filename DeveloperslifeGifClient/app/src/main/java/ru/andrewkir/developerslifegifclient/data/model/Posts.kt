package ru.andrewkir.developerslifegifclient.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Posts(
    @SerialName("result")
    val result: List<PostInfo>?,
    @SerialName("totalCount")
    val totalCount: Int?
)