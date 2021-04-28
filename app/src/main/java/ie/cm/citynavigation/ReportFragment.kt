package ie.cm.citynavigation

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import android.widget.ViewFlipper
import com.droidman.ktoasty.KToasty
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ie.cm.citynavigation.api.*
import kotlinx.android.synthetic.main.fragment_report.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.InputStream
import java.net.URL


class ReportFragment : BottomSheetDialogFragment() {
  private lateinit var report: List<Report>
  private var isLogged: Boolean = false
  private var userId: Int = 0
  private var reportId: Int = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    // Inflate the layout for this fragment
    val root = inflater.inflate(R.layout.fragment_report, container, false)

    report = listOf<Report>()

    userId = (arguments?.getInt("USER") ?: Int) as Int
    isLogged = (arguments?.getBoolean("LOGGED") ?: Boolean) as Boolean
    reportId = (arguments?.getInt("REPORT") ?: Int) as Int

    getReport(reportId, root)

    // Buttons
    root.editReport.setOnClickListener {
      Intent(activity, EditReport::class.java). also {
        it.putExtra("REPORT", reportId)
        startActivity(it)
      }
    }

    return root
  }

  private fun getReport(reportId : Int, root: View) {
    // Call service and add the markers
    val request = ServiceBuilder.buildService(Endpoints::class.java)
    val call = request.getReport(reportId)

    call.enqueue(object : Callback<List<Report>> {
      override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {
        if (response.isSuccessful) {
          report = response.body()!!
          fillFields(root, report)
        } else {
          Toast.makeText(activity, getString(R.string.error), Toast.LENGTH_SHORT).show()
        }
      }

      override fun onFailure(call: Call<List<Report>>, t: Throwable) {
        Log.e("****Mapa", "${t.message}")
      }
    })
  }


  private fun fillFields(root: View, report: List<Report>) {
    // Button Visibility
    if (!isLogged || userId != report[0].user_id) {
      root.editReport.visibility = View.GONE
    }

    // Report info
    root.reportTitle.text = report[0].titulo
    root.reportText.text = report[0].descricao
    root.reportCategoria.text = when (report[0].categoria_id) {
      1 -> getString(R.string.road)
      2 -> getString(R.string.nature)
      3 -> getString(R.string.construction)
      else -> getString(R.string.error)
    }

    if (Build.VERSION.SDK_INT > 9) {
      val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
      StrictMode.setThreadPolicy(policy)
    }
    val imageUrl = "https://citynavigation.000webhostapp.com/citynavigation/images/" + report[0].imagem
    Log.d("****Frag", imageUrl)
    val inputStream: InputStream = URL(imageUrl).openStream()
    val bitmap = BitmapFactory.decodeStream(inputStream)
    root.reportImage.setImageBitmap(bitmap)
  }
}