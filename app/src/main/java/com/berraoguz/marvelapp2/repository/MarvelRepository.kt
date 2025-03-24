package com.berraoguz.marvelapp2.repository

import com.berraoguz.marvelapp2.data.local.MarvelDao
import com.berraoguz.marvelapp2.data.model.character.CharacterModel
import com.berraoguz.marvelapp2.data.remote.Service
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MarvelRepository @Inject constructor(
    private val api: Service,
    private val dao: MarvelDao
) {
    suspend fun list(nameStartsWith: String? = null) = api.list(nameStartsWith)
    suspend fun getComics(characterId: Int) = api.getComics(characterId)

    suspend fun insert(characterModel: CharacterModel) = dao.insert(characterModel)
    suspend fun delete(characterModel: CharacterModel) = dao.delete(characterModel)

    // getAll fonksiyonu Flow döndürmeli, yani veritabanından karakterleri sürekli dinle
    fun getAll(): Flow<List<CharacterModel>> = dao.getAll()
}
