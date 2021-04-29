package ie.cm.citynavigation

import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.widget.Toast
import com.droidman.ktoasty.KToasty
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.ChipGroup
import ie.cm.citynavigation.adapter.NoteCardAdapter
import ie.cm.citynavigation.api.Endpoints
import ie.cm.citynavigation.api.Report
import ie.cm.citynavigation.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.Map

class Map : AppCompatActivity(), OnMapReadyCallback {

  private lateinit var categoryFilterCG: ChipGroup
  private lateinit var distanceFilterCG: ChipGroup
  private lateinit var markersArray: ArrayList<Marker>
  private lateinit var markersHash: HashMap<Marker, Int>

  // Mapa
  private lateinit var mMap: GoogleMap

  // Last known location implementation
  private lateinit var lastLocation: Location
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  // Periodic location updates
  private lateinit var locationCallback: LocationCallback
  private lateinit var locationRequest: LocationRequest

  // Permission
  private val REQUEST_LOCATION_PERMISSION = 1

  private lateinit var reports: List<Report>

  private var isLogged: Boolean = false
  private var userId: Int = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_map)

    //Toolbar
    setSupportActionBar(findViewById(R.id.homeToolbar))
    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setDisplayShowHomeEnabled(true)
      setDisplayShowTitleEnabled(true)
      setDisplayShowTitleEnabled(false)
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
        lastLocation = p0.lastLocation
        var loc = LatLng(lastLocation.latitude, lastLocation.longitude)
      }
    }

    // Location Request
    createLocationRequest()

    // Get Shared Preferences
    val sharedPref: SharedPreferences = getSharedPreferences(
      getString(R.string.preference_file_key),
      Context.MODE_PRIVATE
    )
    isLogged = sharedPref.getBoolean(getString(R.string.logged), false)
    userId = sharedPref.getInt(getString(R.string.userId), 0)

    // Chipgroup
    categoryFilterCG = findViewById(R.id.categoryFilterChipGroup)
    distanceFilterCG = findViewById(R.id.distanceFilterChipGroup)
  }

  private fun getReports() {
    // Call service and add the markers
    val request = ServiceBuilder.buildService(Endpoints::class.java)
    val call = request.getReports()
    var position: LatLng

    call.enqueue(object : Callback<List<Report>> {
      override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {
        if (response.isSuccessful) {
          reports = response.body()!!

          for (report in reports) {
            position = LatLng(report.latitude.toDouble(), report.longitude.toDouble())
            var marker = mMap.addMarker(
              MarkerOptions().position(position).title(report.titulo).snippet(report.id.toString())
            )
            markersArray.add(marker)
            markersHash.put(marker, report.categoria_id)
          }
        } else {
          KToasty.warning(this@Map, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
      }

      override fun onFailure(call: Call<List<Report>>, t: Throwable) {
        Log.e("****Mapa", "${t.message}")
      }
    })
  }

  // Long pressing on the map it opens New Report Activity
  private fun setMapLongClick(map: GoogleMap) {
    if (isLogged) {
      map.setOnMapLongClickListener { latLng ->
        val lat = latLng.latitude
        val lng = latLng.longitude

        Intent(this, NewReport::class.java).also {
          it.putExtra("LAT", lat)
          it.putExtra("LNG", lng)
          it.putExtra("USERID", userId)
          startActivity(it)
        }
      }
    } else {
      KToasty.info(this, getString(R.string.loginNeeded), Toast.LENGTH_LONG).show()
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    setMapStyle(mMap)
    enableMyLocation()
    setMapLongClick(mMap)
    mMap.uiSettings.isMyLocationButtonEnabled = false

    // Map home coordinates
    val homeLatLng = LatLng(41.698871, -8.827075)
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, 15f))

    // Initialize Arrays
    markersArray = arrayListOf()
    markersHash = hashMapOf()

    // Get WS Markers
    getReports()

    // Open Marker's Fragmnet
    mMap.let {
      it.setOnMarkerClickListener {
        val bundle = Bundle()
        bundle.putInt("USER", userId)
        bundle.putBoolean("LOGGED", isLogged)
        bundle.putInt("REPORT", it.snippet.toInt())

        val reportFragment = ReportFragment()
        reportFragment.arguments = bundle
        reportFragment.show(supportFragmentManager, "aaa")

        false
      }
    }

    categoryFilterCG.setOnCheckedChangeListener { group, checkedId ->
      var categoriaId = when (checkedId) {
        2131361916 -> 1
        2131361913 -> 2
        2131361912 -> 3
        else -> 0
      }
      for (marker in markersArray) {
        if (categoriaId == 0) {
          marker.isVisible = true
        } else {
          marker.isVisible = markersHash.get(marker) == categoriaId
        }
      }
    }
  }

  private fun isPermissionGranted(): Boolean {
    return ContextCompat.checkSelfPermission(
      this,
      Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
  }

  @SuppressLint("MissingPermission")
  private fun enableMyLocation() {
    if (isPermissionGranted()) {
      mMap.isMyLocationEnabled = true
    } else {
      ActivityCompat.requestPermissions(
        this,
        arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
        REQUEST_LOCATION_PERMISSION
      )
    }
  }

  @SuppressLint("MissingSuperCall")
  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    // Check if location permissions are granted and if so enable the
    // location data layer.
    if (requestCode == REQUEST_LOCATION_PERMISSION) {
      if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
        enableMyLocation()
      }
    }
  }

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
    fusedLocationClient.removeLocationUpdates(locationCallback)
  }

  override fun onResume() {
    super.onResume()
    startLocationUpdates()
    getReports()
  }

  companion object {
    private const val LOCATION_PERMISSION_REQUEST_CODE = 1
  }

  // Allows map styling and theming to be customized.
  private fun setMapStyle(map: GoogleMap) {
    try {
      // Customize the styling of the base map using a JSON object defined
      // in a raw resource file.
      val success = map.setMapStyle(
        MapStyleOptions.loadRawResourceStyle(
          this,
          R.raw.night_map_style
        )
      )

      if (!success) {
        Log.e("****MapStyle", "Style parsing failed.")
      }
    } catch (e: Resources.NotFoundException) {
      Log.e("****MapStyle", "Can't find style. Error: ", e)
    }
  }
}
