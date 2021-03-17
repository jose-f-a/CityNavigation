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
import android.content.res.Resources
import android.widget.Toast
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import ie.cm.citynavigation.api.Endpoints
import ie.cm.citynavigation.api.Report
import ie.cm.citynavigation.api.ReportGet
import ie.cm.citynavigation.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Map : AppCompatActivity(), OnMapReadyCallback {

  //  private lateinit var tempbtn: Button
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

  private lateinit var reports: List<ReportGet>

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

        lastLocation = p0.lastLocation

        var loc = LatLng(lastLocation.latitude, lastLocation.longitude)
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f))
      }
    }

    // Location Request
    createLocationRequest()

    // Markers from WS
    // Call service and add the markers
    val request = ServiceBuilder.buildService(Endpoints::class.java)
    val call = request.getReports()
    var position: LatLng

    call.enqueue(object : Callback<List<ReportGet>> {
      override fun onResponse(call: Call<List<ReportGet>>, response: Response<List<ReportGet>>) {
        Log.d("****Mapa", "Entrou no onResponse")
        if(response.isSuccessful) {
          Log.d("****Mapa", "Entrou no If")

          reports = response.body()!!

          for(report in reports) {
            position = LatLng(report.geo.lat.toString().toDouble(), report.geo.lng.toString().toDouble())
            mMap.addMarker(MarkerOptions().position(position).title(report.report.titulo))

            Log.d("****Mapa", "Marker: $position")
          }
        } else {
          Log.d("****Mapa", "Entrou no else")
        }
      }

      override fun onFailure(call: Call<List<ReportGet>>, t: Throwable) {
        Log.e("****Mapa", "${t.message}")
      }
    })

    /*//Butao temporario
    tempbtn = findViewById(R.id.tempbtn)
    val reportFragment = ReportFragment()

    tempbtn.setOnClickListener {
      reportFragment.show(supportFragmentManager, "aaa")
    }*/
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    setMapStyle(mMap)
    enableMyLocation()

    // Map home coordinates
    val latitude = 41.698871
    val longitude = -8.827075
    val zoomLevel = 15f

    val homeLatLng = LatLng(latitude, longitude)
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, zoomLevel))

    /*setUpMap()*/
    /*// Add a marker in ESTG and move the camera
    val ESGT = LatLng(41.693408, -8.846684)
    mMap.addMarker(MarkerOptions().position(ESGT).title("Marker in ESTG"))
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ESGT, 12.0f))*/
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
  }

  companion object {
    private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    private const val REQUEST_CHECK_SETTINGS = 2
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

/*  mMap.isMyLocationEnabled = true*/

}