package com.harshRajpurohit.musicPlayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
import com.harshRajpurohit.musicPlayer.databinding.ActivityPlaylistDetailsBinding

class DetallesListaReproduccion : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistDetailsBinding
    private lateinit var adapter: AcomodarMusica

    companion object{
        var currentPlaylistPos: Int = -1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityPlaylistDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        currentPlaylistPos = intent.extras?.get("Indice") as Int
        try{ListaReproduccion.musicPlaylist.ref[currentPlaylistPos].playlist =
            checkPlaylist(playlist = ListaReproduccion.musicPlaylist.ref[currentPlaylistPos].playlist)}
            catch(e: Exception){}
        binding.playlistDetailsRV.setItemViewCacheSize(10)
        binding.playlistDetailsRV.setHasFixedSize(true)
        binding.playlistDetailsRV.layoutManager = LinearLayoutManager(this)
        adapter = AcomodarMusica(this, ListaReproduccion.musicPlaylist.ref[currentPlaylistPos].playlist, playlistDetails = true)
        binding.playlistDetailsRV.adapter = adapter
        binding.backBtnPD.setOnClickListener { finish() }
        binding.shuffleBtnPD.setOnClickListener {
            val intent = Intent(this, ListaReproduccion::class.java)
            intent.putExtra("Indice", 0)
            intent.putExtra("Clase", "DetallesListaDeReproduccion")
            startActivity(intent)
        }
        binding.addBtnPD.setOnClickListener {
            startActivity(Intent(this, SeleccionarCancion::class.java))
        }
        binding.removeAllPD.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Remover")
                .setMessage("¿Quieres eliminar todas las canciones de la lista de reproducción?")
                .setPositiveButton("Si"){ dialog, _ ->
                    ListaReproduccion.musicPlaylist.ref[currentPlaylistPos].playlist.clear()
                    adapter.refreshPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

            setDialogBtnBackground(this, customDialog)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        binding.playlistNamePD.text = ListaReproduccion.musicPlaylist.ref[currentPlaylistPos].name
        binding.moreInfoPD.text = "Total ${adapter.itemCount} Canciones.\n\n" +
                "Creado el:\n${ListaReproduccion.musicPlaylist.ref[currentPlaylistPos].createdOn}\n\n" +
                "  -- ${ListaReproduccion.musicPlaylist.ref[currentPlaylistPos].createdBy}"
        if(adapter.itemCount > 0)
        {
            Glide.with(this)
                .load(ListaReproduccion.musicPlaylist.ref[currentPlaylistPos].playlist[0].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(binding.playlistImgPD)
            binding.shuffleBtnPD.visibility = View.VISIBLE
        }
        adapter.notifyDataSetChanged()
        //Para almacenar datos de favoritos utilizando preferencias compartidas
        val editor = getSharedPreferences("FAVOURITOS", MODE_PRIVATE).edit()
        val jsonStringPlaylist = GsonBuilder().create().toJson(ListaReproduccion.musicPlaylist)
        editor.putString("ListaDeReproduccionDeMusica", jsonStringPlaylist)
        editor.apply()
    }
}