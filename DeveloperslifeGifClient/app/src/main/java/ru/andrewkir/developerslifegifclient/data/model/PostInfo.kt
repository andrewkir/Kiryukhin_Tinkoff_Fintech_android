package ru.andrewkir.developerslifegifclient.data.model
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Parcelize //TODO убрать если не нужно
data class PostInfo(
    @SerialName("description")
    val description: String?,
    @SerialName("gifURL")
    val gifURL: String?,
    @SerialName("id")
    val id: Int?
) : Parcelable