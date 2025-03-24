package com.berraoguz.marvelapp2.data.model.character

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.berraoguz.marvelapp2.data.model.ThumbnailModel
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Entity(tableName = "characterModel")
@Parcelize  // Bu annotation ile `Parcelable` oluyor
data class CharacterModel(
    @PrimaryKey(autoGenerate = true)
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("thumbnail")
    val thumbnail: ThumbnailModel,
    @SerializedName("description")
    val description: String = ""
) : Parcelable
