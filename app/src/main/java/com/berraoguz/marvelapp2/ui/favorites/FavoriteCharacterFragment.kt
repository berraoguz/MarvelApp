package com.berraoguz.marvelapp2.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.berraoguz.marvelapp2.R
import com.berraoguz.marvelapp2.databinding.FragmentFavoriteCharacterBinding
import com.berraoguz.marvelapp2.ui.adapters.CharacterAdapter
import com.berraoguz.marvelapp2.ui.base.BaseFragment
import com.berraoguz.marvelapp2.ui.state.ResourceState
import com.berraoguz.marvelapp2.util.hide
import com.berraoguz.marvelapp2.util.show
import com.berraoguz.marvelapp2.util.toast

@AndroidEntryPoint
class FavoriteCharacterFragment : BaseFragment<FragmentFavoriteCharacterBinding, FavoriteCharacterViewModel>() {

    override val viewModel: FavoriteCharacterViewModel by viewModels()
    private val characterAdapter by lazy { CharacterAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        handleClickEvents()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.favorite.collect { resource ->
                when (resource) {
                    is ResourceState.Success -> {
                        binding.progressBar.hide()
                        resource.data?.let { characters ->
                            if (characters.isNotEmpty()) {
                                binding.tvEmptyList.hide()
                                characterAdapter.characters = characters
                            } else {
                                binding.tvEmptyList.show()
                            }
                        }
                    }
                    is ResourceState.Error -> {
                        binding.progressBar.hide()
                        requireContext().toast(getString(R.string.an_error_occurred))
                    }
                    is ResourceState.Loading -> binding.progressBar.show()
                    is ResourceState.Empty -> binding.tvEmptyList.show()
                }
            }
        }
    }

    private fun handleClickEvents() {
        characterAdapter.setOnClickListener { characterModel ->
            val action = FavoriteCharacterFragmentDirections
                .actionFavoriteCharacterFragmentToDetailsCharacterFragment(characterModel)
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() = with(binding) {
        rvFavoriteCharacter.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(context)
        }
        ItemTouchHelper(itemTouchHelperCallback()).attachToRecyclerView(rvFavoriteCharacter)
    }

    private fun itemTouchHelperCallback(): ItemTouchHelper.SimpleCallback {
        return object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val character = characterAdapter.getCharacterPosition(viewHolder.adapterPosition)
                viewModel.delete(character)
                Snackbar.make(binding.root, getString(R.string.message_delete_character), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.undo)) {
                        viewModel.insert(character)
                    }.show()
            }
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentFavoriteCharacterBinding =
        FragmentFavoriteCharacterBinding.inflate(inflater, container, false)

}
