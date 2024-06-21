package com.harshRajpurohit.musicPlayer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class Notificacion:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            //Reproducir sólo la canción siguiente o anterior, cuando la lista de música contiene más de una canción
            Aplicacion.PREVIOUS -> if(ActividadUsuario.musicListPA.size > 1) prevNextSong(increment = false, context = context!!)
            Aplicacion.PLAY -> if(ActividadUsuario.isPlaying) pauseMusic() else playMusic()
            Aplicacion.NEXT -> if(ActividadUsuario.musicListPA.size > 1) prevNextSong(increment = true, context = context!!)
            Aplicacion.EXIT ->{
                exitApplication()
            }
        }
    }
    private fun playMusic(){
        ActividadUsuario.isPlaying = true
        ActividadUsuario.musicService!!.mediaPlayer!!.start()
        ActividadUsuario.musicService!!.showNotification(R.drawable.pause_icon)
        ActividadUsuario.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        try{ Reproduciendo.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon) }catch (_: Exception){}
    }

    private fun pauseMusic(){
        ActividadUsuario.isPlaying = false
        ActividadUsuario.musicService!!.mediaPlayer!!.pause()
        ActividadUsuario.musicService!!.showNotification(R.drawable.play_icon)
        ActividadUsuario.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        try{ Reproduciendo.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon) }catch (_: Exception){}
    }

    private fun prevNextSong(increment: Boolean, context: Context){
        setSongPosition(increment = increment)
        ActividadUsuario.musicService!!.createMediaPlayer()
        Glide.with(context)
            .load(ActividadUsuario.musicListPA[ActividadUsuario.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
            .into(ActividadUsuario.binding.songImgPA)
        ActividadUsuario.binding.songNamePA.text = ActividadUsuario.musicListPA[ActividadUsuario.songPosition].title
        Glide.with(context)
            .load(ActividadUsuario.musicListPA[ActividadUsuario.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
            .into(Reproduciendo.binding.songImgNP)
        Reproduciendo.binding.songNameNP.text = ActividadUsuario.musicListPA[ActividadUsuario.songPosition].title
        playMusic()
        ActividadUsuario.fIndex = favouriteChecker(ActividadUsuario.musicListPA[ActividadUsuario.songPosition].id)
        if(ActividadUsuario.isFavourite) ActividadUsuario.binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
        else ActividadUsuario.binding.favouriteBtnPA.setImageResource(R.drawable.favourite_empty_icon)
    }
}