package com.harshRajpurohit.musicPlayer

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.audiofx.LoudnessEnhancer
import android.os.*
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class ServicioMusica : Service(), AudioManager.OnAudioFocusChangeListener {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer? = null
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var runnable: Runnable
    lateinit var audioManager: AudioManager

    override fun onBind(intent: Intent?): IBinder {
        mediaSession = MediaSessionCompat(baseContext, "Mi Musica")
        return myBinder
    }

    inner class MyBinder : Binder() {
        fun currentService(): ServicioMusica {
            return this@ServicioMusica
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    fun showNotification(playPauseBtn: Int) {
        val intent = Intent(baseContext, MainActivity::class.java)

        val flag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }

        val contentIntent = PendingIntent.getActivity(this, 0, intent, flag)

        val prevIntent = Intent(
            baseContext, Notificacion::class.java
        ).setAction(Aplicacion.PREVIOUS)
        val prevPendingIntent = PendingIntent.getBroadcast(baseContext, 0, prevIntent, flag)

        val playIntent =
            Intent(baseContext, Notificacion::class.java).setAction(Aplicacion.PLAY)
        val playPendingIntent = PendingIntent.getBroadcast(baseContext, 0, playIntent, flag)

        val nextIntent =
            Intent(baseContext, Notificacion::class.java).setAction(Aplicacion.NEXT)
        val nextPendingIntent = PendingIntent.getBroadcast(baseContext, 0, nextIntent, flag)

        val exitIntent =
            Intent(baseContext, Notificacion::class.java).setAction(Aplicacion.EXIT)
        val exitPendingIntent = PendingIntent.getBroadcast(baseContext, 0, exitIntent, flag)

        val imgArt = getImgArt(ActividadUsuario.musicListPA[ActividadUsuario.songPosition].path)
        val image = if (imgArt != null) {
            BitmapFactory.decodeByteArray(imgArt, 0, imgArt.size)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.music_player_icon_slash_screen)
        }

        val notification =
            androidx.core.app.NotificationCompat.Builder(baseContext, Aplicacion.CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(ActividadUsuario.musicListPA[ActividadUsuario.songPosition].title)
                .setContentText(ActividadUsuario.musicListPA[ActividadUsuario.songPosition].artist)
                .setSmallIcon(R.drawable.music_icon).setLargeIcon(image)
                .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setVisibility(androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.previous_icon, "Anterior", prevPendingIntent)
                .addAction(playPauseBtn, "Reproducir", playPendingIntent)
                .addAction(R.drawable.next_icon, "Siguiente", nextPendingIntent)
                .addAction(R.drawable.exit_icon, "Salir", exitPendingIntent)
                .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            mediaSession.setMetadata(
                MediaMetadataCompat.Builder().putLong(
                    MediaMetadataCompat.METADATA_KEY_DURATION, mediaPlayer!!.duration.toLong()
                ).build()
            )

            mediaSession.setPlaybackState(getPlayBackState())
            mediaSession.setCallback(object : MediaSessionCompat.Callback() {

                //Se activa al se pulsar el botón de reproducción
                override fun onPlay() {
                    super.onPlay()
                    handlePlayPause()
                }

                //Se activa al pulsar el botón de pausa
                override fun onPause() {
                    super.onPause()
                    handlePlayPause()
                }

                //Se activa al pulsar el botón siguiente
                override fun onSkipToNext() {
                    super.onSkipToNext()
                    prevNextSong(increment = true, context = baseContext)
                }

                //Se activa al pulsar el boton anterior
                override fun onSkipToPrevious() {
                    super.onSkipToPrevious()
                    prevNextSong(increment = false, context = baseContext)
                }

                //se activa al pulsar los botones de los auriculares
                //actualmente sólo pausa o reproduce música al pulsar un botón
                override fun onMediaButtonEvent(mediaButtonEvent: Intent?): Boolean {
                    handlePlayPause()
                    return super.onMediaButtonEvent(mediaButtonEvent)
                }

                //Se activa cuando se cambia la barra de búsqueda
                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    mediaPlayer?.seekTo(pos.toInt())

                    mediaSession.setPlaybackState(getPlayBackState())
                }
            })
        }

        startForeground(13, notification)
    }

    fun createMediaPlayer() {
        try {
            if (mediaPlayer == null) mediaPlayer = MediaPlayer()
            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(ActividadUsuario.musicListPA[ActividadUsuario.songPosition].path)
            mediaPlayer?.prepare()

            ActividadUsuario.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
            showNotification(R.drawable.pause_icon)
            ActividadUsuario.binding.tvSeekBarStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            ActividadUsuario.binding.tvSeekBarEnd.text =
                formatDuration(mediaPlayer!!.duration.toLong())
            ActividadUsuario.binding.seekBarPA.progress = 0
            ActividadUsuario.binding.seekBarPA.max = mediaPlayer!!.duration
            ActividadUsuario.nowPlayingId = ActividadUsuario.musicListPA[ActividadUsuario.songPosition].id
            ActividadUsuario.loudnessEnhancer = LoudnessEnhancer(mediaPlayer!!.audioSessionId)
            ActividadUsuario.loudnessEnhancer.enabled = true
        } catch (e: Exception) {
            return
        }
    }

    fun seekBarSetup() {
        runnable = Runnable {
            ActividadUsuario.binding.tvSeekBarStart.text =
                formatDuration(mediaPlayer!!.currentPosition.toLong())
            ActividadUsuario.binding.seekBarPA.progress = mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable, 200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable, 0)
    }

    fun getPlayBackState(): PlaybackStateCompat {
        val playbackSpeed = if (ActividadUsuario.isPlaying) 1F else 0F

        return PlaybackStateCompat.Builder().setState(
            if (mediaPlayer?.isPlaying == true) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED,
            mediaPlayer!!.currentPosition.toLong(), playbackSpeed)
            .setActions(PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_SEEK_TO or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
            .build()
    }

    fun handlePlayPause() {
        if (ActividadUsuario.isPlaying) pauseMusic()
        else playMusic()

        //Actualiza el estado de reproducción para la notificación
        mediaSession.setPlaybackState(getPlayBackState())
    }

    private fun prevNextSong(increment: Boolean, context: Context){

        setSongPosition(increment = increment)

        ActividadUsuario.musicService?.createMediaPlayer()
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

        //Actualiza el estado de reproducción para la notificación
        mediaSession.setPlaybackState(getPlayBackState())
    }

    override fun onAudioFocusChange(focusChange: Int) {
        if (focusChange <= 0) {
            pauseMusic()
        }
    }

    private fun playMusic(){
        //Reproduce la musica
        ActividadUsuario.binding.playPauseBtnPA.setIconResource(R.drawable.pause_icon)
        Reproduciendo.binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
        ActividadUsuario.isPlaying = true
        mediaPlayer?.start()
        showNotification(R.drawable.pause_icon)
    }

    private fun pauseMusic(){
        //Pausa la musica
        ActividadUsuario.binding.playPauseBtnPA.setIconResource(R.drawable.play_icon)
        Reproduciendo.binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
        ActividadUsuario.isPlaying = false
        mediaPlayer!!.pause()
        showNotification(R.drawable.play_icon)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}