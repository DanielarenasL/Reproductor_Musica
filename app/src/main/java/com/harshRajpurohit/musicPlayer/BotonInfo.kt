package com.harshRajpurohit.musicPlayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.harshRajpurohit.musicPlayer.databinding.ActivityAboutBinding

class BotonInfo : AppCompatActivity() {

    lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Acerca De"
        binding.aboutText.text = aboutText()
    }
    private fun aboutText(): String{
        return "Diseñado por: Brahyan Uribe(Oconer49) y Carlos Arenas"
    }
}