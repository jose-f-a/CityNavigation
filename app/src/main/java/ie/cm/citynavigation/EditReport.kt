package ie.cm.citynavigation

import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.droidman.ktoasty.KToasty
import com.google.android.material.chip.ChipGroup
import ie.cm.citynavigation.adapter.NoteCardAdapter
import ie.cm.citynavigation.api.*
import kotlinx.android.synthetic.main.fragment_report.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class EditReport : AppCompatActivity() {
  private lateinit var editReportTitleView: EditText
  private lateinit var editReportTextView: EditText
  private lateinit var editReportImage: ImageView
  private lateinit var editReportChipGroup: ChipGroup

  private var reportId: Int = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_report)

    reportId = intent.getIntExtra("REPORT", 0)

    //Toolbar
    setSupportActionBar(findViewById(R.id.editReportToolbar))

    //Inputs
    editReportTitleView = findViewById(R.id.editReportTitleText)
    editReportTextView = findViewById(R.id.editReportTextText)
    editReportImage = findViewById(R.id.editReportImage)
    editReportChipGroup = findViewById(R.id.editReportChipGroup)

    getReport(reportId)
  }

  private fun getReport(reportId: Int) {
    // Call service and add the markers
    val request = ServiceBuilder.buildService(Endpoints::class.java)
    val call = request.getReport(reportId)
    var report: List<Report>

    call.enqueue(object : Callback<List<Report>> {
      override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {
        if (response.isSuccessful) {
          report = response.body()!!
          fillFields(report)
        } else {
          KToasty.warning(this@EditReport, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
      }

      override fun onFailure(call: Call<List<Report>>, t: Throwable) {
        Log.e("****Mapa", "${t.message}")
      }
    })
  }

  //Menu da toolbar
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.edit_note_menu, menu)

    return true
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.miDelete -> {
      deleteReport(reportId)
      finish()
      true
    }
    R.id.miSave -> {
      if (TextUtils.isEmpty(editReportTitleView.text) || TextUtils.isEmpty(editReportTextView.text)) {
        Toast.makeText(this, R.string.noteNotUpdated, Toast.LENGTH_SHORT).show()
      } else {
        updateReport(reportId)
        finish()
      }

      true
    }
    else -> super.onOptionsItemSelected(item)
  }

  private fun deleteReport(reportId: Int) {
    // Call service and add the markers
    val request = ServiceBuilder.buildService(Endpoints::class.java)
    val call = request.deleteReport(reportId)

    call.enqueue(object : Callback<OutputDeleteReport> {
      override fun onResponse(
        call: Call<OutputDeleteReport>,
        response: Response<OutputDeleteReport>
      ) {
        val r: OutputDeleteReport = response.body()!!

        if (!r.status) {
          KToasty.error(this@EditReport, getString(R.string.error), Toast.LENGTH_SHORT).show()
        } else {
          KToasty.success(this@EditReport, getString(R.string.reportDeleted), Toast.LENGTH_SHORT)
            .show()
          finish()
        }
      }

      override fun onFailure(call: Call<OutputDeleteReport>, t: Throwable) {
        Log.e("****Mapa", "${t.message}")
      }
    })
  }

  private fun updateReport(reportId: Int) {
    val titulo = editReportTitleView.text.toString()
    val descricao = editReportTextView.text.toString()
    val categoria_id = setCategoria()

    /* WS */
    val request = ServiceBuilder.buildService(Endpoints::class.java)
    val call = request.editReport(reportId, titulo, descricao, categoria_id)
    call.enqueue(object : Callback<OutputEditReport> {
      override fun onResponse(call: Call<OutputEditReport>, response: Response<OutputEditReport>) {
        val r: OutputEditReport = response.body()!!

        if (!r.status) {
          KToasty.error(this@EditReport, getString(R.string.error), Toast.LENGTH_SHORT).show()
        } else {
          KToasty.success(this@EditReport, getString(R.string.reportUpdated), Toast.LENGTH_SHORT)
            .show()
          finish()
        }
      }

      override fun onFailure(call: Call<OutputEditReport>, t: Throwable) {
        Log.e("****NewReport", "onFailure: ${t.message}")
        KToasty.error(this@EditReport, getString(R.string.error), Toast.LENGTH_SHORT).show()
      }
    })
  }

  private fun fillFields(report: List<Report>) {
    // Report info
    editReportTitleView.setText(report[0].titulo)
    editReportTextView.setText(report[0].descricao)
    editReportChipGroup.check(getCategoria(report[0].categoria_id))

    if (Build.VERSION.SDK_INT > 9) {
      val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
      StrictMode.setThreadPolicy(policy)
    }
    val imageUrl =
      "https://citynavigation.000webhostapp.com/citynavigation/images/" + report[0].imagem
    Log.d("****Frag", imageUrl)
    val inputStream: InputStream = URL(imageUrl).openStream()
    val bitmap = BitmapFactory.decodeStream(inputStream)
    editReportImage.setImageBitmap(bitmap)
  }

  private fun getCategoria(categoriaId: Int): Int {
    return when (categoriaId) {
      1 -> 2131361902
      2 -> 2131361901
      3 -> 2131361900
      else -> 0
    }
  }

  private fun setCategoria(): Int {
    val id = editReportChipGroup.checkedChipId
    return when (id) {
      2131361902 -> 1
      2131361901 -> 2
      2131361900 -> 3
      else -> 0
    }
  }
}