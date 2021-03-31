package ie.cm.citynavigation

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.children
import com.droidman.ktoasty.KToasty
import com.google.android.material.chip.Chip
import ie.cm.citynavigation.api.Endpoints
import ie.cm.citynavigation.api.OutputLogin
import ie.cm.citynavigation.api.OutputNewReport
import ie.cm.citynavigation.api.ServiceBuilder
import kotlinx.android.synthetic.main.activity_new_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class NewReport : AppCompatActivity() {
  private lateinit var newReportTitleView: EditText
  private lateinit var newReportTextView: EditText

  private var userId: Int = 0
  private var lat: Double = 0.0
  private var lng: Double = 0.0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_new_report)

    // Getting data from previous activity
    lat = intent.getDoubleExtra("LAT", 0.0)
    lng = intent.getDoubleExtra("LNG", 0.0)
    userId = intent.getIntExtra("USERID", 0)

    if (userId == 0) {
      finish()
    }

    //Toolbar
    setSupportActionBar(findViewById(R.id.newReportToolbar))
    supportActionBar?.setTitle(R.string.newReport)

    //Inputs
    newReportTitleView = findViewById(R.id.newReportTitleText)
    newReportTextView = findViewById(R.id.newReportTextText)
  }

  //Toolbar Menu
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.new_note_menu, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.miPicture -> {
      true
    }
    R.id.miDone -> {
      val replyIntent = Intent()
      if (TextUtils.isEmpty(newReportTitleView.text) || TextUtils.isEmpty(newReportTextView.text)) {
        KToasty.warning(this, getString(R.string.emptyFields), Toast.LENGTH_SHORT).show()
      } else {
        newReport(userId, lat, lng)
      }
      true
    }
    R.id.miCancel -> {
      finish()
      true
    }
    else -> {
      super.onOptionsItemSelected(item)
    }
  }

  private fun newReport(user_id: Int, lat: Double, lng: Double) {
    val titulo = newReportTitleView.text.toString()
    val descricao = newReportTextView.text.toString()
    val data = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    //val categoria_id = chip_group.checkedChipId
    val categoria_id = 1
    Log.d("****NewReport", "Categoria: $categoria_id")

    val request = ServiceBuilder.buildService(Endpoints::class.java)
    val call = request.newReport(titulo, descricao, data, lat, lng, categoria_id, user_id)

    call.enqueue(object : Callback<OutputNewReport> {
      override fun onResponse(call: Call<OutputNewReport>, response: Response<OutputNewReport>) {
        val r: OutputNewReport = response.body()!!
        Log.d("****NewReport", r.toString())

        if (!r.status) {
          KToasty.error(this@NewReport, getString(R.string.error), Toast.LENGTH_SHORT).show()
        } else {
          KToasty.success(this@NewReport, getString(R.string.reportCreated), Toast.LENGTH_SHORT).show()
          finish()
        }
      }

      override fun onFailure(call: Call<OutputNewReport>, t: Throwable) {
        Log.e("****NewReport", "onFailure: ${t.message}")
        KToasty.error(this@NewReport, getString(R.string.loginFailed), Toast.LENGTH_SHORT).show()
      }
    })
  }
}