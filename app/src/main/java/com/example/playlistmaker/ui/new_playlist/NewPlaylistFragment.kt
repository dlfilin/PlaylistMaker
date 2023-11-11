package com.example.playlistmaker.ui.new_playlist

import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentNewPlaylistBinding
import com.example.playlistmaker.domain.models.Playlist
import com.example.playlistmaker.presentation.new_playlist.NewPlaylistViewModel
import com.example.playlistmaker.ui.root.RootActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewPlaylistFragment : Fragment() {

    private var _binding: FragmentNewPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NewPlaylistViewModel by viewModel()

    private lateinit var textWatcher: TextWatcher

    private var imageUri: Uri? = null

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

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                // Callback вызовется, когда пользователь выберет картинку
                if (uri != null) {
                    Log.d("PhotoPicker", "Выбранный URI: $uri")
//                binding.playlistCover.setImageURI(uri)
//                binding.playlistCover.scaleType = ImageView.ScaleType.CENTER_CROP

                    Glide.with(this).load(uri).centerCrop()
                        .transform(RoundedCorners(this.resources.getDimensionPixelSize(R.dimen.rounded_corner_8)))
                        .into(binding.playlistCover)

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

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("textWatcher", (!s.isNullOrBlank()).toString())
                binding.btCreatePlaylist.isEnabled = !s.isNullOrBlank()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        binding.edittextName.addTextChangedListener(textWatcher)

        viewModel.observeShowToastEvent().observe(viewLifecycleOwner) { toast ->
            showToast(getString(R.string.playlist_created, toast))
        }

        viewModel.observeCloseFragmentEvent().observe(viewLifecycleOwner) { toBeClosed ->
            if (toBeClosed == true) closeFragment()
        }

    }

    private fun showToast(toast: String) {
        Toast.makeText(
            requireContext(), toast, Toast.LENGTH_LONG
        ).show()
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
        Log.d("XXX", "closeFragment" + requireActivity()::class.toString())
        val rootActivity = requireActivity()
        if (rootActivity is RootActivity) {
            Log.d("XXX", "RootActivity")
            findNavController().navigateUp()
        } else {
            Log.d("XXX", "PlayerActivity")
            rootActivity.supportFragmentManager.popBackStack()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher.let { binding.edittextName.removeTextChangedListener(it) }
        _binding = null
    }

}