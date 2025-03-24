package com.berraoguz.marvelapp2.data.local

import androidx.room.*
import com.berraoguz.marvelapp2.data.model.character.CharacterModel
import kotlinx.coroutines.flow.Flow

@Dao
interface MarvelDao {

    // Karakter modelini veritabanına ekler. Eğer zaten varsa, üzerine yazar (REPLACE).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(characterModel: CharacterModel): Long

    // Tüm karakterleri id'ye göre sıralayarak getirir.
    @Query("SELECT * FROM characterModel ORDER BY id")
    fun getAll(): Flow<List<CharacterModel>>

    // Verilen karakteri veritabanından siler.
    @Delete
    suspend fun delete(characterModel: CharacterModel)
}
