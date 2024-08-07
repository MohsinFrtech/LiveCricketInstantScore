package com.ruthal.live.cricket.app.ui.activities

import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.MimeTypes
import com.ruthal.live.cricket.app.R
import com.ruthal.live.cricket.app.admanager.AdManager
import com.ruthal.live.cricket.app.appinterfaces.AdManagerListener
import com.ruthal.live.cricket.app.constants.ApplicationConstants
import com.ruthal.live.cricket.app.constants.ApplicationConstants.adLocation2topPermanent
import com.ruthal.live.cricket.app.constants.ApplicationConstants.location2BottomProvider
import com.ruthal.live.cricket.app.constants.ApplicationConstants.location2TopPermanentProvider
import com.ruthal.live.cricket.app.constants.UnchangedConstants
import com.ruthal.live.cricket.app.constants.UnchangedConstants.adAfter
import com.ruthal.live.cricket.app.constants.UnchangedConstants.adLocation2bottom
import com.ruthal.live.cricket.app.constants.UnchangedConstants.typeFlussonic
import com.ruthal.live.cricket.app.constants.UnchangedConstants.userCdn
import com.ruthal.live.cricket.app.databinding.ExoPlaybackControlViewBinding
import com.ruthal.live.cricket.app.databinding.StreamingPlayerBinding
import com.ruthal.live.cricket.app.utils.DebugChecker
import com.ruthal.live.cricket.app.utils.StreamingUtils
import com.ruthal.live.cricket.app.viewmodels.StreamingViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.Executors


class StreamingScreen : AppCompatActivity(), Player.Listener, AdManagerListener {


    ///Local class Variables....
    private var binding: StreamingPlayerBinding? = null
    private var path = ""
    private var channel_Type = ""
    private var baseUrl = ""

    //    private var mCastSession: CastSession? = null
    private var player: ExoPlayer? = null
    private var adStatus = false
    private var viewCount = 0
    private var mLocation: PlaybackLocation? = null

    //    private var mCastContextTask: Task<CastContext>? = null
//    private var mCastContext: CastContext? = null
    private var lockCounter = 0
    private var isLockMode: Boolean = true
    private var mediaRouteMenuItem: MenuItem? = null
    private val viewModel by lazy {
        ViewModelProvider(this)[StreamingViewModel::class.java]
    }
    private var context: Context? = null
    private val castExecutor: Executor = Executors.newSingleThreadExecutor()
    private var mPlaybackState: PlaybackState? = null
    private var count = 1
    private var orientationEventListener: OrientationEventListener? = null
    private var bindingExoPlayback: ExoPlaybackControlViewBinding? = null
    private var adManager: AdManager? = null
    private var booleanVpn: Boolean? = false

    ///Playback location
    enum class PlaybackLocation {
        LOCAL, REMOTE
    }

    //Checking player state...
    enum class PlaybackState {
        PLAYING, IDLE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.streaming_player)
        context = this
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )

        val exoView: ConstraintLayout? = binding?.playerView?.findViewById(R.id.exoControlView)
        bindingExoPlayback = exoView?.let { ExoPlaybackControlViewBinding.bind(it) }
        mLocation = PlaybackLocation.LOCAL
        setupActionBar()
        changeOrientation()
//        initializeCastSdk()
        getNavValues()
        checkForAds()
        screenModeController()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                if (adStatus) {
                    if (!ApplicationConstants.locationAfter.equals("none", true)) {
                        adManager?.showAds(ApplicationConstants.locationAfter)
                    }
                } else {
                    ApplicationConstants.videoFinish = true
                    finish()
                }
            }
        })

    }

    private fun getNavValues() {
        try {
            if (intent != null) {
                baseUrl = intent.getStringExtra("base").toString()
                path = intent.getStringExtra("append").toString()
                channel_Type = intent.getStringExtra("type").toString()

                if (channel_Type.equals(typeFlussonic, true)) {

                    if (path.isNotEmpty()) {
                        setUpPlayer(path)
                    }
                } else if (channel_Type.equals(userCdn, true)) {
                    binding?.lottiePlayer?.visibility = View.VISIBLE

                    viewModel?.connectionWithCdnApi()
                    viewModel.userLink.observe(this) {
                        if (it.isNotEmpty()) {
                            path = it
                            lifecycleScope.launch(Dispatchers.Main) {
                                setUpPlayer(path)
                            }
                        }
                    }
                }
            }

        } catch (e: Exception) {
            Log.d("Exception", "message")
        }

    }

    private fun checkForAds() {
        adManager = context?.let { AdManager(it, this, this) }
        binding?.adViewTop?.let { it1 ->
            binding?.fbAdViewTop?.let { it2 ->
                adManager?.loadAdProvider(
                    ApplicationConstants.locationAfter, adAfter,
                    it1, it2, binding?.unityBannerView, binding?.startAppBannerTop
                )
            }
        }

        loadLocation2TopPermanentProvider()
        loadLocation2BottomProvider()
    }

    private fun loadLocation2BottomProvider() {
        if (!location2BottomProvider.equals("none", true)) {
            binding?.adViewBottom?.let { it1 ->
                binding?.fbAdViewBottom?.let { it2 ->
                    adManager?.loadAdProvider(
                        location2BottomProvider, adLocation2bottom,
                        it1, it2, binding?.unityBannerViewBottom, binding?.startAppBannerBottom
                    )
                }
            }
        }
    }

    private fun loadLocation2TopPermanentProvider() {
        if (!location2TopPermanentProvider.equals("none", true)) {
            binding?.adViewTopPermanent?.let { it1 ->
                binding?.fbAdViewTop?.let { it2 ->
                    adManager?.loadAdProvider(
                        location2TopPermanentProvider, adLocation2topPermanent,
                        it1, it2, binding?.unityBannerView, binding?.startAppBannerTop
                    )
                }
            }
        }
    }

    private fun screenModeController() {
        bindingExoPlayback?.fullScreenIcon?.setOnClickListener {
            when (count) {
                1 -> {
                    setImageViewListener("Fit", R.drawable.fit_mode)
                }

                2 -> {
                    setImageViewListener("Fill", R.drawable.full_mode)
                }

                3 -> {
                    setImageViewListener("Stretch", R.drawable.stretch)
                }

                4 -> {
                    setImageViewListener("Original", R.drawable.ic_full_screen)
                }
            }
        }
        bindingExoPlayback?.lockMode?.setOnClickListener {
            if (lockCounter == 0) {
                isLockMode = false
                viewVisibility(View.GONE)
                bindingExoPlayback?.lockAffect?.visibility = View.VISIBLE
                lockCounter++
            } else {
                viewVisibility(View.VISIBLE)
                isLockMode = true
                bindingExoPlayback?.lockAffect?.visibility = View.GONE
                lockCounter = 0
            }

        }
    }

    private fun viewVisibility(value: Int) {
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            bindingExoPlayback?.fullScreenIcon?.visibility = View.GONE
        } else {
            bindingExoPlayback?.fullScreenIcon?.visibility = value

        }

        bindingExoPlayback?.seekProgress?.visibility = value
        bindingExoPlayback?.exoPlayPause?.visibility = value

    }


    ////Change orientation of screen programmatically......
    private fun changeOrientation() {
        Thread {
            orientationEventListener =
                object : OrientationEventListener(this) {
                    override fun onOrientationChanged(orientation: Int) {
                        val leftLandscape = 90
                        val rightLandscape = 270
                        runOnUiThread {
                            if (epsilonCheck(orientation, leftLandscape) ||
                                epsilonCheck(orientation, rightLandscape)
                            ) {
                                if (isLockMode) {
                                    requestedOrientation =
                                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                                }
                            } else {
                                if (isLockMode) {


                                    if (orientation in 0..45 || orientation >= 315 || orientation in 135..225) {
                                        requestedOrientation =
                                            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                    }

                                }
                            }
                        }
                    }

                    private fun epsilonCheck(a: Int, b: Int): Boolean {
                        return a > b - 10 && a < b + 10
                    }
                }
            orientationEventListener?.enable()

        }.start()

    }


    override fun onDestroy() {
        super.onDestroy()
        try {
            orientationEventListener?.disable()
            if (player != null) {
                player!!.release()
                player = null

            }
        } catch (e: Exception) {
            Log.d("Exception", "msg")
        }

    }

    ///
    private fun setImageViewListener(string: String, id: Int) {
        if (binding?.playerView != null) {

            bindingExoPlayback?.layoutRight?.visibility = View.VISIBLE
            val animFadeIn =
                AnimationUtils.loadAnimation(
                    applicationContext,
                    R.anim.fade_in
                )
            bindingExoPlayback?.fullScreenIcon?.startAnimation(animFadeIn)
            when (string) {
                "Fit" -> {
                    binding?.playerView?.resizeMode =
                        AspectRatioFrameLayout.RESIZE_MODE_FILL
                    count++

                }

                "Fill" -> {
                    binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    player?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                    count++
                }

                "Stretch" -> {
                    binding?.playerView?.resizeMode =
                        AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    player?.videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                    count++
                }

                else -> {
                    binding?.playerView?.resizeMode =
                        AspectRatioFrameLayout.RESIZE_MODE_FIT

                    count = 1
                }
            }

            Handler(Looper.getMainLooper()).postDelayed({
                bindingExoPlayback?.layoutRight?.visibility = View.GONE
                changeImageDrawable(id, bindingExoPlayback?.fullScreenIcon)
            }, 500)

        }
        bindingExoPlayback?.chnagedText?.visibility = View.VISIBLE
        bindingExoPlayback?.chnagedText?.text = string
        Handler(Looper.getMainLooper()).postDelayed(
            { bindingExoPlayback?.chnagedText?.visibility = View.GONE },
            2000
        )

    }

    override fun onPlayerError(error: PlaybackException) {
        if (player != null) {
            player?.playWhenReady = false
            player?.stop()
            player?.release()
        }

        againPlay()
    }

    private fun againPlay() {
        try {
            if (channel_Type.equals(typeFlussonic, true)) {
                val token = baseUrl.let { it1 -> StreamingUtils.improveDeprecatedCode(it1) }
                path = baseUrl + token
                setUpPlayer(path)
            }

        } catch (e: Exception) {

        }
    }


    private fun setupActionBar() {
        setSupportActionBar(binding?.myToolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
//        menuInflater.inflate(R.menu.menu, menu)
//        mediaRouteMenuItem = CastButtonFactory.setUpMediaRouteButton(
//            applicationContext,
//            menu,
//            R.id.media_route_menu_item
//        )
        return true
    }

    ///
    fun changeImageDrawable(drawable: Int, imageView: ImageView?) {
        imageView?.setImageDrawable(context?.let { ContextCompat.getDrawable(it, drawable) })
    }

    private fun setUpPlayer(link: String?) {
        binding?.lottiePlayer?.visibility = View.GONE

        val meter: BandwidthMeter = DefaultBandwidthMeter.Builder(this).build()
        val trackSelector: TrackSelector = DefaultTrackSelector(this)
        // 2. Create a default LoadControl
        player = null
        val loadControl: LoadControl = DefaultLoadControl()
        player = context?.let {
            ExoPlayer.Builder(it)
                .setBandwidthMeter(meter)
                .setTrackSelector(trackSelector)
                .setLoadControl(loadControl)
                .build()
        }
        binding?.playerView?.player = player
        binding?.playerView?.keepScreenOn = true
        //Initialize data source factory
        val defaultDataSourceFactory = DefaultDataSource.Factory(this)
        val mediaItem2 = MediaItem.Builder()
            .setUri(link)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()

        //Initialize hlsMediaSource
        val hlsMediaSource: HlsMediaSource =
            HlsMediaSource.Factory(defaultDataSourceFactory).createMediaSource(mediaItem2)
        val concatenatedSource = ConcatenatingMediaSource(hlsMediaSource)

        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            bindingExoPlayback?.fullScreenIcon?.setImageDrawable(
                context?.let {
                    ContextCompat.getDrawable(
                        it,
                        R.drawable.ic_full_screen
                    )
                }
            )
            count = 1
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }

        when (mLocation) {
            PlaybackLocation.LOCAL -> {
//                if (mCastSession != null && mCastSession?.remoteMediaClient != null) {
//                    mCastSession?.remoteMediaClient?.stop()
//                    mCastContext?.sessionManager?.endCurrentSession(true)
//                }
                mPlaybackState =
                    PlaybackState.IDLE

                if (player != null) {
                    player?.addListener(this)
                    player?.setMediaSource(concatenatedSource)
                    player?.prepare()
                    binding?.playerView?.requestFocus()
                    player?.playWhenReady = true
                }

            }

            PlaybackLocation.REMOTE -> {
//                mCastSession?.remoteMediaClient?.play()
                mPlaybackState =
                    PlaybackState.PLAYING
            }

            else -> {
            }
        }
        setOnGestureListeners()
    }

    ////Load remote media when connected with casting device
//    private fun loadRemoteMedia() {
//        if (mCastSession == null) {
//            return
//        }
//        val remoteMediaClient = mCastSession!!.remoteMediaClient ?: return
//        remoteMediaClient.registerCallback(object : RemoteMediaClient.Callback() {
//            override fun onStatusUpdated() {
//                val intent =
//                    Intent(context, ExpendedActivity::class.java)
//                startActivity(intent)
//                remoteMediaClient.unregisterCallback(this)
//            }
//        })
//        val loadData = MediaLoadRequestData.Builder()
//            .setMediaInfo(buildMediaInfo())
//            .build()
//        remoteMediaClient.load(loadData)
////        buildMediaInfo()?.let { remoteMediaClient.load(loadData) }
//    }


    override fun onPause() {
        super.onPause()
        if (player != null) {
            player?.playWhenReady = false

        }
//        Chartboost.onPause(this)
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
//        if (mCastSession != null) {
////            mCastContext?.sessionManager?.addSessionManagerListener(
////                this, CastSession::class.java
////            )
//            if (mCastSession != null && mCastSession!!.isConnected) {
//
//                if (player != null) {
//                    player?.playWhenReady = false
//                    player?.release()
//                }
//
////                try {
////                    lifecycleScope.launch(Dispatchers.Main) {
////
////                        setUpPlayer(path)
////
////                    }
////                } catch (e: Exception) {
////                    Log.d("Exception", "msg")
////                }
//            } else {
//
//                updatePlaybackLocation()
//            }
//        } else {
//
//        }

        if (player != null) {
            if (DebugChecker.checkDebugging(this)) {
                player?.playWhenReady = false
                player?.stop()

            } else {
                player?.playWhenReady = true

            }
        }

        checkVpn()
    }

    private fun checkVpn() {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                    val booleanVpnCheck = hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                    booleanVpn = booleanVpnCheck == true
                }
            } else {
                booleanVpn = false
            }
        }

        if (booleanVpn != null) {
            if (booleanVpn!!) {
                if (binding?.adblockLayout?.isVisible!!) {
                    /////////

                } else {
                    binding?.adblockLayout?.visibility = View.VISIBLE

                }
            } else {
                binding?.adblockLayout?.visibility = View.GONE

            }
        }

    }



    /////Media builder function
//    private fun buildMediaInfo(): MediaInfo? {
//        return path?.let {
//            MediaInfo.Builder(it)
//                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
//                .setContentType("application/x-mpegURL")
//                .build()
//        }
//    }

    ///Hide System bar.....
    private fun hideSystemUI() {
        // Set the content to appear under the system bars so that the content
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.decorView.let {
            WindowInsetsControllerCompat(window, it).let { controller ->
                controller.hide(WindowInsetsCompat.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (binding?.playerView != null) {
            val orientation = newConfig.orientation

            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                binding?.adViewTop?.removeAllViews()
                binding?.fbAdViewTop?.removeAllViews()
                binding?.unityBannerView?.removeAllViews()
                binding?.startAppBannerTop?.removeAllViews()

                loadLocation2TopPermanentProvider()
                loadLocation2BottomProvider()
                bindingExoPlayback?.fullScreenIcon?.visibility = View.GONE
                binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                binding?.adViewTopPermanent?.removeAllViews()
                binding?.adViewBottom?.removeAllViews()
                binding?.fbAdViewBottom?.removeAllViews()
                binding?.unityBannerViewBottom?.removeAllViews()
                binding?.startAppBannerBottom?.removeAllViews()

                if (!ApplicationConstants.location2TopProvider.equals("none", true)) {
                    binding?.adViewTop?.let { it1 ->
                        binding?.fbAdViewTop?.let { it2 ->
                            adManager?.loadAdProvider(
                                ApplicationConstants.location2TopProvider,
                                UnchangedConstants.adLocation2top,
                                it1,
                                it2,
                                binding?.unityBannerView,
                                binding?.startAppBannerTop
                            )
                        }
                    }
                }

                if (bindingExoPlayback?.lockAffect?.visibility == View.VISIBLE) {
                    //
                    bindingExoPlayback?.fullScreenIcon?.visibility = View.GONE


                } else {

                    bindingExoPlayback?.fullScreenIcon?.visibility = View.VISIBLE
                    binding?.playerView?.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    bindingExoPlayback?.fullScreenIcon?.setImageDrawable(
                        context?.let {
                            ContextCompat.getDrawable(
                                it,
                                R.drawable.ic_full_screen
                            )
                        }
                    )
                    count = 1

                }

            }
        }

    }


    ////Listener for castSession manager
    private fun setupCastListener() {
//        if (mSessionManagerListener == null) {
//            mSessionManagerListener = object : SessionManagerListener<CastSession> {
//                override fun onSessionStarting(castSession: CastSession) {
//
//                }
//
//                override fun onSessionStarted(castSession: CastSession, s: String) {
//                    onApplicationConnected(castSession)
//                }
//
//                override fun onSessionStartFailed(castSession: CastSession, i: Int) {
////                    Log.d("player_error", "" + "failed1")
//                    onApplicationDisconnected()
//                }
//
//                override fun onSessionEnding(castSession: CastSession) {
//
//                }
//
//                override fun onSessionEnded(castSession: CastSession, i: Int) {
//                    onApplicationDisconnected()
////                    Log.d("player_error", "" + "failed2")
//                }
//
//                override fun onSessionResuming(castSession: CastSession, s: String) {}
//                override fun onSessionResumed(castSession: CastSession, b: Boolean) {
//                    onApplicationConnected(castSession)
//                }
//
//                override fun onSessionResumeFailed(castSession: CastSession, i: Int) {
////                    Log.d("player_error", "" + "failed3")
//                    onApplicationDisconnected()
//                }
//
//                override fun onSessionSuspended(castSession: CastSession, i: Int) {
//
//                }
//
//
//            }
//        }


    }


    private fun updatePlaybackLocation() {

        mLocation = PlaybackLocation.LOCAL
    }

//    private fun onApplicationConnected(castSession: CastSession) {
//        mCastSession = castSession
//
//        if (mPlaybackState == PlaybackState.IDLE) {
//            if (mPlaybackState != PlaybackState.PLAYING) {
//                loadRemoteMedia()
//                mPlaybackState = PlaybackState.PLAYING
//            }
//            return
//        }
//
//        invalidateOptionsMenu()
//    }

    private fun onApplicationDisconnected() {
        updatePlaybackLocation()
        mPlaybackState = PlaybackState.IDLE
        mLocation = PlaybackLocation.LOCAL
        invalidateOptionsMenu()
    }

    private fun setOnGestureListeners() {

        binding?.playerView?.setOnClickListener {
            if (viewCount == 0) {
                binding?.playerView?.hideController()
                if (mediaRouteMenuItem != null) mediaRouteMenuItem!!.isVisible = false

                viewCount++
            } else {
                binding?.playerView?.showController()
                if (mediaRouteMenuItem != null) mediaRouteMenuItem!!.isVisible = true
                viewCount = 0

            }

        }

    }

    override fun onAdLoad(value: String) {
        adStatus = value.equals("success", true)

    }

    override fun onAdFinish() {
        ApplicationConstants.videoFinish = true
        finish()

    }

}