package ie.cm.citynavigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Login : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)

    //  Temp code for navigation between activities
    val button: Button = findViewById(R.id.loginButton)
    button.setOnClickListener {
      val i = Intent(this, MainActivity::class.java)
      startActivity(i)
    }
    val withoutLogin: TextView = findViewById(R.id.withoutLogin)
    withoutLogin.setOnClickListener {
      val i = Intent(this, MainActivity::class.java)
      startActivity(i)
    }
  }
}