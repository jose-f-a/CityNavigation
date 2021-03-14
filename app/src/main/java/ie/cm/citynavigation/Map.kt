package ie.cm.citynavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Map : AppCompatActivity(), OnMapReadyCallback {

  private lateinit var tempbtn: Button
  private lateinit var mMap: GoogleMap

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

    //Butao temporario
    tempbtn = findViewById(R.id.tempbtn)
    val reportFragment = ReportFragment()

    tempbtn.setOnClickListener {
      reportFragment.show(supportFragmentManager, "aaa")
    }
  }

  override fun onMapReady(googleMap: GoogleMap) {
    mMap = googleMap

    // Add a marker in Sydney and move the camera
    val ESGT = LatLng(41.693408, -8.846684)
    mMap.addMarker(MarkerOptions().position(ESGT).title("Marker in ESTG"))
    mMap.moveCamera(CameraUpdateFactory.newLatLng(ESGT))
  }
}