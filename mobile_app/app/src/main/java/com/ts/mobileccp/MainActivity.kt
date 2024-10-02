package com.ts.mobileccp

import android.os.Bundle
import android.util.TypedValue
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ts.mobileccp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
//            navView.setSelectedItemId(R.id.nav_sales)
//            navController.navigate(R.id.nav_customer)
            bottomNavView.menu.getItem(2).isEnabled = true
            bottomNavView.setSelectedItemId(R.id.nav_sales)
            bottomNavView.menu.getItem(2).isEnabled = false
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            handleNavigationDestination(destination)
        }

    }

    private fun handleNavigationDestination(destination: NavDestination) {
        when (destination.id) {
            R.id.nav_sales -> {
                binding.fab.visibility = View.INVISIBLE
                binding.navView.visibility = View.INVISIBLE
                val layoutParams = binding.lnFragment.layoutParams as CoordinatorLayout.LayoutParams
                layoutParams.bottomMargin = 0
            }
            else -> {
//                if ((binding.navView.visibility) == View.GONE) {
                    binding.fab.visibility = View.VISIBLE
                    binding.navView.visibility = View.VISIBLE

                    val layoutParams = binding.lnFragment.layoutParams as CoordinatorLayout.LayoutParams
                    layoutParams.bottomMargin = getActionBarSize()

//                }
            }
        }
    }

    private fun getActionBarSize(): Int {
        // Retrieve the action bar size from the theme attributes
        val typedValue = TypedValue()
        theme.resolveAttribute(android.R.attr.actionBarSize, typedValue, true)
        return TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    }
}