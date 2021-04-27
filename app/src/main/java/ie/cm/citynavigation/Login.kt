package ie.cm.citynavigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.droidman.ktoasty.KToasty
import ie.cm.citynavigation.api.Endpoints
import ie.cm.citynavigation.api.OutputLogin
import ie.cm.citynavigation.api.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {
  private lateinit var emailView: EditText
  private lateinit var passwordView: EditText
  private lateinit var loginProgressView: ProgressBar

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    // Inputs
    emailView = findViewById(R.id.emailText)
    passwordView = findViewById(R.id.passwordText)
    loginProgressView = findViewById(R.id.loginProgress)

    // Shared Preferences
    val sharedPref: SharedPreferences = getSharedPreferences(
      getString(R.string.preference_file_key),
      Context.MODE_PRIVATE
    )

    val isLogged = sharedPref.getBoolean(getString(R.string.logged), false)
    if (isLogged) {
      val i = Intent(this, MainActivity::class.java)
      startActivity(i)
      finish()
    }

    //  Temp code for navigation between activities
    val withoutLogin: TextView = findViewById(R.id.withoutLogin)
    withoutLogin.setOnClickListener {
      val i = Intent(this, MainActivity::class.java)
      startActivity(i)
      finish()
    }
  }

  fun login(view: View) {
    val email = emailView.text.toString()
    val password = passwordView.text.toString()

    loginProgressView.visibility = View.VISIBLE

    if (!(TextUtils.isEmpty(email) || TextUtils.isEmpty(password))) {
      val request = ServiceBuilder.buildService(Endpoints::class.java)
      val call = request.login(email, password)

      call.enqueue(object : Callback<OutputLogin> {
        override fun onResponse(call: Call<OutputLogin>, response: Response<OutputLogin>) {
          val c: OutputLogin = response.body()!!
          Log.d("****Login", c.toString())

          if (c.id == 0) {
            KToasty.error(this@Login, getString(R.string.loginFailed), Toast.LENGTH_SHORT).show()
            loginProgressView.visibility = View.GONE
          } else {
            val sharedPref: SharedPreferences = getSharedPreferences(
              getString(R.string.preference_file_key),
              Context.MODE_PRIVATE
            )
            with(sharedPref.edit()) {
              putBoolean(getString(R.string.logged), true)
              putInt(getString(R.string.userId), c.id)
              putString(getString(R.string.userEmail), c.email)
              putString(getString(R.string.userName), c.nome)
              commit()
            }

            val i = Intent(this@Login, MainActivity::class.java)
            startActivity(i)
            finish()
          }
        }

        override fun onFailure(call: Call<OutputLogin>, t: Throwable) {
          Log.e("****Login", "${t.message}")
        }
      })
    } else {
      KToasty.info(this, getString(R.string.emptyFields), Toast.LENGTH_SHORT).show()
      loginProgressView.visibility = View.GONE
    }
    closeKeyBoard()
  }

  private fun closeKeyBoard() {
    val view = this.currentFocus
    if (view != null) {
      val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
      imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
  }
}