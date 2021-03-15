package ie.cm.citynavigation

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Map : AppCompatActivity(), OnMapReadyCallback {

  //  private lateinit var tempbtn: Button
  private lateinit var mMap: GoogleMap

  // Last known location implementation
  private lateinit var lastLocation: Location
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  // Periodic location updates
  private lateinit var locationCallback: LocationCallback
  private lateinit var locationRequest: LocationRequest

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_map)

    //Toolbar
    setSupportActionBar(findViewById(R.id.homeToolbar))
    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setDisplayShowHomeEnabled(true)
      setDisplayShowTitleEnabled(true)
    }

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    val mapFragment = supportFragmentManager
      .findFragmentById(R.id.mapp) as SupportMapFragment
    mapFragment.getMapAsync(this)

    // Inicializar o  fusedLocationClient
    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    // Real time Location update
    locationCallback = object : LocationCallback() {
      override fun onLocationResult(p0: LocationResult) {
        super.onLocationResult(p0)
        Log.d("****Mapa", "Entrou no onLocationResult")

        lastLocation = p0.lastLocation

        var loc = LatLng(lastLocation.latitude, lastLocation.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f))

        Log.d("****Mapa", "New location received: " + loc.latitude + " - " + loc.longitude)
      }
    }

    // Location Request
    createLocationRequest()

    /*//Butao temporario
    tempbtn = findViewById(R.id.tempbtn)
    val reportFragment = ReportFragment()

    tempbtn.setOnClickListener {
      reportFragment.show(supportFragmentManager, "aaa")
    }*/
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    Log.d("****Mapa", "Entrou no onMapReady")

    /*setUpMap()*/
    /*// Add a marker in ESTG and move the camera
    val ESGT = LatLng(41.693408, -8.846684)
    mMap.addMarker(MarkerOptions().position(ESGT).title("Marker in ESTG"))
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ESGT, 12.0f))*/
  }

  /*fun setUpMap() {
    if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
      return
    }
  }*/


  private fun startLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(
        this,
        android.Manifest.permission.ACCESS_FINE_LOCATION
      ) != PackageManager.PERMISSION_GRANTED
    ) {
      ActivityCompat.requestPermissions(
        this,
        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
        LOCATION_PERMISSION_REQUEST_CODE
      )
      return
    }

    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
  }

  private fun createLocationRequest() {
    locationRequest = LocationRequest()
    locationRequest.interval = 10000
    locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
  }

  override fun onPause() {
    super.onPause()
    Log.d("****Mapa", "Paused")
    fusedLocationClient.removeLocationUpdates(locationCallback)
  }

  override fun onResume() {
    super.onResume()
    Log.d("****Mapa", "Resumed")
    startLocationUpdates()
  }

  companion object {
    private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    private const val REQUEST_CHECK_SETTINGS = 2
  }

/*  mMap.isMyLocationEnabled = true*/

}