package ie.cm.citynavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class Map : AppCompatActivity() {

  private lateinit var tempbtn: Button

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_map)

    setSupportActionBar(findViewById(R.id.homeToolbar))
    supportActionBar?.apply {
      setDisplayHomeAsUpEnabled(true)
      setDisplayShowHomeEnabled(true)
      setDisplayShowTitleEnabled(true)
    }

    tempbtn = findViewById(R.id.tempbtn)
    val reportFragment = ReportFragment()

    tempbtn.setOnClickListener {
      reportFragment.show(supportFragmentManager, "aaa")
    }
  }
}