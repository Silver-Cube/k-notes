package com.ipanardian.noteapp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private var listNotes = ArrayList<Note>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fabAddNote = findViewById(R.id.fabAddNote) as FloatingActionButton
        fabAddNote.setOnClickListener { _ ->
            this.goToAdd()
        }

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

        val myNotesAdapter = MyNotesAdapter(listNotes)

        val listView: ListView = findViewById(R.id.lvNotes) as ListView
        listView.adapter = myNotesAdapter
    }

    fun goToAdd() {
        val intent = Intent(this, ManageNotes::class.java)
        startActivity(intent)
    }

    fun goToUpdate(note: Note) {
        val intent = Intent(this, ManageNotes::class.java)
        intent.putExtra("ID", note.id)
        startActivity(intent)
    }

    override fun onRestart() {
        super.onRestart()
        LoadQuery("%")
    }

    inner class MyNotesAdapter(private var listNotesAdapter: ArrayList<Note>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.note, null)
            val myNote = listNotesAdapter[position]

            val textTitle: TextView = myView.findViewById(R.id.textTitle) as TextView
            textTitle.text = myNote.title

            myView.findViewById(R.id.lyNote).setOnClickListener({
                goToUpdate(myNote)
            })

            return myView
        }

        override fun getItem(position: Int): Any = listNotesAdapter[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int = listNotesAdapter.size

    }
}
