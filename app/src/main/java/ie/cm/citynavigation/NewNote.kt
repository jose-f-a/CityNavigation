package ie.cm.citynavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText

class NewNote : AppCompatActivity() {
  private lateinit var newNoteTitleView: EditText
  private lateinit var newNoteTextView: EditText

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_new_note)

    //Toolbar
    setSupportActionBar(findViewById(R.id.newNoteToolbar))

    //Inputs
    newNoteTitleView = findViewById(R.id.newNoteTitleText)
    newNoteTextView = findViewById(R.id.newNoteTextText)
  }

  //Menu da toolbar
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
          if (TextUtils.isEmpty(newNoteTitleView.text) || TextUtils.isEmpty(newNoteTextView.text)) {
              setResult(Activity.RESULT_CANCELED, replyIntent)
          } else {
              val noteTitle = newNoteTitleView.text.toString()
              val noteText = newNoteTextView.text.toString()

              replyIntent.putExtra(EXTRA_REPLY, arrayOf(noteTitle, noteText))

              setResult(Activity.RESULT_OK, replyIntent)
          }
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

  companion object {
    const val EXTRA_REPLY = "com.example.andorid.wordlistsql.REPLY"
  }
}