package ie.cm.citynavigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun openMap(view: View) {
        val m = Intent(this, Map::class.java)
        startActivity(m)
    }

    fun openNoteList(view: View) {
        val n = Intent(this, NoteList::class.java)
        startActivity(n)
    }

    fun openLogin(view: View) {
        val l = Intent(this, Login::class.java)
        startActivity(l)
        finish()
    }

    fun openSettings(view: View) {
        val s = Intent(this, Settings::class.java)
        startActivity(s)
    }
}