package com.berraoguz.marvelapp2.data.local

import androidx.room.TypeConverter
import com.berraoguz.marvelapp2.data.model.ThumbnailModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class MarvelConverters {
    @TypeConverter
    fun fromThumbnails(thumbnailModel: ThumbnailModel): String = Gson().toJson(thumbnailModel)
    @TypeConverter
    fun toThumbnail(thumbnailModel: String): ThumbnailModel = Gson().fromJson(thumbnailModel, ThumbnailModel::class.java)

    fun fromString(value: String?): ArrayList<String> {
        val listType = object : TypeToken<ArrayList<String>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<String>?): String {
        return Gson().toJson(list)
    }
}