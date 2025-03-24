package com.berraoguz.marvelapp2.ui.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.berraoguz.marvelapp2.R
import com.berraoguz.marvelapp2.databinding.FragmentListCharacterBinding
import com.berraoguz.marvelapp2.ui.adapters.CharacterAdapter
import com.berraoguz.marvelapp2.ui.base.BaseFragment
import com.berraoguz.marvelapp2.ui.state.ResourceState
import com.berraoguz.marvelapp2.util.hide
import com.berraoguz.marvelapp2.util.show
import com.berraoguz.marvelapp2.util.toast
import timber.log.Timber

@AndroidEntryPoint
class ListCharacterFragment : BaseFragment<FragmentListCharacterBinding, ListCharacterViewModel>() {

    override val viewModel: ListCharacterViewModel by viewModels()
    private val characterAdapter by lazy { CharacterAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        clickAdapter() // Burada tıklama olayını dinliyoruz
        collectObservers()
    }

    private fun collectObservers() = lifecycleScope.launch {
        viewModel.list.collect { resource ->
            when (resource) {
                is ResourceState.Success -> {
                    resource.data?.let { values ->
                        binding.progressCircular.hide()
                        characterAdapter.characters = values.data.results.toList()
                    }
                }
                is ResourceState.Error -> {
                    binding.progressCircular.hide()
                    resource.message?.let { message ->
                        requireContext().toast(getString(R.string.on_error))
                        Timber.tag("ListCharacterFragment").e("Error: $message")
                    }
                }
                is ResourceState.Loading -> {
                    binding.progressCircular.show()
                }
                else -> {
                    // Default durumda herhangi bir işlem yapmıyoruz.
                }
            }
        }
    }

    private fun setupRecyclerView() = with(binding) {
        rvCharacters.apply {
            adapter = characterAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun clickAdapter() {
        characterAdapter.setOnClickListener { characterModel ->
            // `Parcelable` veri ile geçiş yapıyoruz
            val action = ListCharacterFragmentDirections
                .actionListCharacterFragmentToDetailsCharacterFragment(characterModel)
            findNavController().navigate(action) // Navigasyon işlemi burada yapılır
        }
    }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentListCharacterBinding = FragmentListCharacterBinding.inflate(inflater, container, false)
}
