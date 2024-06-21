 package com.harshRajpurohit.musicPlayer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import androidx.appcompat.app.AlertDialog
import com.google.android.material.color.MaterialColors
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess


data class Musica(val id:String, val title:String, val album:String ,val artist:String, val duration: Long = 0, val path: String,
val artUri:String)

class Playlist{
    lateinit var name: String
    lateinit var playlist: ArrayList<Musica>
    lateinit var createdBy: String
    lateinit var createdOn: String
}
class MusicPlaylist{
    var ref: ArrayList<Playlist> = ArrayList()
}

fun formatDuration(duration: Long):String{
    val minutes = TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS)
    val seconds = (TimeUnit.SECONDS.convert(duration, TimeUnit.MILLISECONDS) -
            minutes*TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES))
    return String.format("%02d:%02d", minutes, seconds)
}
fun getImgArt(path: String): ByteArray? {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture
}
fun setSongPosition(increment: Boolean){
    if(!ActividadUsuario.repeat){
        if(increment)
        {
            if(ActividadUsuario.musicListPA.size - 1 == ActividadUsuario.songPosition)
                ActividadUsuario.songPosition = 0
            else ++ActividadUsuario.songPosition
        }else{
            if(0 == ActividadUsuario.songPosition)
                ActividadUsuario.songPosition = ActividadUsuario.musicListPA.size-1
            else --ActividadUsuario.songPosition
        }
    }
}
fun exitApplication(){
    if(ActividadUsuario.musicService != null){
        ActividadUsuario.musicService!!.audioManager.abandonAudioFocus(ActividadUsuario.musicService)
        ActividadUsuario.musicService!!.stopForeground(true)
        ActividadUsuario.musicService!!.mediaPlayer!!.release()
        ActividadUsuario.musicService = null}
    exitProcess(1)
}

fun favouriteChecker(id: String): Int{
    ActividadUsuario.isFavourite = false
    FuncionFavoritos.favouriteSongs.forEachIndexed { Indice, musica ->
        if(id == musica.id){
            ActividadUsuario.isFavourite = true
            return Indice
        }
    }
    return -1
}
fun checkPlaylist(playlist: ArrayList<Musica>): ArrayList<Musica>{
    playlist.forEachIndexed { index, music ->
        val file = File(music.path)
        if(!file.exists())
            playlist.removeAt(index)
    }
    return playlist
}

 fun setDialogBtnBackground(context: Context, dialog: AlertDialog){
     //setting button text
     dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setTextColor(
         MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
     )
     dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setTextColor(
         MaterialColors.getColor(context, R.attr.dialogTextColor, Color.WHITE)
     )

     //setting button background
     dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)?.setBackgroundColor(
         MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
     )
     dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)?.setBackgroundColor(
         MaterialColors.getColor(context, R.attr.dialogBtnBackground, Color.RED)
     )
 }

 fun getMainColor(img: Bitmap): Int {
     val newImg = Bitmap.createScaledBitmap(img, 1,1 , true)
     val color = newImg.getPixel(0, 0)
     newImg.recycle()
     return color
 }