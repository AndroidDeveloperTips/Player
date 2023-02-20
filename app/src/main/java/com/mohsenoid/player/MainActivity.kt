package com.mohsenoid.player

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Rational
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.mohsenoid.player.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val isPipSupported by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        } else false
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            enterPictureInPictureMode()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = getPipParams()
            enterPictureInPictureMode(params)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getPipParams(isPlaying: Boolean = false): PictureInPictureParams {
        val rect = Rect()
        binding.player.getGlobalVisibleRect(rect)

        val builder = PictureInPictureParams.Builder()
            .setAspectRatio(Rational(16, 9))
            .setSourceRectHint(rect)
            .setActions(
                listOfNotNull(
                    getPipAction()
                )
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setAutoEnterEnabled(isPlaying)
        }

        return builder.build()
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
                    setupPip(isPlaying)
                    binding.button.isVisible = isPlaying && isPipSupported
                }
            })
        }

        binding.player.player = exoPlayer

        binding.title.setText(R.string.dcbln22)
    }

    private fun setupPip(isPlaying: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = getPipParams(isPlaying)
            setPictureInPictureParams(params)
        }
    }

    private fun getPipAction(): RemoteAction? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val actionUri = Uri.parse("https://youtube.com/AndroidDeveloperTips")
            val actionIntent = Intent(Intent.ACTION_VIEW, actionUri)
            val actionPendingIntent =
                PendingIntent.getActivity(this, 0, actionIntent, PendingIntent.FLAG_IMMUTABLE)
            val remoteAction = RemoteAction(
                Icon.createWithResource(this, R.drawable.ic_picture_in_picture_action),
                "More info",
                "More info action",
                actionPendingIntent
            )
            remoteAction
        } else null
    }

    override fun onResume() {
        super.onResume()
        binding.player.player?.play()
    }

    override fun onPictureInPictureModeChanged(isInPictureInPictureMode: Boolean, newConfig: Configuration) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)
        }
        if (isInPictureInPictureMode) {
            binding.title.isVisible = false
            binding.button.isVisible = false
            binding.player.useController = false
        } else {
            binding.title.isVisible = true
            binding.button.isVisible = true
            binding.player.useController = true
        }
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // the PiP auto enter feature is not available and we should do it manually
            enterPipMode()
        }
    }

    override fun onPause() {
        super.onPause()
        val isInPipMode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            isInPictureInPictureMode
        } else false

        if (!isInPipMode) {
            binding.player.player?.pause()
        }
    }

    override fun onStop() {
        super.onStop()
        binding.player.player?.run {
            stop()
            release()
        }
    }
}
