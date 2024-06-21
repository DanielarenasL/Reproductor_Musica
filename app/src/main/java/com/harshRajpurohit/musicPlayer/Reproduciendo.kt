package com.harshRajpurohit.musicPlayer

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.harshRajpurohit.musicPlayer.databinding.FragmentNowPlayingBinding

class Reproduciendo : Fragment() {

    companion object{
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentNowPlayingBinding
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.currentTheme[MainActivity.themeIndex], true)
        val view = inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding = FragmentNowPlayingBinding.bind(view)
        binding.root.visibility = View.INVISIBLE
        binding.playPauseBtnNP.setOnClickListener {
            if(ActividadUsuario.isPlaying) pauseMusic() else playMusic()
        }
        binding.nextBtnNP.setOnClickListener {
            setSongPosition(increment = true)
            ActividadUsuario.musicService!!.createMediaPlayer()
            Glide.with(requireContext())
                .load(ActividadUsuario.musicListPA[ActividadUsuario.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text = ActividadUsuario.musicListPA[ActividadUsuario.songPosition].title
            ActividadUsuario.musicService!!.showNotification(R.drawable.pause_icon)
            playMusic()
        }
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(), ActividadUsuario::class.java)
            intent.putExtra("index", ActividadUsuario.songPosition)
            intent.putExtra("class", "NowPlaying")
            ContextCompat.startActivity(requireContext(), intent, null)
        }
        return view
    }

    override fun onResume() {
        super.onResume()
        if(ActividadUsuario.musicService != null){
            binding.root.visibility = View.VISIBLE
            binding.songNameNP.isSelected = true
            Glide.with(requireContext())
                .load(ActividadUsuario.musicListPA[ActividadUsuario.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.music_player_icon_slash_screen).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text = ActividadUsuario.musicListPA[ActividadUsuario.songPosition].title
            if(ActividadUsuario.isPlaying) binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
            else binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
        }
    }

    private fun playMusic(){
        ActividadUsuario.isPlaying = true
        ActividadUsuario.musicService!!.mediaPlayer!!.start()
        binding.playPauseBtnNP.setIconResource(R.drawable.pause_icon)
        ActividadUsuario.musicService!!.showNotification(R.drawable.pause_icon)
    }
    private fun pauseMusic(){
        ActividadUsuario.isPlaying = false
        ActividadUsuario.musicService!!.mediaPlayer!!.pause()
        binding.playPauseBtnNP.setIconResource(R.drawable.play_icon)
        ActividadUsuario.musicService!!.showNotification(R.drawable.play_icon)
    }
}