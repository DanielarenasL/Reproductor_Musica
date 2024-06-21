package com.harshRajpurohit.musicPlayer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.harshRajpurohit.musicPlayer.databinding.FavouriteViewBinding
import com.harshRajpurohit.musicPlayer.databinding.MoreFeaturesBinding

class AcomodarFavoritos(private val context: Context, private var musicList: ArrayList<Musica>,val playNext: Boolean = false) : RecyclerView.Adapter<AcomodarFavoritos.MyHolder>() {

    class MyHolder(binding: FavouriteViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.songImgFV
        val name = binding.songNameFV
        val root = binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(FavouriteViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text = musicList[position].title
        Glide.with(context)
            .load(musicList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
            .into(holder.image)

        //Cuando se pulsa reproducir siguiente cancion
        if(playNext){
            holder.root.setOnClickListener {
                val intent = Intent(context, ActividadUsuario::class.java)
                intent.putExtra("Indice", position)
                intent.putExtra("Clase", "ReproduccirSiguiente")
                ContextCompat.startActivity(context, intent, null)
            }
            holder.root.setOnLongClickListener {
                val customDialog = LayoutInflater.from(context).inflate(R.layout.more_features, holder.root, false)
                val bindingMF = MoreFeaturesBinding.bind(customDialog)
                val dialog = MaterialAlertDialogBuilder(context).setView(customDialog)
                    .create()
                dialog.show()
                dialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
                bindingMF.AddToPNBtn.text = "Remover"
                bindingMF.AddToPNBtn.setOnClickListener {
                    if(position == ActividadUsuario.songPosition)
                        Snackbar.make((context as Activity).findViewById(R.id.linearLayoutPN),
                            "No se puede eliminar la canción porque se está reproduciendo.", Snackbar.LENGTH_SHORT).show()
                    else{
                        if(ActividadUsuario.songPosition < position && ActividadUsuario.songPosition != 0) --ActividadUsuario.songPosition
                        ReproduccirSiguiente.playNextList.removeAt(position)
                        ActividadUsuario.musicListPA.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    dialog.dismiss()
                }
                return@setOnLongClickListener true
            }
        }
        else{
            holder.root.setOnClickListener {
                val intent = Intent(context, ActividadUsuario::class.java)
                intent.putExtra("Indice", position)
                intent.putExtra("Clase", "AcomodarFavoritos")
                ContextCompat.startActivity(context, intent, null)
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateFavorites(newList: ArrayList<Musica>){
        musicList = ArrayList()
        musicList.addAll(newList)
        notifyDataSetChanged()
    }
}