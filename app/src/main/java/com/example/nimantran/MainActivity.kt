package com.example.nimantran

import android.Manifest.permission.*
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.nimantran.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private val prefs by lazy { getSharedPreferences("prefs", 0) }
    private lateinit var auth: FirebaseAuth
    private val perms = arrayOf(
        ACCESS_FINE_LOCATION,
        ACCESS_COARSE_LOCATION,
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE,
        CAMERA,
        READ_PHONE_STATE,
        ACCESS_NETWORK_STATE
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        auth = FirebaseAuth.getInstance()
        // val drawerLayout: DrawerLayout = binding.drawerLayout
        // val navView: NavigationView = binding.navView
        // val bottomNav: BottomNavigationView = binding.appBarMain.bottomNavigationView
        navController = findNavController(R.id.nav_host_fragment_content_main)
        loadUI()
        if (EasyPermissions.hasPermissions(this, *perms)) {
            //enableUI()
        } else {
            //disableUI()
            EasyPermissions.requestPermissions(
                this,
                "This app needs access to permission to work",
                1,
                *perms
            )
        }
    }

    private fun loadUI() {
        binding.appBarMain.appBarLogoimage.setOnClickListener {
            binding.drawerLayout.open()
        }
        //Notification
        binding.appBarMain.imageViewMyNotifications.setOnClickListener {
            navController.navigate(R.id.myNotificationFragment)
        }
        // Passing each menu ID as a set of Ids because each
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_logout,
                R.id.homeFragment,
                R.id.myGuestListFragment,
                R.id.myGiftsFragment,
                R.id.myProfileFragment,
                R.id.templateDesignsFragment
            ), binding.drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
        //hide action bar
        supportActionBar?.hide()
        binding.appBarMain.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener(object : NavController.OnDestinationChangedListener{
            override fun onDestinationChanged(
                controller: NavController,
                destination: NavDestination,
                arguments: Bundle?
            ) {
                title = when(destination.id){
                    R.id.homeFragment -> "Home"
                    R.id.myGuestListFragment -> "My Guest List"
                    R.id.myGiftsFragment -> "My Gifts"
                    R.id.myProfileFragment -> "My Profile"
                    R.id.templateDesignsFragment -> "Template Designs"
                    else -> "Nimantran"
                }
            }
        })

        val userType = prefs.getString("userType", "user")

        // add listener to navigation drawer
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_logout -> {
                    Log.d("logout", "logout")
                    auth.signOut()
                    prefs.edit().putString("userType", "").apply()
                    startActivity(Intent(this, AuthenticationActivity::class.java))
                    finish()
                }
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        enableUI()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
       // disableUI()
    }

    private fun enableUI() {
        binding.appBarMain.bottomNavigationView.visibility = android.view.View.VISIBLE
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
    }


    private fun disableUI() {
        binding.appBarMain.bottomNavigationView.visibility = android.view.View.GONE
        Toast.makeText(this, "Permission Revoked", Toast.LENGTH_SHORT).show()
    }
}