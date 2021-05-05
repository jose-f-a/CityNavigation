package ie.cm.citynavigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.droidman.ktoasty.KToasty
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.chip.ChipGroup
import ie.cm.citynavigation.api.Endpoints
import ie.cm.citynavigation.api.Report
import ie.cm.citynavigation.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class Map : AppCompatActivity(), OnMapReadyCallback, SensorEventListener {
  // Inputs
  private lateinit var categoryFilterCG: ChipGroup
  private lateinit var distanceFilterCG: ChipGroup
  private lateinit var speedTV: TextView

  // Mapa
  private lateinit var mMap: GoogleMap
  private lateinit var geofenceClient: GeofencingClient
  private lateinit var geofenceHelper: GeofenceHelper
  var geofenceRadius = 200
  var GEOFENCE_ID = "GEOFENCEID"

  // Sensors
  private lateinit var sensorManager: SensorManager
  private var light: Sensor? = null
  private var accelerometer: Sensor? = null
  private var brightness: Float = 0F

  // Last known location implementation
  private lateinit var lastLocation: Location
  private lateinit var fusedLocationClient: FusedLocationProviderClient

  // Periodic location updates
  private lateinit var locationCallback: LocationCallback
  private lateinit var locationRequest: LocationRequest

  // Permission
  private val REQUEST_LOCATION_PERMISSION = 1
  private val BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10002

  // Dados
  private lateinit var reports: List<Report>
  private lateinit var markersArray: ArrayList<Marker>
  private lateinit var markersCatHash: HashMap<Marker, Int>

  // Shared Preferences
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
    geofenceRadius = sharedPref.getInt(getString(R.string.geofenceRadius), 200)

    // Chipgroup
    categoryFilterCG = findViewById(R.id.categoryFilterChipGroup)
    distanceFilterCG = findViewById(R.id.distanceFilterChipGroup)
    speedTV = findViewById(R.id.speed)

    // Geofence
    geofenceClient = LocationServices.getGeofencingClient(this)
    geofenceHelper = GeofenceHelper(this)

    // Sensors
    sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
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
            if (report.user_id == userId) {
              var marker = mMap.addMarker(
                MarkerOptions().position(position).title(report.titulo)
                  .snippet(report.id.toString()).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                  )
              )
              markersArray.add(marker)
              markersCatHash.put(marker, report.categoria_id)
            } else {
              var marker = mMap.addMarker(
                MarkerOptions().position(position).title(report.titulo)
                  .snippet(report.id.toString())
              )
              markersArray.add(marker)
              markersCatHash.put(marker, report.categoria_id)
            }
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

  @RequiresApi(Build.VERSION_CODES.M)
  private fun addCircle(latlng: LatLng, radius: Double) {
    var circle = CircleOptions()
    circle.center(latlng)
    circle.radius(radius)
    circle.strokeColor(getColor(R.color.main))
    circle.fillColor(getColor(R.color.secondaryFaded))
    circle.strokeWidth(1.0f)
    mMap.addCircle(circle)
  }

  @SuppressLint("MissingPermission")
  private fun addGeofence(latLng: LatLng, radius: Double) {
    val geofence = geofenceHelper.getGeofence(
      GEOFENCE_ID,
      latLng,
      radius,
      Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_DWELL or Geofence.GEOFENCE_TRANSITION_EXIT
    )

    val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
    val pendingIntent = geofenceHelper.getPendingIntent()

    geofenceClient.addGeofences(geofencingRequest, pendingIntent)
      .addOnSuccessListener {
        Log.d("****Geofence", "onSuccess: Geofence Added...")
      }
      .addOnFailureListener { e ->
        val errorMessage = geofenceHelper.getErrorString(e)
        Log.d("****Geofence", "onFailure: $e")
      }
  }

  @RequiresApi(Build.VERSION_CODES.M)
  private fun handleMapClick(latLng: LatLng) {
    addGeofence(latLng, geofenceRadius.toDouble())
    addCircle(latLng, geofenceRadius.toDouble())
  }

  @RequiresApi(Build.VERSION_CODES.M)
  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap
    setMapStyle(mMap)
    enableMyLocation()
    setMapLongClick(mMap)
    mMap.uiSettings.isMyLocationButtonEnabled = false
    mMap.uiSettings.isCompassEnabled = false

    // Map home coordinates
    val homeLatLng = LatLng(41.698871, -8.827075)
    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(homeLatLng, 15f))

    // Initialize Arrays
    markersArray = arrayListOf()
    markersCatHash = hashMapOf()

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

    // Category Filter
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
          marker.isVisible = markersCatHash.get(marker) == categoriaId
        }
      }
    }

    // Distance Filter
    distanceFilterCG.setOnCheckedChangeListener { group, checkedId ->
      var filter: Float = when (checkedId) {
        2131361901 -> 1000f
        2131361903 -> 2000f
        2131361905 -> 3000f
        2131361906 -> 4000f
        2131361907 -> 5000f
        2131361908 -> 6000f
        2131361909 -> 7000f
        2131361910 -> 8000f
        2131361911 -> 9000f
        2131361900 -> 10000f
        else -> 0f
      }

      for (marker in markersArray) {
        if (filter == 0f) {
          marker.isVisible = true
        } else {
          var distance = getDistance(
            lastLocation.latitude,
            lastLocation.longitude,
            marker.position.latitude,
            marker.position.longitude
          )
          marker.isVisible = filter >= distance
        }
      }
    }

    // On Map Click adds Geofence
    mMap.setOnMapClickListener { latLng ->
      if (Build.VERSION.SDK_INT >= 29) {
        //We need background permission
        if (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
          ) == PackageManager.PERMISSION_GRANTED
        ) {
          handleMapClick(latLng)
        } else {
          if (ActivityCompat.shouldShowRequestPermissionRationale(
              this,
              Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
          ) {
            //We show a dialog and ask for permission
            ActivityCompat.requestPermissions(
              this,
              arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
              BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
            )
          } else {
            ActivityCompat.requestPermissions(
              this,
              arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
              BACKGROUND_LOCATION_ACCESS_REQUEST_CODE
            )
          }
        }
      } else {
        handleMapClick(latLng)
      }
    }
  }

  // Calculating distance
  private fun getDistance(lllat: Double, lllong: Double, mplat: Double, mplong: Double): Float {
    val result = FloatArray(1)

    Location.distanceBetween(lllat, lllong, mplat, mplong, result)
    return result[0]
  }

  // Permission
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

  // Location
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

  // Stop getting updates
  override fun onPause() {
    super.onPause()
    fusedLocationClient.removeLocationUpdates(locationCallback)
    sensorManager.unregisterListener(this)
  }

  // Resuming updates
  override fun onResume() {
    super.onResume()
    startLocationUpdates()
    getReports()
    sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
  }

  companion object {
    private const val LOCATION_PERMISSION_REQUEST_CODE = 1
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    return
  }

  override fun onSensorChanged(event: SensorEvent) {
    if (event.sensor?.type == Sensor.TYPE_LIGHT) {
      brightness = event.values[0]
    }
    if (event.sensor?.type == Sensor.TYPE_LINEAR_ACCELERATION) {
      var speed1 = event.values[0]
      var speed2 = event.values[1]
      var speed3 = event.values[2]

      var speed = abs(speed1) * abs(speed2) * abs(speed3)

      if (speed  < 2 ) {
        speedTV.setText("0")
      } else {
        speedTV.setText(speed.toString())
      }
    }
  }

  // Allows map styling and theming to be customized.
  private fun setMapStyle(map: GoogleMap) {
    try {
      if (brightness > 100) {
        val success = map.setMapStyle(
          MapStyleOptions.loadRawResourceStyle(
            this,
            R.raw.light_map_style
          )
        )

        if (!success) {
          Log.e("****MapStyle", "Style parsing failed.")
        }
      } else {
        val success = map.setMapStyle(
          MapStyleOptions.loadRawResourceStyle(
            this,
            R.raw.night_map_style
          )
        )

        if (!success) {
          Log.e("****MapStyle", "Style parsing failed.")
        }
      }
    } catch (e: Resources.NotFoundException) {
      Log.e("****MapStyle", "Can't find style. Error: ", e)
    }
  }
}
