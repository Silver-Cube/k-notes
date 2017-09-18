package com.ipanardian.noteapp

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*

class MainActivity : AppCompatActivity() {

    var listNotes = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        LoadQuery("%")
    }

    fun LoadQuery(title: String ) {
        val dbManager = DbManager(this)
        val projections = arrayOf("ID", "Title", "Description")
        val selectionArgs = arrayOf(title)
        val cursor = dbManager.Query(
                projections,
                "Title LIKE ?",
                selectionArgs,
                "ID DESC"
        )

        listNotes.clear()

        if (cursor.moveToFirst()) {
            do {
                val ID = cursor.getInt(cursor.getColumnIndex("ID"))
                val Title = cursor.getString((cursor.getColumnIndex("Title")))
                val Description = cursor.getString(cursor.getColumnIndex("Description"))

                listNotes.add(Note(ID, Title, Description))
            } while (cursor.moveToNext())
        }

        var myNotesAdapter = MyNotesAdapter(this, listNotes)

        val listView: ListView = findViewById(R.id.lvNotes) as ListView
        listView.adapter = myNotesAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
                R.menu.main_menu,
                menu
        )
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.addNotes -> {
                    var intent = Intent(this, AddNotes::class.java)
                    startActivity(intent)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun goToUpdate(note: Note) {
        val intent = Intent(this, AddNotes::class.java)
        intent.putExtra("ID", note.id)
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        LoadQuery("%")
    }

    fun createAndShowAlertDialog(context: Context?, note: Note) {
        var builder: AlertDialog.Builder = AlertDialog.Builder(context!!)
        builder.setTitle("Confirm Delete")
        builder.setMessage("Are you sure want to delete this note?")
        builder.setPositiveButton(android.R.string.yes, { dialog, which ->
            var dbManager = DbManager(context!!)
            val selectionArgs = arrayOf(note.id.toString())
            dbManager.Delete("ID = ?", selectionArgs)
            LoadQuery("%")
            dialog.dismiss()
        })
        builder.setNegativeButton(android.R.string.no, { dialog, which ->
            dialog.cancel()
        })
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    inner class MyNotesAdapter: BaseAdapter {
        var context: Context? = null
        var listNotesAdapter = ArrayList<Note>()

        constructor(context: Context, listNotesAdapter: ArrayList<Note>): super() {
            this.context = context
            this.listNotesAdapter = listNotesAdapter
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var myView = layoutInflater.inflate(R.layout.ticket, null)
            var myNote = listNotesAdapter[position]

            var textTitle: TextView = myView.findViewById(R.id.textTitle) as TextView
            textTitle.text = myNote.title

//            myView.findViewById(R.id.btnDelete).setOnClickListener({
//                createAndShowAlertDialog(this.context, myNote)
//            })

            myView.findViewById(R.id.ivEdit).setOnClickListener({
                goToUpdate(myNote)
            })

            return myView
        }

        override fun getItem(position: Int): Any {
            return listNotesAdapter[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return listNotesAdapter.size
        }

    }
}
