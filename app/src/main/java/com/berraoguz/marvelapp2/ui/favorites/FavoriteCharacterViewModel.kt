package com.berraoguz.marvelapp2.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.berraoguz.marvelapp2.data.model.character.CharacterModel
import com.berraoguz.marvelapp2.repository.MarvelRepository
import com.berraoguz.marvelapp2.ui.state.ResourceState
import javax.inject.Inject

@HiltViewModel
class FavoriteCharacterViewModel @Inject constructor(
    private val repository: MarvelRepository
): ViewModel(){

    private val _favorites = MutableStateFlow<ResourceState<List<CharacterModel>>>(ResourceState.Empty())
    val favorite : StateFlow<ResourceState<List<CharacterModel>>> = _favorites

    init{
        fetch()
    }

    private fun fetch() = viewModelScope.launch{
        repository.getAll().collectLatest { characters ->
            if(characters.isNullOrEmpty()){
                _favorites.value = ResourceState.Empty()
            }else{
                _favorites.value = ResourceState.Success(characters)
            }
        }
    }

    fun delete(characterModel: CharacterModel) = viewModelScope.launch {
        repository.delete(characterModel)
    }

    fun insert(characterModel: CharacterModel) = viewModelScope.launch {
        repository.insert(characterModel)  // insert fonksiyonu ekleniyor
    }

}