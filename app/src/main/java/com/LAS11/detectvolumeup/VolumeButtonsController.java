package com.LAS11.detectvolumeup;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.media.VolumeProvider;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import androidx.annotation.NonNull;

/*
класс, который отвечает за отслеживание кнопок громкости и их привязку к фонарику
секрет прост — создаётся фейковая медиасессия MediaSession fakeMediaSession
она позволяет отслеживать кнопки громкости напрямую
а также вешать на них любой функционал
 */

public class VolumeButtonsController {
    //необходим для регистрации медиасессии
    final String TAG = "VolumeButtonsController";

    AudioManager audioManager;
    MediaSession fakeMediaSession;
    VolumeProvider volumeProvider;
    FlashlightManager flashlightService;
    boolean altMode = false;


    public VolumeButtonsController(@NonNull Context c) {

        //отслеживание громкости звука в системе
        audioManager = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        fakeMediaSession = new MediaSession(c, TAG);
        flashlightService = new FlashlightManager(c);
        volumeProvider = new VolumeProvider(VolumeProvider.VOLUME_CONTROL_RELATIVE, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) {

            //direction == 1 - громкость+;
            //direction == 0 - кнопку отпустили, любую;
            //direction == -1 - громкость-;
            @Override
            public void onAdjustVolume(int direction) {
                //сюда писать требуемый функционал
                if (altMode) {
                    if (direction == 1 || direction == -1) {
                        try { flashlightService.setStatus(true); }
                        catch (CameraAccessException e) { e.printStackTrace(); }
                    }
                    else if (direction == 0) {
                        try { flashlightService.setStatus(false); }
                        catch (CameraAccessException e) { e.printStackTrace(); }
                    }
                }
                else if (direction == 1 || direction == -1) {
                    try { flashlightService.switchLight(); }
                    catch (CameraAccessException e) { e.printStackTrace(); }
                }
                setCurrentVolume(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
            }
        };
    }

    //фейковая медиасессия начинает свою работу
    void startDetectVolumeButtons(boolean m) {
        fakeMediaSession.setCallback(new MediaSession.Callback(){});
        fakeMediaSession.setPlaybackState(new PlaybackState.Builder().setState(PlaybackState.STATE_PLAYING, 0, 0).build());
        fakeMediaSession.setPlaybackToRemote(volumeProvider);
        fakeMediaSession.setActive(true);
        altMode = m;
    }

    //мы не хотим, что бы сервис висел фоном, пока не удалишь приложение
    void destroy() {
        try { flashlightService.setStatus(false); }
        catch (CameraAccessException e) { e.printStackTrace(); }

        if (fakeMediaSession != null) {
            fakeMediaSession.setActive(false);
            fakeMediaSession.release();
        }
    }

}
