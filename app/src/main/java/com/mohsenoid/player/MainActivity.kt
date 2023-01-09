package com.mohsenoid.player

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.mohsenoid.player.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupPlayer()
    }

    private fun setupPlayer() {
        val videoUri: Uri = Uri.parse("android.resource://" + packageName.toString() + "/" + R.raw.dcbln22)
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)

        val exoPlayer = ExoPlayer.Builder(this).build().apply {
            setMediaItem(mediaItem)
            prepare()
            play()
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
        }

        binding.player.player = exoPlayer
        binding.player.useController = false

        binding.title.setText(R.string.dcbln22)
    }


    override fun onPause() {
        super.onPause()
        binding.player.player?.pause()
    }

    override fun onResume() {
        super.onResume()
        binding.player.player?.play()
    }
}
