package com.example.notesapplication

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DatabaseReference

class NotesAdapter(    private val mContext: Context,
                       private val notesList : List<Note>,
                       private val refNotes:DatabaseReference
    )
    :RecyclerView.Adapter<NotesAdapter.CardDesignHolder> ()
{
    inner class CardDesignHolder(design: View) : RecyclerView.ViewHolder(design) {
        var noteOrder: TextView
        var popupImage: ImageView

        init {
            noteOrder = design.findViewById(R.id.note_order_info)
            popupImage = design.findViewById(R.id.popup_image)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardDesignHolder {
        val design =
            LayoutInflater.from(mContext).inflate(R.layout.person_card_design, parent, false)
        return CardDesignHolder(design)
    }

    override fun getItemCount() : Int {
        return notesList.size
    }

    override fun onBindViewHolder(holder: CardDesignHolder, position: Int) {

        val note = notesList.get(position)
        holder.noteOrder.text = "${note.note_id}:  ${note.note_name}"

        holder.popupImage.setOnClickListener{

            val popupMenu = PopupMenu(mContext,holder.popupImage)
            popupMenu.menuInflater.inflate(R.menu.popup_menu,popupMenu.menu)
            popupMenu.setOnMenuItemClickListener{ menuItem ->


                when(menuItem.itemId){
                                        R.id.action_delete -> {
                                            val snackbar= Snackbar.make(holder.popupImage,"${note.note_name} WANT TO DELETE?",Snackbar.LENGTH_INDEFINITE)
                                            snackbar.setAction("YES"){
                                                   refNotes.child(note.note_id!!).removeValue()
                                                }
                                            snackbar.duration = 10000
                                            snackbar.show()
                                            true
                                        }

                                            R.id.action_update -> {
                                                alertShow(note)// row 45
                                                true
                                            }


                    else -> false
                }
            }

            popupMenu.show()
            }

            }


    fun alertShow(note:Note){
        val design = LayoutInflater.from(mContext).inflate(R.layout.alert_design,null)
        val updateId = design.findViewById(R.id.updateOrAdd_id) as EditText
        val updateName = design.findViewById(R.id.updateOrAdd_name) as EditText

        updateId.setText(note.note_id)
        updateName.setText(note.note_name)

        val ad = AlertDialog.Builder(mContext)

        ad.setTitle("Update note")
        ad.setView(design)
        ad.setPositiveButton("Update"){ dialogInterface, i ->

            val noteId = updateId.text.toString().trim()
            val noteName = updateName.text.toString().trim()

            val info = HashMap<String,Any>()
            info.put("note_id",noteId)
            info.put("note_name",noteName)

            Toast.makeText(mContext,"updated", Toast.LENGTH_SHORT).show()

            refNotes.child(note.note_id!!).updateChildren(info)// bu consolda değil sadece android studioda güncellemek istersek de yazmak zorundayız

        }
        ad.setNegativeButton("Cancel"){dialogInterface, i ->
        }
        ad.create().show()
    }






        }





