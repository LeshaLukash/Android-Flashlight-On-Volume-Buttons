package com.LAS11.detectvolumeup;

/*
класс для работы с фонариком
*/

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import androidx.annotation.NonNull;

public class FlashlightManager {
    boolean flashlightIsEnabled = false;

    CameraManager cameraManager;
    String cameraID;
    Handler flashlightHandler;
    CameraManager.TorchCallback flashlightCallback;

    public FlashlightManager(@NonNull Context c) {

        //cameraManager необходим для работы с фонариком
        cameraManager = (CameraManager) c.getSystemService(Context.CAMERA_SERVICE);

        //CameraManager.TorchCallback позволяет регистрировать состояние фонарика в момент переключения
        flashlightCallback = new CameraManager.TorchCallback() {
            @Override
            public void onTorchModeChanged(@NonNull String cameraId, boolean enabled) {
                super.onTorchModeChanged(cameraId, enabled);
                flashlightIsEnabled = enabled;
            }
        };

        cameraManager.registerTorchCallback(flashlightCallback, flashlightHandler);
        try { cameraID = cameraManager.getCameraIdList()[0]; }
        catch (CameraAccessException e) { e.printStackTrace(); }
    }

    //включает и выключает фонарик задней камеры
    void run() throws CameraAccessException {
        cameraManager.setTorchMode(cameraID, !flashlightIsEnabled);
    }

}
