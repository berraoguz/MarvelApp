package com.berraoguz.marvelapp2.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ThumbnailModel(
    val path: String,
    val extension: String
) : Parcelable

