package com.vitalii.aptitudetestapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.vitalii.aptitudetestapplication.accounts.GoogleAccountConst
import com.vitalii.aptitudetestapplication.dialogs.DialogType
import com.vitalii.aptitudetestapplication.data.Point
import com.vitalii.aptitudetestapplication.databinding.ActivityMapsBinding
import com.vitalii.aptitudetestapplication.dialogs.DialogHelper
import com.vitalii.aptitudetestapplication.dialogs.UiDialogHelper

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mMap: GoogleMap
    private val dialogHelper = DialogHelper(this)
    lateinit var binding: ActivityMapsBinding
    val mAuth = FirebaseAuth.getInstance()
    lateinit var userEmailHeader: TextView
    lateinit var uiDialogHelper: UiDialogHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initToolbar()
        initComponents()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GoogleAccountConst.GOOGLE_SIGN_IN_REQUEST_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)

                if (account != null) {
                    dialogHelper.accountHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            } catch (e: ApiException) {
                Log.d("APIError", "Api error : ${e.message}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        uiDialogHelper.uiUpdate(mAuth.currentUser)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val points = createSomeCoordinatePoints()
        for (point in points) {
            val location = LatLng(point.lat, point.lng)
            mMap.addMarker(MarkerOptions().position(location).title(point.title).snippet(point.snippet))
        }

        // Отцентрируем камеру относительно точки
        val centerPoint = LatLng(points[0].lat, points[0].lng)
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16f))
        mMap.animateCamera(CameraUpdateFactory.newLatLng(centerPoint), 1750, null)
    }

    private fun createSomeCoordinatePoints(): List<Point> {
        val data = arrayListOf<Point>()
        data.add(Point(50.44827766875538, 30.492633539984705, "Национальный Цирк", null))
        data.add(Point(50.44743863953556, 30.495236569888018, "Море Пива", null))
        return data
    }

    private fun initComponents() {
        binding.navView.setNavigationItemSelectedListener(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        userEmailHeader = binding.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
        uiDialogHelper = UiDialogHelper(this)
    }

    private fun initToolbar() {
        val toogle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbarContent.toolbar, R.string.menu_open, R.string.menu_close)
        binding.drawerLayout.addDrawerListener(toogle)
        toogle.syncState()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.login_in -> {
                dialogHelper.createSignDialog(DialogType.SIGN_IN_DIALOG)
            }
            R.id.registration -> {
                dialogHelper.createSignDialog(DialogType.SIGN_UP_DIALOG)
            }
            R.id.sign_out -> {
                Toast.makeText(this, resources.getString(R.string.user_login_out_done), Toast.LENGTH_SHORT).show()
                mAuth.signOut()
                dialogHelper.accountHelper.signOutGoogle()
                uiDialogHelper.uiUpdate(mAuth.currentUser)
            }
        }
//        binding.drawerLayout.closeDrawer(GravityCompat.START)
        binding.drawerLayout.refreshDrawableState()
        return true
    }

}