package com.example.playlistmaker.ui.edit_playlist

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentEditPlaylistBinding
import com.example.playlistmaker.presentation.edit_playlist.EditPlaylistScreenState
import com.example.playlistmaker.presentation.edit_playlist.EditPlaylistViewModel
import com.example.playlistmaker.ui.root.RootActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class EditPlaylistFragment : Fragment() {

    private var _binding: FragmentEditPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<EditPlaylistViewModel> {
        parametersOf(
            requireArguments().getString(CURRENT_PLAYLIST)
        )
    }

    private lateinit var textWatcherName: TextWatcher
    private lateinit var textWatcherDescription: TextWatcher

    private lateinit var boxStrokeEmpty: ColorStateList
    private lateinit var boxStrokeFilled: ColorStateList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditPlaylistBinding.inflate(inflater, container, false)
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boxStrokeEmpty =
            AppCompatResources.getColorStateList(requireContext(), R.color.outlined_stroke_empty)
        boxStrokeFilled =
            AppCompatResources.getColorStateList(requireContext(), R.color.outlined_stroke_filled)

        setListeners()

        setObservables()
    }

    private fun setListeners() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPressed()
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Glide.with(this).load(uri).transform(
                        CenterCrop(),
                        RoundedCorners(resources.getDimensionPixelSize(R.dimen.rounded_corner_8))
                    ).into(binding.playlistCover)

                    viewModel.newImageUri = uri

                } else {
                    Log.d("PhotoPicker", "Ничего не выбрано")
                }
            }

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btBack.setOnClickListener {
            handleBackPressed()
        }

        binding.btCreatePlaylist.setOnClickListener {
            viewModel.createOrUpdatePressed(
                name = binding.edittextName.text.toString(),
                description = binding.edittextDescription.text.toString().ifBlank { null },
            )
        }

        textWatcherName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btCreatePlaylist.isEnabled = !s.isNullOrBlank()

                with(binding.textLayoutName) {
                    defaultHintTextColor = if (s.isNullOrBlank()) {
                        setBoxStrokeColorStateList(boxStrokeEmpty)
                        boxStrokeEmpty
                    } else {
                        setBoxStrokeColorStateList(boxStrokeFilled)
                        boxStrokeFilled
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.edittextName.addTextChangedListener(textWatcherName)

        textWatcherDescription = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                with(binding.textLayoutDescription) {
                    defaultHintTextColor = if (s.isNullOrBlank()) {
                        setBoxStrokeColorStateList(boxStrokeEmpty)
                        boxStrokeEmpty
                    } else {
                        setBoxStrokeColorStateList(boxStrokeFilled)
                        boxStrokeFilled
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.edittextDescription.addTextChangedListener(textWatcherDescription)

    }

    private fun setObservables() {
        viewModel.getScreenStateLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is EditPlaylistScreenState.PlaylistLoaded -> {

                    binding.title.text = getString(R.string.edit_playlist_title)

                    if (state.uri != null) {
                        Glide.with(this)
                            .load(state.uri)
                            .fallback(R.drawable.ic_placeholder_big)
                            .transform(
                                CenterCrop(),
                                RoundedCorners(resources.getDimensionPixelSize(R.dimen.rounded_corner_8))
                            ).into(binding.playlistCover)
                    }

                    binding.edittextName.setText(state.name)
                    binding.edittextDescription.setText(state.description)
                    binding.btCreatePlaylist.text = getString(R.string.edit_playlist_save)

//                    viewModel.changeScreenState(EditPlaylistScreenState.InitState)
                }

                is EditPlaylistScreenState.PlaylistCreated -> {
                    if (state.successful) {
                        showSnackBar(getString(R.string.playlist_created, state.name))
                    } else {
                        showSnackBar(getString(R.string.playlist_not_created))
                    }
                    viewModel.changeScreenState(EditPlaylistScreenState.InitState)
                    closeFragment()
                }

                is EditPlaylistScreenState.PlaylistUpdated -> {
                    viewModel.changeScreenState(EditPlaylistScreenState.InitState)
                    closeFragment()
                }

                is EditPlaylistScreenState.InitState -> {}
            }
        }
    }

    private fun showSnackBar(toast: String) {
        Snackbar.make(binding.root, toast, Snackbar.LENGTH_LONG).show()
    }

    private fun handleBackPressed() {

        if (requireArguments().getString(CURRENT_PLAYLIST) == null) {
            with(binding) {
                if (viewModel.newImageUri != null || edittextName.text!!.isNotBlank() || edittextDescription.text!!.isNotBlank()) {
                    showExitDialog()
                } else {
                    closeFragment()
                }
            }
        } else {
            closeFragment()
        }
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(
            requireContext(),
            R.style.ThemeOverlay_App_MaterialAlertDialog
        ).setTitle(getString(R.string.exit_dialog_title))
            .setMessage(getString(R.string.exit_dialog_message))
            .setPositiveButton(getString(R.string.exit_dialog_close)) { _, _ ->
                closeFragment()
            }.setNeutralButton(getString(R.string.exit_dialog_cancel)) { _, _ ->
            }.show()
    }

    private fun closeFragment() {
        val rootActivity = requireActivity()
        if (rootActivity is RootActivity) {
            findNavController().navigateUp()
        } else {
            rootActivity.supportFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.edittextName.removeTextChangedListener(textWatcherName)
        binding.edittextDescription.removeTextChangedListener(textWatcherDescription)
        _binding = null
    }

    companion object {
        private const val CURRENT_PLAYLIST = "current_playlist"

        fun createArgs(playlistId: String?): Bundle = bundleOf(
            CURRENT_PLAYLIST to playlistId,
        )

        fun getInstance(playlistId: String?) = EditPlaylistFragment().apply {
            arguments = bundleOf(CURRENT_PLAYLIST to playlistId)
        }
    }

}