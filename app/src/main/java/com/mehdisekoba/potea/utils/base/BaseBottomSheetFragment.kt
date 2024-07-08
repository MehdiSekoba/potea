package com.mehdisekoba.potea.utils.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.utils.network.NetworkChecker
import com.mehdisekoba.potea.utils.other.CustomToast
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseBottomSheetFragment<T : ViewBinding> : BottomSheetDialogFragment() {
    // Binding
    protected abstract val bindingInflater: (inflater: LayoutInflater) -> T

    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: T? = null
    protected val binding: T get() = requireNotNull(_binding)

    @Inject
    lateinit var networkChecker: NetworkChecker

    // Theme
    override fun getTheme() = R.style.RemoveDialogBackground

    // Other
    var isNetworkAvailable = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        // Check network
        lifecycleScope.launch {
            networkChecker.checkNetwork().collect {
                isNetworkAvailable = it
                if (!it) {
                    CustomToast
                        .makeText(
                            requireContext(),
                            getString(R.string.checkYourNetwork),
                            CustomToast.LONG_DURATION,
                            CustomToast.WARNING,
                        ).show()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val window = dialog!!.window
        window!!.setBackgroundDrawableResource(R.color.backShadow)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
