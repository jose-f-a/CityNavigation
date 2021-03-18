package ie.cm.citynavigation

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView

class MainActivity : AppCompatActivity() {
  private lateinit var relativeView: RelativeLayout
  private lateinit var customNameView: TextView

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    relativeView = findViewById(R.id.customText)
    customNameView = findViewById(R.id.customName)

    // Shared Preferences
    val sharedPref: SharedPreferences = getSharedPreferences(
      getString(R.string.preference_file_key),
      Context.MODE_PRIVATE
    )

    // Checking is a user is logged in and putting is name on the screen
    val isLogged = sharedPref.getBoolean(getString(R.string.logged), false)
    val userName = sharedPref.getString(getString(R.string.userName), getString(R.string.stranger))
    if (isLogged) {
      customNameView.text = userName
      relativeView.visibility = View.VISIBLE
    }
  }

  fun openMap(view: View) {
    val m = Intent(this, Map::class.java)
    startActivity(m)
  }

  fun openNoteList(view: View) {
    val n = Intent(this, NoteList::class.java)
    startActivity(n)
  }

  fun logout(view: View) {
    // Removing user data from shared preferences
    val sharedPref: SharedPreferences = getSharedPreferences(
      getString(R.string.preference_file_key),
      Context.MODE_PRIVATE
    )
    with(sharedPref.edit()) {
      putBoolean(getString(R.string.logged), false)
      putInt(getString(R.string.userId), 0)
      putString(getString(R.string.userEmail), null)
      putString(getString(R.string.userName), null)
      commit()
    }

    val l = Intent(this, Login::class.java)
    startActivity(l)
    finish()
  }

  fun openSettings(view: View) {
    val s = Intent(this, Settings::class.java)
    startActivity(s)
  }
}