package com.ts.mobileccp

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.ts.mobileccp.databinding.ActivityMainBinding
import com.ts.mobileccp.ui.SharedViewModel

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavView: BottomNavigationView = binding.navView

        bottomNavView.background = null
        bottomNavView.menu.getItem(2).isEnabled = false


        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        bottomNavView.setupWithNavController(navController)


        binding.fab.setOnClickListener{
            bottomNavView.menu.getItem(2).isEnabled = true
            bottomNavView.setSelectedItemId(R.id.nav_visit)
            bottomNavView.menu.getItem(2).isEnabled = false
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            handleNavigationDestination(destination)
        }

        //for controlling from fragment
        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
        sharedViewModel.bottomMenuVisible.observe(this) { isVisible ->
            setBottomMenuVisible(isVisible ?: true)
        }

        sharedViewModel.selectedNavItem.observe(this, Observer { itemId ->
            bottomNavView.setSelectedItemId(itemId)
        })

    }

    private fun handleNavigationDestination(destination: NavDestination) {
        when (destination.id) {
            R.id.nav_home, R.id.nav_browse_visit, R.id.nav_browse_sales, R.id.nav_setting -> {

                if (::sharedViewModel.isInitialized) {
                    sharedViewModel.bottomMenuVisible.apply { value = true }

                    if (sharedViewModel.selectedNavItem.value != destination.id) {
                        sharedViewModel.selectedNavItem.apply { value = destination.id }
                    }
                }
            }
            else -> {
                if (::sharedViewModel.isInitialized)
                    sharedViewModel.bottomMenuVisible.apply { value = false }
            }
        }
    }

    private fun getActionBarSize(): Int {
        // Retrieve the action bar size from the theme attributes
        val typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)
        return TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    }

    private fun setBottomMenuVisible(isVisible:Boolean){
        val visiblity  =  if (isVisible) View.VISIBLE else View.GONE
        binding.fab.visibility = visiblity
        binding.bottomAppBar.visibility = visiblity
        binding.navView.visibility = visiblity
        val layoutParams = binding.lnFragment.layoutParams as CoordinatorLayout.LayoutParams

        layoutParams.bottomMargin = if (isVisible) getActionBarSize() else 0
    }
}