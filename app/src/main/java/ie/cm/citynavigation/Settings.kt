package ie.cm.citynavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Settings : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_settings)

    setSupportActionBar(findViewById(R.id.settingsToolbar))
    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setDisplayShowHomeEnabled(true)
      setDisplayShowTitleEnabled(true)
    }
  }
}