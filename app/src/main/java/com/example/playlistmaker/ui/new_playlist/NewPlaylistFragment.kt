package com.example.playlistmaker.ui.new_playlist

import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.new_playlist.NewPlaylistViewModel
import com.example.playlistmaker.ui.root.RootActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel


class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    private lateinit var textWatcherName: TextWatcher
    private lateinit var textWatcherDescription: TextWatcher

    private var imageUri: Uri? = null
    private lateinit var boxStrokeEmpty: ColorStateList
    private lateinit var boxStrokeFilled: ColorStateList

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            handleBackPressed()
        }

        boxStrokeEmpty = AppCompatResources.getColorStateList(requireContext(), R.color.outlined_stroke_empty)
        boxStrokeFilled = AppCompatResources.getColorStateList(requireContext(), R.color.outlined_stroke_filled)

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    Glide.with(this).load(uri).transform(
                        CenterCrop(),
                        RoundedCorners(resources.getDimensionPixelSize(R.dimen.rounded_corner_8))
                    ).into(binding.playlistCover)

                    imageUri = uri

                } else {
                    Log.d("PhotoPicker", "Ничего не выбрано")
                }
            }

        binding.playlistCover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btBack.setOnClickListener { handleBackPressed() }

        binding.btCreatePlaylist.setOnClickListener {
            viewModel.createNewPlaylist(
                playlist = Playlist(
                    name = binding.edittextName.text.toString(),
                    description = binding.edittextDescription.text.toString(),
                    imageUri = imageUri
                )
            )
        }

        textWatcherName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.btCreatePlaylist.isEnabled = !s.isNullOrBlank()

                with (binding.textLayoutName) {
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
                with (binding.textLayoutDescription) {
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

        viewModel.observeShowSnackBarEvent().observe(viewLifecycleOwner) { message ->
            if (message != null) {
                showSnackBar(getString(R.string.playlist_created, message))
            } else {
                showSnackBar(getString(R.string.playlist_not_created))
            }
        }

        viewModel.observeCloseFragmentEvent().observe(viewLifecycleOwner) { toBeClosed ->
            if (toBeClosed == true) closeFragment()
        }

    }

    private fun showSnackBar(toast: String) {
        Snackbar.make(binding.root, toast, Snackbar.LENGTH_LONG).show()
    }

    private fun handleBackPressed() {
        with(binding) {
            if (imageUri != null || edittextName.text!!.isNotBlank() || edittextDescription.text!!.isNotBlank()) {
                showExitDialog()
            } else {
                closeFragment()
            }
        }
    }

    private fun showExitDialog() {
        MaterialAlertDialogBuilder(requireContext()).setTitle(getString(R.string.exit_dialog_title))
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

}