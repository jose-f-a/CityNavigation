package ie.cm.citynavigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class EditNote : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        intent.getStringArrayExtra(EXTRA_REPLY)
        Log.d("***Teste", EXTRA_REPLY.toString())
    }
    //intent.getStringArrayExtra(EXTRA_DETAILS)

    companion object {
        const val EXTRA_REPLY = "com.example.android.wordlistsql.REPLY"
    }
}