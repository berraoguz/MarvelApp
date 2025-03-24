package com.berraoguz.marvelapp2.ui.details

import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.berraoguz.marvelapp2.R
import com.berraoguz.marvelapp2.data.model.character.CharacterModel
import com.berraoguz.marvelapp2.databinding.FragmentDetailsCharacterBinding
import com.berraoguz.marvelapp2.ui.adapters.ComicAdapter
import com.berraoguz.marvelapp2.ui.base.BaseFragment
import com.berraoguz.marvelapp2.ui.state.ResourceState
import com.berraoguz.marvelapp2.util.hide
import com.berraoguz.marvelapp2.util.limitText
import com.berraoguz.marvelapp2.util.show
import com.berraoguz.marvelapp2.util.toast
import timber.log.Timber
import androidx.navigation.fragment.navArgs as navArgs

@AndroidEntryPoint
class DetailsCharacterFragment : BaseFragment<FragmentDetailsCharacterBinding, DetailsCharacterViewModel>() {
    override val viewModel: DetailsCharacterViewModel by viewModels()

    private val args: DetailsCharacterFragmentArgs by navArgs()  // Bu satırda veri alımı gerçekleşiyor
    private val comicAdapter by lazy { ComicAdapter() }
    private lateinit var characterModel: CharacterModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Bundle üzerinden characterModel verisini alıyoruz
        characterModel = args.characterModel // Burada doğru bir şekilde alıyoruz

        viewModel.fetch(characterModel.id)
        setupRecyclerView()
        onLoadedCharacter(characterModel)
        collectObservers()
        binding.tvDescriptionCharacterDetails.setOnClickListener {
            onShowDialog(characterModel)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    private fun onShowDialog(characterModel: CharacterModel) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(characterModel.name)
            .setMessage(characterModel.description)
            .setNegativeButton(getString(R.string.close_dialog)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun collectObservers() = lifecycleScope.launch {
        viewModel.searchContainer.collect { result ->
            when (result) {
                is ResourceState.Success -> {
                    binding.progressBarDetail.hide()
                    result.data?.let { values ->
                        if (values.data.result.isNotEmpty()) {
                            comicAdapter.comics = values.data.result.toList()
                        } else {
                            requireContext().toast(getString(R.string.an_error_occurred))
                        }
                    }
                }
                is ResourceState.Error -> {
                    binding.progressBarDetail.hide()
                    result.message?.let { message ->
                        Timber.tag("DetailCharacterFragment").e("Error -> $message")
                        requireContext().toast(getString(R.string.an_error_occurred))
                    }
                }
                is ResourceState.Loading -> {
                    binding.progressBarDetail.show()
                }
                else -> {}
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_details, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite -> {
                viewModel.insert(characterModel)
                requireContext().toast(getString(R.string.saved_successfully))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onLoadedCharacter(characterModel: CharacterModel) = with(binding) {
        tvNameCharacterDetails.text = characterModel.name
        tvDescriptionCharacterDetails.text = if (characterModel.description.isEmpty()) {
            requireContext().getString(R.string.text_description_empty).limitText(80)
        } else {
            characterModel.description
        }

        Glide.with(requireContext())
            .load("${characterModel.thumbnail.path}.${characterModel.thumbnail.extension}")
            .into(imgCharacterDetails)
    }

    private fun setupRecyclerView() = with(binding) {
        rvComics.apply {
            adapter = comicAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentDetailsCharacterBinding =
        FragmentDetailsCharacterBinding.inflate(inflater, container, false)
}
