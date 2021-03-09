package ie.cm.citynavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import ie.cm.citynavigation.adapter.NoteCardAdapter
import ie.cm.citynavigation.viewModel.NoteViewModel

class EditNote : AppCompatActivity() {
  private lateinit var editNoteTitleView: EditText
  private lateinit var editNoteTextView: EditText

  private lateinit var noteViewModel: NoteViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_edit_note)

    //ViewModel
    noteViewModel = ViewModelProvider(this).get(NoteViewModel::class.java)

    //Toolbar
    setSupportActionBar(findViewById(R.id.editNoteToolbar))

    //Inputs
    editNoteTitleView = findViewById(R.id.editNoteTitleText)
    editNoteTextView = findViewById(R.id.editNoteTextText)

    editNoteTitleView.setText(intent.getStringExtra(NoteCardAdapter.noteTitle))
    editNoteTextView.setText(intent.getStringExtra(NoteCardAdapter.noteText))
  }

  //Menu da toolbar
  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    val inflater: MenuInflater = menuInflater
    inflater.inflate(R.menu.edit_note_menu, menu)

    return true
  }

  override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
    R.id.miDelete -> {
      val noteId = intent.getStringExtra(NoteCardAdapter.noteId)
      noteViewModel.deleteById(noteId.toString())
      Log.d("***aaa", "miDelete")
      finish()
      true
    }
    R.id.miSave -> {
      val noteId = intent.getStringExtra(NoteCardAdapter.noteId)
      noteViewModel.updateById(
        editNoteTitleView.text.toString(),
        editNoteTextView.text.toString(),
        noteId.toString()
      )
      Log.d("***aaa", "midone")
      finish()
      true
    }
    else -> super.onOptionsItemSelected(item)
  }

  companion object {
    const val EXTRA_REPLY = "com.example.andorid.wordlistsql.REPLY"
  }
}