package com.ipanardian.knotes

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.SearchView
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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
                R.menu.main_menu,
                menu
        )

        val searchView = menu?.findItem(R.id.searchMenu)?.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnSearchClickListener {
            LoadQuery("null")
        }
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    if (query.isNotEmpty()) LoadQuery("%$query%")
                    if (query.isEmpty()) LoadQuery("null")
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if (newText.length > 1) LoadQuery("%$newText%")
                    if (newText.isEmpty()) LoadQuery("null")
                }
                return false
            }
        })
        searchView.setOnCloseListener {
            LoadQuery("%")
            false
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) when (item.itemId) {
            R.id.aboutMenu -> aboutDialog()
        }
        return super.onOptionsItemSelected(item)
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

    private fun goToAdd() {
        val intent = Intent(this, ManageNotes::class.java)
        startActivity(intent)
    }

    fun goToView(note: Note) {
        val intent = Intent(this, ViewNotes::class.java)
        intent.putExtra("ID", note.id)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        LoadQuery("%")
    }

    private fun aboutDialog() {
        var aboutContent = SpannableString("k-Notes v0.1.0\n" +
                "This app is open source project\n\n" +
                "Web: http://ipanardian.com\n" +
                "Github: https://github.com/ipanardian/k-notes")
        Linkify.addLinks(aboutContent, Linkify.WEB_URLS)

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.about)
        builder.setMessage(aboutContent)
        builder.setPositiveButton(android.R.string.yes, { dialog, _ ->
            dialog.dismiss()
        })
        val alert: AlertDialog = builder.create()
        alert.show()

        var textView = alert.findViewById(android.R.id.message) as TextView
        textView.movementMethod = LinkMovementMethod.getInstance()
    }

    inner class MyNotesAdapter(private var listNotesAdapter: ArrayList<Note>) : BaseAdapter() {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val myView = layoutInflater.inflate(R.layout.note, null)
            val myNote = listNotesAdapter[position]

            val textTitle: TextView = myView.findViewById(R.id.textTitle) as TextView
            textTitle.text = myNote.title

            myView.findViewById(R.id.lyNote).setOnClickListener({
                goToView(myNote)
            })

            return myView
        }

        override fun getItem(position: Int): Any = listNotesAdapter[position]

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getCount(): Int = listNotesAdapter.size

    }
}
