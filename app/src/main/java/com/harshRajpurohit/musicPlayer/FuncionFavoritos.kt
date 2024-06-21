package com.harshRajpurohit.musicPlayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.harshRajpurohit.musicPlayer.databinding.ActivityFavouriteBinding

class FuncionFavoritos : AppCompatActivity() {

    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adapter: AcomodarFavoritos

    companion object{
        var favouriteSongs: ArrayList<Musica> = ArrayList()
        var favouritesChanged: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])
        binding = ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        favouriteSongs = checkPlaylist(favouriteSongs)
        binding.backBtnFA.setOnClickListener { finish() }
        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)
        binding.favouriteRV.layoutManager = GridLayoutManager(this, 4)
        adapter = AcomodarFavoritos(this, favouriteSongs)
        binding.favouriteRV.adapter = adapter

        favouritesChanged = false

        if(favouriteSongs.size < 1) binding.shuffleBtnFA.visibility = View.INVISIBLE

        if(favouriteSongs.isNotEmpty()) binding.instructionFV.visibility = View.GONE

        binding.shuffleBtnFA.setOnClickListener {
            val intent = Intent(this, ActividadUsuario::class.java)
            intent.putExtra("Indice", 0)
            intent.putExtra("Clase", "MezclaFavoritos")
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        if(favouritesChanged) {
            adapter.ActualizarFavoritos(favouriteSongs)
            favouritesChanged = false
        }
    }
}