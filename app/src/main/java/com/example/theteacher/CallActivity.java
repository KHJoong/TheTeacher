package com.example.theteacher;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoRenderer;

import com.example.theteacher.NeedToWebRTC.AppRTCClient;
import com.example.theteacher.NeedToWebRTC.PeerConnectionClient;
import com.example.theteacher.NeedToWebRTC.AppRTCAudioManager;
import com.example.theteacher.NeedToWebRTC.AppRTCAudioManager.AudioDevice;
import com.example.theteacher.NeedToWebRTC.AppRTCAudioManager.AudioManagerEvents;
import com.example.theteacher.NeedToWebRTC.AppRTCClient.SignalingParameters;
import com.example.theteacher.NeedToWebRTC.AppRTCClient.RoomConnectionParameters;
import com.example.theteacher.NeedToWebRTC.DirectRTCClient;
import com.example.theteacher.NeedToWebRTC.PeerConnectionClient.DataChannelParameters;
import com.example.theteacher.NeedToWebRTC.PeerConnectionClient.PeerConnectionParameters;
import com.example.theteacher.NeedToWebRTC.WebSocketRTCClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Created by kimhj on 2018-02-10.
 */

// AppRTC를 기본 베이스로 이용했습니다.

// WebRTC를 이용하여 화상전화를 할 때 뜨는 화면입니다.
// 다음 두 경우 이 화면이 열리게 됩니다.
// (1) 학생이 LectureDetailActivity에서 상담하기 버튼을 클릭했을 경우
// (2) 선생님이 ReceiveSignalActivity에서 받기 버튼을 클릭했을 경우
public class CallActivity extends AppCompatActivity implements AppRTCClient.SignalingEvents, PeerConnectionClient.PeerConnectionEvents, CallActivity_Call.OnCallEvents {

    public static final String EXTRA_ROOMID = "com.example.theteacher.ROOMID";
    public static final String EXTRA_URLPARAMETERS = "com.example.theteacher.URLPARAMETERS";
    public static final String EXTRA_LOOPBACK = "com.example.theteacher.LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = "com.example.theteacher.VIDEO_CALL";
    public static final String EXTRA_SCREENCAPTURE = "com.example.theteacher.SCREENCAPTURE";
    public static final String EXTRA_CAMERA2 = "com.example.theteacher.CAMERA2";
    public static final String EXTRA_VIDEO_WIDTH = "com.example.theteacher.VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "com.example.theteacher.VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "com.example.theteacher.VIDEO_FPS";
    public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED =
            "com.example.theteacher.VIDEO_CAPTUREQUALITYSLIDER";
    public static final String EXTRA_VIDEO_BITRATE = "com.example.theteacher.VIDEO_BITRATE";
    public static final String EXTRA_VIDEOCODEC = "com.example.theteacher.VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = "com.example.theteacher.HWCODEC";
    public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "com.example.theteacher.CAPTURETOTEXTURE";
    public static final String EXTRA_FLEXFEC_ENABLED = "com.example.theteacher.FLEXFEC";
    public static final String EXTRA_AUDIO_BITRATE = "com.example.theteacher.AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = "com.example.theteacher.AUDIOCODEC";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED =
            "com.example.theteacher.NOAUDIOPROCESSING";
    public static final String EXTRA_AECDUMP_ENABLED = "com.example.theteacher.AECDUMP";
    public static final String EXTRA_OPENSLES_ENABLED = "com.example.theteacher.OPENSLES";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = "com.example.theteacher.DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = "com.example.theteacher.DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = "com.example.theteacher.DISABLE_BUILT_IN_NS";
    public static final String EXTRA_ENABLE_LEVEL_CONTROL = "com.example.theteacher.ENABLE_LEVEL_CONTROL";
    public static final String EXTRA_DISABLE_WEBRTC_AGC_AND_HPF =
            "com.example.theteacher.DISABLE_WEBRTC_GAIN_CONTROL";
    public static final String EXTRA_DISPLAY_HUD = "com.example.theteacher.DISPLAY_HUD";
    public static final String EXTRA_TRACING = "com.example.theteacher.TRACING";
    public static final String EXTRA_CMDLINE = "com.example.theteacher.CMDLINE";
    public static final String EXTRA_RUNTIME = "com.example.theteacher.RUNTIME";
    public static final String EXTRA_VIDEO_FILE_AS_CAMERA = "com.example.theteacher.VIDEO_FILE_AS_CAMERA";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE =
            "com.example.theteacher.SAVE_REMOTE_VIDEO_TO_FILE";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH =
            "com.example.theteacher.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT =
            "com.example.theteacher.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";
    public static final String EXTRA_USE_VALUES_FROM_INTENT =
            "com.example.theteacher.USE_VALUES_FROM_INTENT";
    public static final String EXTRA_DATA_CHANNEL_ENABLED = "com.example.theteacher.DATA_CHANNEL_ENABLED";
    public static final String EXTRA_ORDERED = "com.example.theteacher.ORDERED";
    public static final String EXTRA_MAX_RETRANSMITS_MS = "com.example.theteacher.MAX_RETRANSMITS_MS";
    public static final String EXTRA_MAX_RETRANSMITS = "com.example.theteacher.MAX_RETRANSMITS";
    public static final String EXTRA_PROTOCOL = "com.example.theteacher.PROTOCOL";
    public static final String EXTRA_NEGOTIATED = "com.example.theteacher.NEGOTIATED";
    public static final String EXTRA_ID = "com.example.theteacher.ID";
    public static final String EXTRA_CALLER_ID = "com.example.theteacher.CallID";

    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;

    // List of mandatory application permissions.
    public static final String[] MANDATORY_PERMISSIONS = {"android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO", "android.permission.INTERNET", "android.permission.CAMERA"};

    // Peer connection statistics callback period in ms.
    private static final int STAT_CALLBACK_PERIOD = 1000;


    private class ProxyRenderer implements VideoRenderer.Callbacks{
        private VideoRenderer.Callbacks target;

        synchronized public void renderFrame(VideoRenderer.I420Frame frame){
            if(target == null){
                VideoRenderer.renderFrameDone(frame);
                return;
            }

            target.renderFrame(frame);
        }

        synchronized  public void setTarget(VideoRenderer.Callbacks target){
            this.target = target;
        }
    }

    private final ProxyRenderer remoteProxyRenderer = new ProxyRenderer();
    private final ProxyRenderer localProxyRenderer = new ProxyRenderer();
    private PeerConnectionClient peerConnectionClient = null;
    private AppRTCClient appRtcClient;
    private SignalingParameters signalingParameters;
    private AppRTCAudioManager audioManager = null;
    private EglBase rootEglBase;
    private SurfaceViewRenderer pipRenderer;
    private SurfaceViewRenderer fullscreenRenderer;
    private VideoFileRenderer videoFileRenderer;
    private final List<VideoRenderer.Callbacks> remoteRenderers =
            new ArrayList<VideoRenderer.Callbacks>();
    private Toast logToast;
    private boolean commandLineRun;
    private int runTimeMs;
    private boolean activityRunning;
    private RoomConnectionParameters roomConnectionParameters;
    private PeerConnectionParameters peerConnectionParameters;
    private boolean iceConnected;
    private boolean isError;
    private boolean callControlFragmentVisible = true;
    private long callStartedTimeMs = 0;
    private boolean micEnabled = true;
    private boolean screencaptureEnabled = false;
    private static Intent mediaProjectionPermissionResultData;
    private static int mediaProjectionPermissionResultCode;
    // True if local view is in the fullscreen renderer.
    private boolean isSwappedFeeds;

    private CallActivity_Call callFragment;
    private CallActivity_Hud hudFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 이 화면이 타이틀바를 사용하지 않고 풀 스크린을 사용하겠다고 설정값을 바꿔주는 부분입니다.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getWindow().getDecorView().setSystemUiVisibility(getSystemUiVisibility());
        setContentView(R.layout.video_call);

        // WebRTC 연결에 사용되는 초기 셋팅값입니다.
        // iceConnected는 상대방과 연결될 경우 true로 바뀝니다.
        iceConnected = false;
        signalingParameters = null;

        // 처음 이 화면이 실행될 때
        // fullscreenRenderer는 자신 폰 카메라를 보여주고 pipRenderer는 화면에 뜨지 않습니다.
        // 상대방과 연결될 경우 기본적으로 fullscreenRenderer는 상대방의 화면을, pipRenderer는 내 화면을 띄웁니다.
        // pipRenderer를 클릭할 경우 두 화면에서 띄워주는 영상이 바뀝니다.
        pipRenderer = (SurfaceViewRenderer) findViewById(R.id.pip_video_view);
        fullscreenRenderer = (SurfaceViewRenderer) findViewById(R.id.fullscreen_video_view);
        callFragment = new CallActivity_Call();
        hudFragment = new CallActivity_Hud();

        // 밑에 종료, 음소거와 같은 버튼들을 화면에서 보이지 않게 해줍니다.
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCallControlFragmentVisibility();
            }
        };

        // pipRenderer를 클릭할 경우 화면에서 보이는 영상이 바뀌는 부분입니다.
        // 초기 값은 fullscreenRenderer에 상대방 화면, pipRenderer에 내 화면입니다.
        pipRenderer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSwappedFeeds(!isSwappedFeeds);
            }
        });

        fullscreenRenderer.setOnClickListener(listener);
        remoteRenderers.add(remoteProxyRenderer);

        final Intent intent = getIntent();

        // Create video renderers.
        rootEglBase = EglBase.create();
        pipRenderer.init(rootEglBase.getEglBaseContext(), null);
        pipRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        String saveRemoteVideoToFile = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);

        // 통화 내용을 저장하겠다고 설정하는 경우 작동하는 부분입니다.
        if (saveRemoteVideoToFile != null) {
            int videoOutWidth = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
            int videoOutHeight = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
            try {
                videoFileRenderer = new VideoFileRenderer(
                        saveRemoteVideoToFile, videoOutWidth, videoOutHeight, rootEglBase.getEglBaseContext());
                remoteRenderers.add(videoFileRenderer);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to open video file for output: " + saveRemoteVideoToFile, e);
            }
        }
        fullscreenRenderer.init(rootEglBase.getEglBaseContext(), null);
        fullscreenRenderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);

        pipRenderer.setZOrderMediaOverlay(true);
        pipRenderer.setEnableHardwareScaler(true /* enabled */);
        fullscreenRenderer.setEnableHardwareScaler(true /* enabled */);
        // Start with local feed in fullscreen and swap it to the pip when the call is connected.
        setSwappedFeeds(true /* isSwappedFeeds */);

        // 오디오, 인터넷, 카메라 권한을 체크하는 부분입니다.
        // 권한을 허락하지 않았을 경우 종료됩니다.
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                logAndToast("Permission " + permission + " is not granted");
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }

        Uri roomUri = intent.getData();
        if (roomUri == null) {
            logAndToast(getString(R.string.missing_url));
            Log.e("CallActivity:", "Didn't get any URL in intent!");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        // Get Intent parameters.
        String roomId = intent.getStringExtra(EXTRA_ROOMID);
        Log.d("CallActivity:", "Room ID: " + roomId);
        if (roomId == null || roomId.length() == 0) {
            logAndToast(getString(R.string.missing_url));
            Log.e("CallActivity:", "Incorrect room ID in intent!");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
        boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false);

        int videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0);
        int videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0);

        screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREENCAPTURE, false);
        // If capturing format is not specified for screencapture, use screen resolution.
        if (screencaptureEnabled && videoWidth == 0 && videoHeight == 0) {
            DisplayMetrics displayMetrics = getDisplayMetrics();
            videoWidth = displayMetrics.widthPixels;
            videoHeight = displayMetrics.heightPixels;
        }
        DataChannelParameters dataChannelParameters = null;
        if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, false)) {
            dataChannelParameters = new DataChannelParameters(intent.getBooleanExtra(EXTRA_ORDERED, true),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(EXTRA_PROTOCOL),
                    intent.getBooleanExtra(EXTRA_NEGOTIATED, false), intent.getIntExtra(EXTRA_ID, -1));
        }
        // 연결을 위해 PeerConnectionParameters 값을 설정하는 부분입니다.
        peerConnectionParameters =
                new PeerConnectionParameters(intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), loopback,
                        tracing, videoWidth, videoHeight, intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
                        intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0), intent.getStringExtra(EXTRA_VIDEOCODEC),
                        intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true),
                        intent.getBooleanExtra(EXTRA_FLEXFEC_ENABLED, false),
                        intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0), intent.getStringExtra(EXTRA_AUDIOCODEC),
                        intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false),
                        intent.getBooleanExtra(EXTRA_ENABLE_LEVEL_CONTROL, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_WEBRTC_AGC_AND_HPF, false), dataChannelParameters);
        commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);
        runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0);

        Log.d("CallActivity:", "VIDEO_FILE: '" + intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA) + "'");

        // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the
        // standard WebSocketRTCClient.
        if (loopback || !DirectRTCClient.IP_PATTERN.matcher(roomId).matches()) {
            appRtcClient = new WebSocketRTCClient(this);
        } else {
            Log.i("CallActivity:", "Using DirectRTCClient because room name looks like an IP.");
            appRtcClient = new DirectRTCClient(this);
        }
        // Create connection parameters.
        String urlParameters = intent.getStringExtra(EXTRA_URLPARAMETERS);
        roomConnectionParameters =
                new RoomConnectionParameters(roomUri.toString(), roomId, loopback, urlParameters);

        // Send intent arguments to fragments.
        callFragment.setArguments(intent.getExtras());
        hudFragment.setArguments(intent.getExtras());
        // Activate call and HUD fragments and start the call.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(R.id.call_fragment_container, callFragment);
        ft.add(R.id.hud_fragment_container, hudFragment);
        ft.commit();

        // For command line execution run connection for <runTimeMs> and exit.
        if (commandLineRun && runTimeMs > 0) {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    disconnect();
                }
            }, runTimeMs);
        }

        peerConnectionClient = PeerConnectionClient.getInstance();
        if (loopback) {
            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
            options.networkIgnoreMask = 0;
            peerConnectionClient.setPeerConnectionFactoryOptions(options);
        }
        peerConnectionClient.createPeerConnectionFactory(
                getApplicationContext(), peerConnectionParameters, CallActivity.this);

        if (screencaptureEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startScreenCapture();
        } else {
            startCall();
        }

    }


    @TargetApi(17)
    private DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager =
                (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        return displayMetrics;
    }

    @TargetApi(19)
    private static int getSystemUiVisibility() {
        int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flags |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }
        return flags;
    }

    @TargetApi(21)
    private void startScreenCapture() {
        MediaProjectionManager mediaProjectionManager =
                (MediaProjectionManager) getApplication().getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
            return;
        mediaProjectionPermissionResultCode = resultCode;
        mediaProjectionPermissionResultData = data;
        startCall();
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && getIntent().getBooleanExtra(EXTRA_CAMERA2, true);
    }

    private boolean captureToTexture() {
        return getIntent().getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false);
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d("CallActivity:", "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d("CallActivity:", "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d("CallActivity:", "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d("CallActivity:", "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    @TargetApi(21)
    private VideoCapturer createScreenCapturer() {
        if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
            reportError("User didn't give permission to capture the screen.");
            return null;
        }
        return new ScreenCapturerAndroid(
                mediaProjectionPermissionResultData, new MediaProjection.Callback() {
            @Override
            public void onStop() {
                reportError("User revoked permission to capture the screen.");
            }
        });
    }

    // Activity interfaces
    @Override
    public void onStop() {
        super.onStop();
        activityRunning = false;
        // Don't stop the video when using screencapture to allow user to show other apps to the remote
        // end.
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient.stopVideoSource();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        activityRunning = true;
        // Video is not paused for screencapture. See onPause.
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient.startVideoSource();
        }
    }

    @Override
    protected void onDestroy() {
        Thread.setDefaultUncaughtExceptionHandler(null);
        disconnect();
        if (logToast != null) {
            logToast.cancel();
        }
        activityRunning = false;
        rootEglBase.release();
        super.onDestroy();
    }

    // CallFragment.OnCallEvents interface implementation.
    @Override
    public void onCallHangUp() {
        disconnect();
    }

    @Override
    public void onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
        }
    }

    @Override
    public void onVideoScalingSwitch(RendererCommon.ScalingType scalingType) {
        fullscreenRenderer.setScalingType(scalingType);
    }

    @Override
    public void onCaptureFormatChange(int width, int height, int framerate) {
        if (peerConnectionClient != null) {
            peerConnectionClient.changeCaptureFormat(width, height, framerate);
        }
    }

    @Override
    public boolean onToggleMic() {
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled;
            peerConnectionClient.setAudioEnabled(micEnabled);
        }
        return micEnabled;
    }

    // Helper functions.
    private void toggleCallControlFragmentVisibility() {
        if (!iceConnected || !callFragment.isAdded()) {
            return;
        }
        // Show/hide call control fragment
        callControlFragmentVisible = !callControlFragmentVisible;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (callControlFragmentVisible) {
            ft.show(callFragment);
            ft.show(hudFragment);
        } else {
            ft.hide(callFragment);
            ft.hide(hudFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void startCall() {
        if (appRtcClient == null) {
            Log.e("CallActivity:", "AppRTC client is not allocated for a call.");
            return;
        }
        callStartedTimeMs = System.currentTimeMillis();

        // Start room connection.
//        logAndToast(getString(R.string.connecting_to, roomConnectionParameters.roomUrl));
        appRtcClient.connectToRoom(roomConnectionParameters);

        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        audioManager = AppRTCAudioManager.create(getApplicationContext());
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d("CallActivity:", "Starting the audio manager...");
        audioManager.start(new AudioManagerEvents() {
            // This method will be called each time the number of available audio
            // devices has changed.
            @Override
            public void onAudioDeviceChanged(
                    AudioDevice audioDevice, Set<AudioDevice> availableAudioDevices) {
                onAudioManagerDevicesChanged(audioDevice, availableAudioDevices);
            }
        });
    }

    // Should be called from UI thread
    private void callConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        Log.i("CallActivity:", "Call connected: delay=" + delta + "ms");
        if (peerConnectionClient == null || isError) {
            Log.w("CallActivity:", "Call is connected in closed or error state");
            return;
        }
        // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
        setSwappedFeeds(false /* isSwappedFeeds */);
    }

    // This method is called when the audio manager reports audio device change,
    // e.g. from wired headset to speakerphone.
    private void onAudioManagerDevicesChanged(
            final AudioDevice device, final Set<AudioDevice> availableDevices) {
        Log.d("CallActivity:", "onAudioManagerDevicesChanged: " + availableDevices + ", "
                + "selected: " + device);
        // TODO(henrika): add callback handler.
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private void disconnect() {
        activityRunning = false;
        remoteProxyRenderer.setTarget(null);
        localProxyRenderer.setTarget(null);
        if (appRtcClient != null) {
            appRtcClient.disconnectFromRoom();
            appRtcClient = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }
        if (pipRenderer != null) {
            pipRenderer.release();
            pipRenderer = null;
        }
        if (videoFileRenderer != null) {
            videoFileRenderer.release();
            videoFileRenderer = null;
        }
        if (fullscreenRenderer != null) {
            fullscreenRenderer.release();
            fullscreenRenderer = null;
        }
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        if (iceConnected && !isError) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void disconnectWithErrorMessage(final String errorMessage) {
        if (commandLineRun || !activityRunning) {
            Log.e("CallActivity:", "Critical error: " + errorMessage);
            disconnect();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getText(R.string.channel_error_title))
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setNeutralButton(R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    disconnect();
                                }
                            })
                    .create()
                    .show();
        }
    }

    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d("CallActivity:", msg);
        if (logToast != null) {
            logToast.cancel();
        }
        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        logToast.show();
    }

    private void reportError(final String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError) {
                    isError = true;
                    disconnectWithErrorMessage(description);
                }
            }
        });
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer = null;
        String videoFileAsCamera = getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);
        if (videoFileAsCamera != null) {
            try {
                videoCapturer = new FileVideoCapturer(videoFileAsCamera);
            } catch (IOException e) {
                reportError("Failed to open video file for emulated camera");
                return null;
            }
        } else if (screencaptureEnabled && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return createScreenCapturer();
        } else if (useCamera2()) {
            if (!captureToTexture()) {
                reportError(getString(R.string.camera2_texture_only_error));
                return null;
            }

            Logging.d("CallActivity:", "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            Logging.d("CallActivity:", "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            reportError("Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    private void setSwappedFeeds(boolean isSwappedFeeds) {
        Logging.d("CallActivity:", "setSwappedFeeds: " + isSwappedFeeds);
        this.isSwappedFeeds = isSwappedFeeds;
        localProxyRenderer.setTarget(isSwappedFeeds ? fullscreenRenderer : pipRenderer);
        remoteProxyRenderer.setTarget(isSwappedFeeds ? pipRenderer : fullscreenRenderer);
        fullscreenRenderer.setMirror(isSwappedFeeds);
        pipRenderer.setMirror(!isSwappedFeeds);
    }

    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and
    // are routed to UI thread.
    private void onConnectedToRoomInternal(final SignalingParameters params) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;

        signalingParameters = params;
        logAndToast("Creating peer connection, delay=" + delta + "ms");
        VideoCapturer videoCapturer = null;
        if (peerConnectionParameters.videoCallEnabled) {
            videoCapturer = createVideoCapturer();
        }
        peerConnectionClient.createPeerConnection(rootEglBase.getEglBaseContext(), localProxyRenderer,
                remoteRenderers, videoCapturer, signalingParameters);

        if (signalingParameters.initiator) {
            logAndToast("Creating OFFER...");
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createOffer();
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(params.offerSdp);
                logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    @Override
    public void onConnectedToRoom(final SignalingParameters params) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onConnectedToRoomInternal(params);
            }
        });
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e("CallActivity:", "Received remote SDP for non-initilized peer connection.");
                    return;
                }
                logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms");
                peerConnectionClient.setRemoteDescription(sdp);
                if (!signalingParameters.initiator) {
                    logAndToast("Creating ANSWER...");
                    // Create answer. Answer SDP will be sent to offering client in
                    // PeerConnectionEvents.onLocalDescription event.
                    peerConnectionClient.createAnswer();
                }
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e("CallActivity:", "Received ICE candidate for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.addRemoteIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e("CallActivity:", "Received ICE candidate removals for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.removeRemoteIceCandidates(candidates);
            }
        });
    }

    @Override
    public void onChannelClose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Remote end hung up; dropping PeerConnection");
                disconnect();
            }
        });
    }

    @Override
    public void onChannelError(final String description) {
        reportError(description);
    }

    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    logAndToast("Sending " + sdp.type + ", delay=" + delta + "ms");
                    if (signalingParameters.initiator) {
                        appRtcClient.sendOfferSdp(sdp);
                    } else {
                        appRtcClient.sendAnswerSdp(sdp);
                    }
                }
                if (peerConnectionParameters.videoMaxBitrate > 0) {
                    Log.d("CallActivity:", "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
                    peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
                }
            }
        });
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidate(candidate);
                }
            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidateRemovals(candidates);
                }
            }
        });
    }

    @Override
    public void onIceConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("ICE connected, delay=" + delta + "ms");
                iceConnected = true;
                callConnected();
            }
        });
    }

    @Override
    public void onIceDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("ICE disconnected");
                iceConnected = false;
                disconnect();
            }
        });
    }

    @Override
    public void onPeerConnectionClosed() {}

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError && iceConnected) {
                    hudFragment.updateEncoderStatistics(reports);
                }
            }
        });
    }

    @Override
    public void onPeerConnectionError(final String description) {
        reportError(description);
    }

}
