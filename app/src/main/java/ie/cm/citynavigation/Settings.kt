package ie.cm.citynavigation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText

class Settings : AppCompatActivity() {
  private lateinit var radiusText: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    setSupportActionBar(findViewById(R.id.settingsToolbar))
    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setDisplayShowHomeEnabled(true)
      setDisplayShowTitleEnabled(true)
    }

    val sharedPref: SharedPreferences = getSharedPreferences(
      getString(R.string.preference_file_key),
      Context.MODE_PRIVATE
    )

    val radius = sharedPref.getInt(getString(R.string.geofenceradius), 200)

    radiusText = findViewById(R.id.geofenceRadiusText)
    radiusText.setText(radius.toString())
  }

  fun setOptions(view: View) {
    val radius = radiusText.text.toString().toInt()
    Log.d("****Settings", radius.toString())
    // Shared Preferences
    val sharedPref: SharedPreferences = getSharedPreferences(
      getString(R.string.preference_file_key),
      Context.MODE_PRIVATE
    )
    with(sharedPref.edit()) {
      putInt(getString(R.string.geofenceRadius), radius)
      commit()
    }

    val i = Intent(this@Settings, MainActivity::class.java)
    startActivity(i)
    finish()
  }
}
