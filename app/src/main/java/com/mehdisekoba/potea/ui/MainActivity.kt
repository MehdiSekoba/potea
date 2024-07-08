package com.mehdisekoba.potea.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.mehdisekoba.potea.R
import com.mehdisekoba.potea.data.stored.SessionManager
import com.mehdisekoba.potea.databinding.ActivityMainBinding
import com.mehdisekoba.potea.utils.base.BaseActivity
import com.mehdisekoba.potea.utils.events.EventBus
import com.mehdisekoba.potea.utils.events.Events
import com.mehdisekoba.potea.utils.network.NetworkChecker
import com.mehdisekoba.potea.utils.network.NetworkRequest
import com.mehdisekoba.potea.utils.other.CustomToast
import com.mehdisekoba.potea.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
@Suppress("DEPRECATION")
class MainActivity : BaseActivity() {
    // Binding
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var networkChecker: NetworkChecker

    @Inject
    lateinit var sessionManager: SessionManager

    // other
    private lateinit var navHost: NavHostFragment
    private val viewModelCart by viewModels<CartViewModel>()
    private var isNetworkAvailable = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        // Init nav host
        navHost = supportFragmentManager.findFragmentById(R.id.navHost) as NavHostFragment
        // Bottom nav menu
        binding.bottomNav.apply {
            setupWithNavController(navHost.navController)
            // Disable double click on items
            setOnNavigationItemReselectedListener {}
        }
        // Gone bottom menu
        navHost.navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.apply {
                when (destination.id) {
                    R.id.homeFragment -> bottomNav.isVisible = true
                    R.id.cartFragment -> bottomNav.isVisible = true
                    R.id.favoriteFragment -> bottomNav.isVisible = true
                    R.id.profileFragment -> bottomNav.isVisible = true
                    else -> bottomNav.isVisible = false
                }
            }
        }
        //Check network
        lifecycleScope.launch {
            networkChecker.checkNetwork().collect {
                isNetworkAvailable = it
            }
        }
        //Update badge
        lifecycleScope.launch {
            EventBus.subscribe<Events.IsUpdateCart> {
                viewModelCart.callCartListApi()
            }
        }
        lifecycleScope.launch {
            delay(200)
            sessionManager.getToken.collect { token ->
                token?.let {
                    if (isNetworkAvailable) {
                        viewModelCart.callCartListApi()
                    }
                }
            }
        }
        //Load data
        loadCartData()
    }

    override fun onNavigateUp(): Boolean =
        navHost.navController.navigateUp() || super.onNavigateUp()

    private fun loadCartData() {
        binding.apply {
            viewModelCart.cartListData.observe(this@MainActivity) { response ->
                when (response) {
                    is NetworkRequest.Loading -> {}
                    is NetworkRequest.Success -> {
                        response.data?.let { data ->
                            if (data.count != null) {
                                if (data.count.toString().toInt() > 0) {
                                    bottomNav.getOrCreateBadge(R.id.cartFragment).apply {
                                        number = data.count.toString().toInt()
                                        backgroundColor = ContextCompat.getColor(
                                            this@MainActivity,
                                            R.color.UFO_Green
                                        )
                                        badgeTextColor =
                                            ContextCompat.getColor(this@MainActivity, R.color.white)
                                        horizontalOffsetWithText = 6
                                    }
                                } else {
                                    bottomNav.removeBadge(R.id.cartFragment)
                                }
                            } else {
                                bottomNav.removeBadge(R.id.cartFragment)
                            }
                        }

                    }

                    is NetworkRequest.Error -> {
                        CustomToast.makeText(
                            this@MainActivity,
                            response.error!!,
                            CustomToast.LONG_DURATION,
                            CustomToast.ERROR
                        ).show()
                    }
                }

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
