package com.berraoguz.marvelapp2.data.model.comic

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ComicModelData(
    @SerializedName("results")
    val result: List<ComicModel>
): Serializable