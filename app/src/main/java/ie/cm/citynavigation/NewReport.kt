package ie.cm.citynavigation

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.droidman.ktoasty.KToasty
import com.google.android.material.chip.ChipGroup
import ie.cm.citynavigation.api.Endpoints
import ie.cm.citynavigation.api.OutputNewReport
import ie.cm.citynavigation.api.ServiceBuilder
import kotlinx.android.synthetic.main.activity_new_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class NewReport : AppCompatActivity() {
  private lateinit var newReportTitleView: EditText
  private lateinit var newReportTextView: EditText
  private lateinit var newReportImage: ImageView
  private lateinit var chipGroup: ChipGroup

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

    newReportImage = findViewById(R.id.newReportImage)
  }

  //Toolbar Menu
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.new_note_menu, menu)
    return true
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.miPicture -> {
      if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(this.packageManager) != null) {
          @Suppress("DEPRECATION")
          startActivityForResult(takePictureIntent, REQUEST_CODE)
        } else {
          KToasty.error(this, getString(R.string.cantCamera), Toast.LENGTH_SHORT).show()
        }
      }
       else {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
      }

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

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
      val takenPicture = data?.extras?.get("data") as Bitmap
      newReportImage.setImageBitmap(takenPicture)
    } else {
      super.onActivityResult(requestCode, resultCode, data)
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun newReport(user_id: Int, latitude: Double, longitude: Double) {
    val titulo = newReportTitleView.text.toString()
    val descricao = newReportTextView.text.toString()
    val data = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
    val categoria_id = getCategoria()
    val imagem = getImage()

    /* WS */
    val request = ServiceBuilder.buildService(Endpoints::class.java)
    val call = request.newReport(titulo, descricao, data, imagem, latitude, longitude, user_id, categoria_id)
    call.enqueue(object : Callback<OutputNewReport> {
      override fun onResponse(call: Call<OutputNewReport>, response: Response<OutputNewReport>) {
        val r: OutputNewReport = response.body()!!

        if (!r.status) {
          KToasty.error(this@NewReport, getString(R.string.error), Toast.LENGTH_SHORT).show()
        } else {
          KToasty.success(this@NewReport, getString(R.string.reportCreated), Toast.LENGTH_SHORT)
            .show()
          finish()
        }
      }

      override fun onFailure(call: Call<OutputNewReport>, t: Throwable) {
        Log.e("****NewReport", "onFailure: ${t.message}")
        KToasty.error(this@NewReport, getString(R.string.error), Toast.LENGTH_SHORT).show()
      }
    })
  }

  private fun getCategoria(): Int {
    chipGroup = findViewById(R.id.chip_group)
    var id = chipGroup.checkedChipId

    return when (id) {
      2131361902 -> 1
      2131361901 -> 2
      2131361900 -> 3
      else -> 0
    }
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun getImage() : String {
    val image = newReportImage.drawable.toBitmap()
    val byteArrayOutputStream = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    val encodedImage: String = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray())

    return encodedImage
  }

  companion object {
    private const val CAMERA_PERMISSION_CODE = 1
    private const val REQUEST_CODE = 2
  }
}