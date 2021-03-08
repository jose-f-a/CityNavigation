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
import android.widget.EditText
import ie.cm.citynavigation.adapter.NoteCardAdapter

class EditNote : AppCompatActivity() {
    private lateinit var editNoteTitleView: EditText
    private lateinit var editNoteTextView: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

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
        inflater.inflate(R.menu.new_note_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.miDelete -> {
            finish()
            true
        }
        R.id.miSave -> {
            finish()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }
}