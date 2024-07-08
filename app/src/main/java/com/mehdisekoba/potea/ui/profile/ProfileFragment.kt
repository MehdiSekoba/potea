package com.mehdisekoba.potea.ui.profile

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.setPadding
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.app.imagepickerlibrary.ImagePicker
import com.app.imagepickerlibrary.ImagePicker.Companion.registerImagePicker
import com.app.imagepickerlibrary.listener.ImagePickerResultListener
import com.app.imagepickerlibrary.model.PickExtension
import com.app.imagepickerlibrary.model.PickerType
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.stored.SessionManager
import com.mehdisekoba.potea.databinding.FeedbackDialogBinding
import com.mehdisekoba.potea.databinding.FragmentProfileBinding
import com.mehdisekoba.potea.utils.AVATAR
import com.mehdisekoba.potea.utils.BASE_URL_IMAGE_PROFILE
import com.mehdisekoba.potea.utils.METHOD
import com.mehdisekoba.potea.utils.MULTIPART_FROM_DATA
import com.mehdisekoba.potea.utils.POST
import com.mehdisekoba.potea.utils.UTF_8
import com.mehdisekoba.potea.utils.base.BaseFragment
import com.mehdisekoba.potea.utils.events.EventBus
import com.mehdisekoba.potea.utils.events.Events
import com.mehdisekoba.potea.utils.extensions.alert
import com.mehdisekoba.potea.utils.extensions.getRealFileFromUri
import com.mehdisekoba.potea.utils.extensions.hideKeyboard
import com.mehdisekoba.potea.utils.extensions.loadImage
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.net.URLEncoder
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment :
    BaseFragment<FragmentProfileBinding>(),
    ImagePickerResultListener {
    override val bindingInflater: (inflater: LayoutInflater) -> FragmentProfileBinding
        get() = FragmentProfileBinding::inflate

    @Inject
    lateinit var sessionManager: SessionManager

    // other
    private val viewModel by viewModels<ProfileViewModel>()
    private val imagePicker: ImagePicker by lazy { registerImagePicker(this) }
    private var selectedThemeIndex = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Auto update profile
        lifecycleScope.launch {
            EventBus.subscribe<Events.IsUpdateProfile> {
                if (isNetworkAvailable)
                    viewModel.callProfileApi()
            }
            EventBus.subscribe<Events.IsUpdatePhotoProfile> {
                if (isNetworkAvailable)
                    viewModel.callProfileApi()
            }
        }
        //call api
        if (isNetworkAvailable) {
            viewModel.callProfileApi()
        } else {
            CustomToast.makeText(
                requireContext(),
                getString(R.string.checkYourNetwork),
                CustomToast.LONG_DURATION,
                CustomToast.WARNING,
                true
            ).show()
        }
        binding.apply {
            //Choose image
            avatarEditImg.setOnClickListener {
                openImagePicker()
            }
            //add address

            lifecycleScope.launch {
                veilLayoutToolbar.veil()
                contentLay.veil()
                delay(500)
                veilLayoutToolbar.unVeil()
                contentLay.unVeil()
                val smallIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_logo)
                smallIcon!!.setBounds(0, 0, 48, 48)
                toolbarTitle.setCompoundDrawablesRelative(smallIcon, null, null, null)

                //orderContainer
                binding.orderLay.apply {
                    //edit profile
                    txtEditProfile.setOnClickListener {
                        val direction = ProfileFragmentDirections.actionToEditProfile()
                        findNavController().navigate(direction)
                    }
                    //add && edit address
                    txtAddAddress.setOnClickListener {
                        val direction = ProfileFragmentDirections.actionToProfileAddresses()
                        findNavController().navigate(direction)
                    }
                    //feedback
                    txtFeedBack.setOnClickListener {
                        showFeedback()
                    }
                    // theme
                    txtDarkMode.setOnClickListener {
                        val themeSettings = resources.getStringArray(R.array.theme_settings)

                        requireContext().alert {
                            setTitle(R.string.theme_setting)
                            setSingleChoiceItems(
                                themeSettings,
                                selectedThemeIndex
                            ) { dialog, which ->
                                selectedThemeIndex = which
                                dialog.dismiss()
                            }
                        }
                    }
                    //logout
                    txtLogout.setOnClickListener {
                        lifecycleScope.launch {
                            sessionManager.clearToken()
                        }
                        (activity as? AppCompatActivity)?.finishAffinity()
                    }
                }
            }
        }

        //load data
        loadProfileData()
        uploadAvatarData()
    }


    @SuppressLint("SetTextI18n")
    private fun loadProfileData() {
        binding.apply {
            viewModel.profileData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {

                    }

                    is NetworkRequest.Success -> {
                        response.data?.let { data ->
                            //Avatar
                            if (data.profilePhoto != null) {
                                val avatar = "${BASE_URL_IMAGE_PROFILE}${data.profilePhoto}"
                                avatarUser.loadImage(avatar)
                            } else {
                                avatarUser.load(R.drawable.placeholder)
                            }
                            if (data.name.isNullOrEmpty().not()) {
                                nameTxt.text = "${data.name} ${data.family}"
                            } else {
                                nameTxt.visibility = View.INVISIBLE
                            }
                            emailTxt.text = data.email

                        }
                    }

                    is NetworkRequest.Error -> {
                        CustomToast.makeText(
                            requireContext(),
                            response.error!!,
                            CustomToast.LONG_DURATION,
                            CustomToast.WARNING,
                            true
                        ).show()
                    }
                }
            }
        }
    }

    private fun uploadAvatarData() {
        binding.apply {
            viewModel.avatarData.observe(viewLifecycleOwner) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {}
                    is NetworkRequest.Success -> {
                        lifecycleScope.launch {
                            EventBus.publish(Events.IsUpdatePhotoProfile)
                            viewModel.callProfileApi()
                        }
                    }

                    is NetworkRequest.Error -> {}
                }
            }
        }
    }

    private fun openImagePicker() {
        imagePicker.title(getString(R.string.gallery_Images))
            .multipleSelection(false)
            .showFolder(true)
            .cameraIcon(true)
            .doneIcon(true)
            .allowCropping(true)
            .compressImage(true)
            .maxImageSize(2)
            .extension(PickExtension.ALL)
        imagePicker.open(PickerType.GALLERY)
    }

    override fun onImagePick(uri: Uri?) {
        val imageFile = getRealFileFromUri(requireContext(), uri!!)?.let { path -> File(path) }

        val multipart = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart(METHOD, POST)

        //Image body
        if (imageFile != null) {
            val fileName = URLEncoder.encode(imageFile.absolutePath, UTF_8)
            val reqFile = imageFile.asRequestBody(MULTIPART_FROM_DATA.toMediaTypeOrNull())
            multipart.addFormDataPart(AVATAR, fileName, reqFile)
        }
        //Call api
        val requestBody = multipart.build()
        if (isNetworkAvailable) {
            viewModel.callUploadAvatarApi(requestBody)
        }
    }

    override fun onMultiImagePick(uris: List<Uri>?) {
    }

    private fun showFeedback() {
        val dialogBinding = FeedbackDialogBinding.inflate(layoutInflater)
        val builder = MaterialAlertDialogBuilder(requireContext())
        builder.setView(dialogBinding.root)
            .setCancelable(false)
        val dialog = builder.create()

        // Variable to keep track of selected star
        var selectedStar: View? = null

        dialogBinding.apply {
            imgClose.setOnClickListener {
                dialog.dismiss()
                root.hideKeyboard()
                sendFeedbackBtn.setOnClickListener { dialog.dismiss() }
            }

            fun updateStarSelection(selected: View?) {
                val selectedScale = 1.2f
                val defaultScale = 0.8f
                val padding = resources.getDimensionPixelSize(R.dimen.selected_star_padding)

                listOf(fiveStar, threeStar, oneStar).forEach { star ->
                    star.setBackgroundResource(if (star == selected) R.drawable.bg_emoji else 0)
                    star.setPadding(if (star == selected) padding else 0)
                    star.scaleX = if (star == selected) selectedScale else defaultScale
                    star.scaleY = if (star == selected) selectedScale else defaultScale
                }
            }

            // Update send button state
            fun updateSendButtonState() {
                val isMessageNotEmpty = messageEdt.text.toString().isNotEmpty()
                sendFeedbackBtn.isEnabled = isMessageNotEmpty && selectedStar != null
            }

            // Check click emoji
            fun onStarClick(star: View) {
                selectedStar = star
                updateStarSelection(star)
                animateImage(star)
                updateSendButtonState()
            }

            fiveStar.setOnClickListener { onStarClick(fiveStar) }
            threeStar.setOnClickListener { onStarClick(threeStar) }
            oneStar.setOnClickListener { onStarClick(oneStar) }


            messageEdt.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateSendButtonState()
                }

                override fun afterTextChanged(s: Editable?) {}
            })

            // Initial call to set button state
            updateSendButtonState()
        }

        dialog.show()
    }

    private fun animateImage(ratingImageView: View) {
        val scaleAnimation =
            ScaleAnimation(
                0F,
                1f,
                0F,
                1f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
            )
        scaleAnimation.fillAfter = true
        scaleAnimation.duration = 200
        ratingImageView.startAnimation(scaleAnimation)
    }
}