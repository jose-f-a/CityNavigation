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

class NewReport : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_new_report)

    // Getting the coordinates from previous activity
    val coord = intent.getStringExtra(Map.COORD)
    if (coord != null) {
      Log.d("****NewNote", coord)
    } else {
       finish()
    }

    //Toolbar
    setSupportActionBar(findViewById(R.id.newReportToolbar))
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
      /*if (TextUtils.isEmpty(newNoteTitleView.text) || TextUtils.isEmpty(newNoteTextView.text)) {
        setResult(Activity.RESULT_CANCELED, replyIntent)
      } else {
        val noteTitle = newNoteTitleView.text.toString()
        val noteText = newNoteTextView.text.toString()

        replyIntent.putExtra(NewNote.EXTRA_REPLY, arrayOf(noteTitle, noteText))

        setResult(Activity.RESULT_OK, replyIntent)
      }*/
      finish()
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
}