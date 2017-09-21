package com.ipanardian.knotes

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.database.Cursor
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast

class ViewNotes : AppCompatActivity() {
    var id: Int = 0
    var tvTitle: TextView? = null
    var tvDescription: TextView? = null
    var note: Note? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_notes)

        tvTitle = findViewById(R.id.tvTitle) as TextView
        tvDescription = findViewById(R.id.tvDescription) as TextView

        val bundle: Bundle = intent.extras
        id = bundle.getInt("ID")
        if (id != 0) {
            note = getNote(id)

            if (note != null) {
                tvTitle?.setText(note?.title)
                tvDescription?.setText(note?.description)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
                R.menu.view_menu,
                menu
        )

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) when (item.itemId) {
            R.id.editNote -> goToUpdate(note!!)
            R.id.deleteNote -> deleteAction(id)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()

        if (id != 0) {
            note = getNote(id)

            if (note != null) {
                tvTitle?.setText(note?.title)
                tvDescription?.setText(note?.description)
            }
        }
    }

    fun getNote(id: Int?): Note? {
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

    fun goToUpdate(note: Note) {
        val intent = Intent(this, ManageNotes::class.java)
        intent.putExtra("ID", note.id)
        startActivity(intent)
    }

    fun deleteAction(id: Int?) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure want to delete this note?")
        builder.setPositiveButton(android.R.string.yes, { dialog, _ ->
            val dbManager = DbManager(this)
            val selectionArgs = arrayOf(id.toString())
            val isDeleted = dbManager.Delete("ID = ?", selectionArgs)
            if (isDeleted > 0) Toast.makeText(applicationContext, "Note deleted", Toast.LENGTH_LONG).show()
            dialog.dismiss()
            finish()
        })
        builder.setNegativeButton(android.R.string.no, { dialog, _ ->
            dialog.cancel()
        })
        val alert: AlertDialog = builder.create()
        alert.show()
    }
}
