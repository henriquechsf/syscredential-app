package com.github.henriquechsf.syscredentialapp.presenter.profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.github.henriquechsf.syscredentialapp.R
import com.github.henriquechsf.syscredentialapp.data.model.User
import com.github.henriquechsf.syscredentialapp.databinding.BottomSheetImageBinding
import com.github.henriquechsf.syscredentialapp.databinding.FragmentProfileBinding
import com.github.henriquechsf.syscredentialapp.presenter.base.BaseFragment
import com.github.henriquechsf.syscredentialapp.presenter.base.StateView
import com.github.henriquechsf.syscredentialapp.util.Constants.APP_FILE_PROVIDER
import com.github.henriquechsf.syscredentialapp.util.FirebaseHelper
import com.github.henriquechsf.syscredentialapp.util.hideKeyboard
import com.github.henriquechsf.syscredentialapp.util.initToolbar
import com.github.henriquechsf.syscredentialapp.util.loadImage
import com.github.henriquechsf.syscredentialapp.util.toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    private val profileViewModel: ProfileViewModel by viewModels()

    private var user: User? = null

    private var imageProfile: String? = null
    private var currentPhotoPath: String? = null

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentProfileBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolbar(binding.toolbar)

        getProfile()
        initListeners()
    }

    private fun initListeners() {
        binding.imgUser.setOnClickListener { showBottomSheetPickImage() }
        binding.btnSaveProfile.setOnClickListener {
            if (user != null) {
                imageProfile?.let {
                    saveProfileWithImage(it)
                } ?: run {
                    validateData()
                }
            }
        }
        binding.btnLogout.setOnClickListener {
            FirebaseHelper.getAuth().signOut()
            findNavController().navigate(R.id.action_profileFragment_to_auth_graph)
        }
    }

    private fun configData() {
        user?.image?.let {
            if (it.isNotEmpty()) {
                loadImage(binding.imgUser, it)
            } else {
                binding.imgUser.setImageResource(R.drawable.ic_image_profile)
            }
        }
        binding.edtName.setText(user?.name)
        binding.edtEmail.setText(user?.email)
    }

    private fun validateData() = with(binding) {
        val name = edtName.text.toString().trim()
        val email = edtEmail.text.toString().trim()

        if (name.isNotEmpty() || email.isNotEmpty()) {
            hideKeyboard()

            user?.let {
                it.name = name
                it.email = email

                saveProfile(it)
            }
        } else {
            toast(message = getString(R.string.common_error_required_field))
        }
    }

    private fun getProfile() {
        profileViewModel.getProfile().observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is StateView.Sucess -> {
                    binding.progressBar.isVisible = false
                    stateView.data?.let { user = it }
                    configData()
                }
                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    toast(message = stateView.message ?: "")
                }
            }
        }
    }

    private fun saveProfile(user: User) {
        profileViewModel.saveProfile(user).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is StateView.Sucess -> {
                    binding.progressBar.isVisible = false
                    toast(message = "Perfil atualizado com sucesso")
                }
                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    toast(message = stateView.message ?: "")
                }
            }
        }
    }

    private fun showBottomSheetPickImage() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
        val bottomSheetBinding: BottomSheetImageBinding =
            BottomSheetImageBinding.inflate(layoutInflater, null, false)

        bottomSheetBinding.btnCamera.setOnClickListener {
            checkPermissionCamera()
            bottomSheetDialog.dismiss()
        }
        bottomSheetBinding.btnGallery.setOnClickListener {
            openGallery()
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.setContentView(bottomSheetBinding.root)
        bottomSheetDialog.show()
    }

    private fun saveProfileWithImage(image: String) {
        profileViewModel.saveImageProfile(image).observe(viewLifecycleOwner) { stateView ->
            when (stateView) {
                is StateView.Loading -> {
                    binding.progressBar.isVisible = true
                }
                is StateView.Sucess -> {
                    stateView.data?.let { user?.image = it }
                    validateData()
                }
                is StateView.Error -> {
                    binding.progressBar.isVisible = false
                    toast(message = stateView.message ?: "")
                }
            }
        }
    }

    // TODO: refactor implementation to own file
    private fun checkPermissionCamera() {
        val permissionlistener: PermissionListener = object : PermissionListener {
            override fun onPermissionGranted() {
                openCamera()
            }

            override fun onPermissionDenied(deniedPermissions: List<String>) {
                toast(message = "Permissao Negada")
            }
        }
        showDialogPermissionDenied(
            permissionlistener = permissionlistener,
            permission = Manifest.permission.CAMERA,
            message = R.string.text_message_camera_denied_profile_fragment
        )
    }

    private fun showDialogPermissionDenied(
        permissionlistener: PermissionListener,
        permission: String,
        message: Int
    ) {
        TedPermission.create()
            .setPermissionListener(permissionlistener)
            .setDeniedTitle("Permissao negada")
            .setDeniedMessage(message)
            .setDeniedCloseButtonText("Nao")
            .setGotoSettingButtonText("Sim")
            .setPermissions(permission)
            .check();
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {

            val imageSelected = result.data!!.data
            imageProfile = imageSelected.toString()

            if (imageSelected != null) {
                binding.imgUser.setImageBitmap(getBitmap(imageSelected))
            }
        }
    }

    private fun getBitmap(pathUri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, pathUri)
            } else {
                val source =
                    ImageDecoder.createSource(requireActivity().contentResolver, pathUri)
                ImageDecoder.decodeBitmap(source)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bitmap
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        var photoFile: File? = null
        try {
            photoFile = createImageFile()
        } catch (ex: IOException) {
            toast(message = "Não foi possível abrir a câmera do dispositivo")
        }

        if (photoFile != null) {
            val photoURI = FileProvider.getUriForFile(
                requireContext(),
                APP_FILE_PROVIDER,
                photoFile
            )
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(takePictureIntent)
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss", Locale("pt", "BR")).format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        currentPhotoPath = image.absolutePath
        return image
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath!!)
            binding.imgUser.setImageURI(Uri.fromFile(file))

            imageProfile = file.toURI().toString()
        }
    }
}