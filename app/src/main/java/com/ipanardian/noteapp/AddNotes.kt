package com.ipanardian.noteapp

import android.content.ContentValues
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class AddNotes : AppCompatActivity() {

    var id: Int = 0
    var etTitle: EditText? = null
    var etDescription: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_notes)

        etTitle = findViewById(R.id.etTitle) as EditText
        etDescription = findViewById(R.id.etDes) as EditText

        if (intent.hasExtra("ID")) {
            var bundle: Bundle = intent.extras
            id = bundle.getInt("ID")
            if (id != 0) {
                var note: Note? = this.getNote(id)

                if (note != null) {
                    etTitle?.setText(note.title)
                    etDescription?.setText(note.description)
                }
            }
        }

    }

    private fun getNote(id: Int?): Note? {
        val dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(id!!.toString())
        var note: Note? = null
        var cursor: Cursor? = null

        try {
            cursor = dbManager.Query(
                    projections,
                    "ID = ?",
                    selectionArgs,
                    "ID"
            )
            var title = ""
            var description = ""

            if (cursor.count > 0) {
                cursor.moveToFirst()
                title = cursor.getString(cursor.getColumnIndex("Title"))
                description = cursor.getString(cursor.getColumnIndex("Description"))

                note = Note(id, title, description)
            }
        }
        finally {
            cursor?.close()
        }

        return note
    }

    fun saveAction(view: View) {
        var dbManager = DbManager(this)
        var values = ContentValues()

        if (etTitle?.text.toString().equals("")) {
            etTitle?.requestFocus()
            Toast.makeText(applicationContext, "Title can not be empty", Toast.LENGTH_LONG).show()
            return
        }
        if (etDescription?.text.toString().equals("")) {
            etDescription?.requestFocus()
            Toast.makeText(applicationContext, "Description can not be empty", Toast.LENGTH_LONG).show()
            return
        }

        values.put("Title", etTitle?.text.toString())
        values.put("Description", etDescription?.text.toString())

        if (id == 0) {
            val ID = dbManager.Insert(values)

            if (ID > 0) {
                Toast.makeText(applicationContext, "Notes added", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(applicationContext, "Can not added notes", Toast.LENGTH_LONG).show()
            }
        }
        else {
            val selectionArgs = arrayOf(id.toString())
            val ID = dbManager.Update(values, "ID = ?", selectionArgs)

            if (ID > 0) {
                Toast.makeText(applicationContext, "Notes updated", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(applicationContext, "Can not update notes", Toast.LENGTH_LONG).show()
            }
        }

        finish()
    }
}
