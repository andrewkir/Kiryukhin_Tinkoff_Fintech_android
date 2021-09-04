package ru.andrewkir.developerslifegifclient.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Serializable
@Parcelize
data class Posts(
    @SerialName("result")
    val result: List<PostInfo>?,
    @SerialName("totalCount")
    val totalCount: Int?
) : Parcelable