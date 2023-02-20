package com.mohsenoid.player

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.mohsenoid.player.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val isPipSupported by lazy {
        // TODO
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setupPipButton()
    }

    private fun setupPipButton() {
        binding.button.isVisible = isPipSupported
        binding.button.setOnClickListener { enterPipMode() }
    }

    private fun enterPipMode() {
        // TODO
    }

    override fun onStart() {
        super.onStart()
        setupPlayer()
    }

    private fun setupPlayer() {
        val videoUri: Uri = Uri.parse("android.resource://" + packageName.toString() + "/" + R.raw.dcbln22)
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)

        val exoPlayer = ExoPlayer.Builder(this).build().apply {
            setMediaItem(mediaItem)
            prepare()
            repeatMode = ExoPlayer.REPEAT_MODE_ONE
            addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    binding.button.isVisible = isPlaying && isPipSupported
                    // TODO: setupPip(isPlaying)
                }
            })
        }

        binding.player.player = exoPlayer

        binding.title.setText(R.string.dcbln22)
    }

    override fun onResume() {
        super.onResume()
        binding.player.player?.play()
    }

    override fun onPause() {
        super.onPause()
        binding.player.player?.pause()
    }

    override fun onStop() {
        super.onStop()
        binding.player.player?.run {
            stop()
            release()
        }
    }
}
