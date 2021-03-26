/*
 * Copyright (c) 2015, Picker Weng
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of CameraRecorder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Project:
 *     CameraRecorder
 *
 * File:
 *     CameraRecorder.java
 *
 * Author:
 *     Picker Weng (pickerweng@gmail.com)
 */

package com.meowme.camerarecorder;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class RecorderService extends Service {
	private static final String TAG = "RecorderService";
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Camera mServiceCamera;
	private boolean mRecordingStatus;
	private MediaRecorder mMediaRecorder;
	private String VideoType,Video_CameraType;
	@Override
	public void onCreate() {


		mRecordingStatus = false;
		mServiceCamera = CameraRecorder.mCamera;
		mSurfaceView = CameraRecorder.mSurfaceView;
		mSurfaceHolder = CameraRecorder.mSurfaceHolder;

		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		VideoType = intent.getExtras().get("VideoType").toString();
		Video_CameraType = intent.getExtras().get("CameraType").toString();
		if (mRecordingStatus == false)

			startRecording();

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		stopRecording();
		mRecordingStatus = false;

		super.onDestroy();
	}

	public boolean startRecording(){
		DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
		String date = df.format(Calendar.getInstance().getTime());

		try {
			Toast.makeText(getBaseContext(), "Recording Started: "+VideoType, Toast.LENGTH_SHORT).show();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
				if(Video_CameraType.equals("Front")) {
					mServiceCamera = Camera.open(1);
				}else if(Video_CameraType.equals("Back")) {
					mServiceCamera = Camera.open(0);
				}

			}
			Camera.Parameters params = mServiceCamera.getParameters();
			mServiceCamera.setParameters(params);
			Camera.Parameters p = mServiceCamera.getParameters();

			final List<Size> listPreviewSize = p.getSupportedPreviewSizes();
			for (Size size : listPreviewSize) {
                Log.i(TAG, String.format("Supported Preview Size (%d, %d)", size.width, size.height));
            }

            Size previewSize = listPreviewSize.get(0);
			p.setPreviewSize(previewSize.width, previewSize.height);
			mServiceCamera.setParameters(p);

			try {
				mServiceCamera.setPreviewDisplay(mSurfaceHolder);
				mServiceCamera.startPreview();
			}
			catch (IOException e) {
				Log.e(TAG, e.getMessage());
				e.printStackTrace();
			}

			mServiceCamera.unlock();

			mMediaRecorder = new MediaRecorder();
			mMediaRecorder.setCamera(mServiceCamera);
//			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//			mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//			mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
			// Step 2: Set sources
			mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
			mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

			// Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
			if(VideoType.equals("High")) {
				mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
			}else if(VideoType.equals("Medium")) {
				mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
			}else if(VideoType.equals("Low")) {
				mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_LOW));
			}



			mMediaRecorder.setOutputFile(Environment.getExternalStorageDirectory().getPath() + "/VID-"+date+".mp4");

			mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

			mMediaRecorder.prepare();
			mMediaRecorder.start();

			mRecordingStatus = true;

			return true;

		} catch (IllegalStateException e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
			return false;

		} catch (IOException e) {
			Log.d(TAG, e.getMessage());
			e.printStackTrace();
			return false;
		}
	}

	public void stopRecording() {
		Toast.makeText(getBaseContext(), "Recording Stopped", Toast.LENGTH_SHORT).show();
		try {
			mServiceCamera.reconnect();

		} catch (IOException e) {
			e.printStackTrace();
		}

		mMediaRecorder.stop();
		mMediaRecorder.reset();
		VideoType = "";

		mServiceCamera.stopPreview();
		mMediaRecorder.release();

		mServiceCamera.release();
		mServiceCamera = null;
	}
}
