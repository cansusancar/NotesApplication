package com.example.notesapplication

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesapplication.databinding.ActivityMainBinding
import com.example.notesapplication.databinding.AlertDesignBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener{
private lateinit var binding: ActivityMainBinding
    private lateinit var notesList: ArrayList<Note>
    private lateinit var adapter: NotesAdapter
    private lateinit var refNotes: DatabaseReference//burayı ekledik
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Notes Application"
        setSupportActionBar(binding.toolbar)

        binding.rv.setHasFixedSize(true)
        binding.rv.layoutManager= LinearLayoutManager(this)

        notesList= ArrayList()


        /*
        // consolda id (key)'yi aşağıdaki kodlarla biz belirleyebiliriz:
        // bunun için refNotes ile alakalı tüm kodlar silinmeli ama
        //val db = FirebaseDatabase.getInstance().getReference("Notes")
        //val key= db.child("Note5")
        //val note = Note("note5","Yeni not")
        //key.setValue(note)
        //notesList.add(note)
        */

        val db = FirebaseDatabase.getInstance()
        refNotes = db.getReference("Notes")


        //val note = Note("1","first note")
        //val note2 = Note("2","second note")
        //notesList.add(note)
        //notesList.add(note2)
        adapter = NotesAdapter(this,notesList,refNotes)
        binding.rv.adapter = adapter
        tumKisiler()



        binding.add.setOnClickListener {
            alertShow()
        }

    }
    fun tumKisiler() {

        refNotes.addValueEventListener(object :ValueEventListener

        {
            override fun onDataChange(snapshot: DataSnapshot) {

                notesList.clear ()
                for ( data in snapshot.children) {

            val note = data.getValue(Note::class.java)
            if (note != null) {
                note.note_id = data.key
                notesList.add(note)
            }
        }
            adapter.notifyDataSetChanged()
        }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }



        })

    }
    fun alertShow(){
        val design = LayoutInflater.from(this).inflate(R.layout.alert_design,null)
        val updateId = design.findViewById(R.id.updateOrAdd_id) as EditText
        val updateName = design.findViewById(R.id.updateOrAdd_name) as EditText


        val ad = AlertDialog.Builder(this)

        ad.setTitle("Add note")
        ad.setView(design)
        ad.setPositiveButton("Add"){ dialogInterface, i ->

            val noteId = updateId.text.toString().trim()
            val noteName = updateName.text.toString().trim()

            val info = HashMap<String,Any>()
            info.put("note_id",noteId)
            info.put("note_name",noteName)
            refNotes.push().updateChildren(info)
            Toast.makeText(this,"added", Toast.LENGTH_SHORT).show()



        }
        ad.setNegativeButton("Cancel"){dialogInterface, i ->
        }
        ad.create().show()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)

        val item = menu?.findItem(R.id.action_search)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }
    fun searching(searchWord:String){
        refNotes.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                notesList.clear()

                for (data in snapshot.children){
                    val note =data.getValue(Note::class.java)

                    if (note != null){
                        if (note.note_name!!.contains(searchWord)){
                            note.note_id= data.key
                            notesList.add(note)
                        }
                    }
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    override fun onQueryTextSubmit(query: String): Boolean {
      searching(query)
        Log.e("sended search",query.toString())
        return true
    }

    override fun onQueryTextChange(newText: String): Boolean {
   searching(newText)
        Log.e("as letters are typed",newText.toString())
        return true
    }



}