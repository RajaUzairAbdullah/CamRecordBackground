package com.meowme.camerarecorder;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class ImageRecorder extends Service  {
    private static final String TAG = "ImageService";
    private boolean mRecordingStatus;
    //Github
    private Camera mCamera;
    // the camera parameters
    private Camera.Parameters parameters;
    private Bitmap bmp;
    FileOutputStream fo;
    private String FLASH_MODE;
    private int QUALITY_MODE = 0;
    private boolean isFrontCamRequest = false;
    private Camera.Size pictureSize;
    SurfaceView sv;
    private SurfaceHolder sHolder;
    private WindowManager windowManager;
    WindowManager.LayoutParams params;
    public Intent cameraIntent;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int width = 0, height = 0;
    public boolean safeToCapture = true;
    //Github





    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (mRecordingStatus == false)
            TakingPic();

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        mRecordingStatus = false;

        super.onCreate();
    }


    Camera.PictureCallback mCall = new Camera.PictureCallback()
    {

        public void onPictureTaken(byte[] data, Camera camera)
        {
            safeToCapture = false;
            //decode the data obtained by the camera into a Bitmap

            FileOutputStream outStream = null;
            try{

                // create a File object for the parent directory
                File myDirectory = new File(Environment.getExternalStorageDirectory()+"/SecretPictures");
                // have the object build the directory structure, if needed.
                myDirectory.mkdirs();

                //SDF for getting current time for unique image name
                SimpleDateFormat curTimeFormat = new SimpleDateFormat("ddMMyyyyhhmmss");
                String curTime = curTimeFormat.format(new java.util.Date());

                // create a File object for the output file
                outStream = new FileOutputStream(myDirectory+"/user"+curTime+".jpg");
                outStream.write(data);
                outStream.close();
                mCamera.release();
                mCamera = null;

                String strImagePath = Environment.getExternalStorageDirectory()+"/"+myDirectory.getName()+"/user"+curTime+".jpg";
                Log.d("CAMERA", "picture clicked - "+strImagePath);
            } catch (FileNotFoundException e){
                Log.d("CAMERA", e.getMessage());
            } catch (IOException e){
                Log.d("CAMERA", e.getMessage());
            }

            safeToCapture = true;    //Set a boolean to true again after saving file.

        }
    };

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private Camera getAvailableFrontCamera (){

        int cameraCount = 0;
        Camera cam = null;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();
        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo);
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                try {
                    cam = Camera.open(camIdx);
                } catch (RuntimeException e) {
                    Log.e("CAMERA", "Camera failed to open: " + e.getLocalizedMessage());
                }
            }
        }

        return cam;
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public boolean TakingPic(){

        try {
            Toast.makeText(this, "Taking Picture", Toast.LENGTH_SHORT).show();
            mCamera = getAvailableFrontCamera();     // globally declared instance of camera
            if (mCamera == null){
                mCamera = Camera.open();    //Take rear facing camera only if no front camera available
            }
            SurfaceView sv = new SurfaceView(getApplicationContext());
            SurfaceTexture surfaceTexture = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                surfaceTexture = new SurfaceTexture(10);
            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mCamera.setPreviewTexture(surfaceTexture);
                }
                //mCamera.setPreviewDisplay(sv.getHolder());
                parameters = mCamera.getParameters();

                //set camera parameters
                mCamera.setParameters(parameters);


                //This boolean is used as app crashes while writing images to file if simultaneous calls are made to takePicture
                if(safeToCapture) {
                    mCamera.startPreview();
                    mCamera.takePicture(null, null, mCall);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            //Get a surface
            sHolder = sv.getHolder();
            //tells Android that this surface will have its data constantly replaced
            sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            return true;
        }
        catch (IllegalStateException e) {
            Log.d(TAG, e.getMessage());
            e.printStackTrace();
            return false;

        }
    }

}
