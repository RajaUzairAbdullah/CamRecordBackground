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

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.TreeMap;

public class CameraRecorder extends Activity implements SurfaceHolder.Callback,PictureCapturingListener{


	public static SurfaceView mSurfaceView;
	public static SurfaceHolder mSurfaceHolder;
	public static Camera mCamera;
	public static boolean mPreviewRunning;
	ToggleButton togglebtn_video,togglebtn_pic;
	Button takepic;
	public static Spinner videotype,cameratype,pic_cameratype,pic_spinner_time;
	private static final String[] VideoType = {"High", "Medium", "Low"};
	private static final String[] CameraType = {"Front","Back"};
	private static final String[] PicCameraType = {"Front","Back"};
	private static final String[] CameraTimmer = {"5","10","20","30","40","50","60"};



	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		videotype = (Spinner)findViewById(R.id.spinner);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(CameraRecorder.this,
				android.R.layout.simple_spinner_item,VideoType);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		videotype.setAdapter(adapter);

		videotype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
				((TextView) parent.getChildAt(0)).setTextSize(16);
				switch (position) {
					case 0:
//						Toast.makeText(CameraRecorder.this, "High1", Toast.LENGTH_SHORT).show();
						break;
					case 1:
//						Toast.makeText(CameraRecorder.this, "Medium1", Toast.LENGTH_SHORT).show();
						break;
					case 2:
//						Toast.makeText(CameraRecorder.this, "Low1", Toast.LENGTH_SHORT).show();
						break;

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});


		cameratype = (Spinner)findViewById(R.id.cameratype);
		ArrayAdapter<String> cameratypeadapter = new ArrayAdapter<String>(CameraRecorder.this,
				android.R.layout.simple_spinner_item,PicCameraType);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		cameratype.setAdapter(cameratypeadapter);

		cameratype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
				((TextView) parent.getChildAt(0)).setTextSize(16);
				switch (position) {
					case 0:
						Toast.makeText(CameraRecorder.this, "Front", Toast.LENGTH_SHORT).show();
						break;
					case 1:
						Toast.makeText(CameraRecorder.this, "Back", Toast.LENGTH_SHORT).show();
						break;

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

		pic_cameratype = (Spinner)findViewById(R.id.pic_cameratype);
		ArrayAdapter<String> pic_cameratypeadapter = new ArrayAdapter<String>(CameraRecorder.this,
				android.R.layout.simple_spinner_item,CameraType);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		pic_cameratype.setAdapter(pic_cameratypeadapter);

		pic_cameratype.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
				((TextView) parent.getChildAt(0)).setTextSize(16);
				switch (position) {
					case 0:
						Toast.makeText(CameraRecorder.this, "Front", Toast.LENGTH_SHORT).show();
						break;
					case 1:
						Toast.makeText(CameraRecorder.this, "Back", Toast.LENGTH_SHORT).show();
						break;

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});

		pic_spinner_time = (Spinner)findViewById(R.id.cam_timmer_spinner);
		ArrayAdapter<String> pic_spinner_adapter = new ArrayAdapter<String>(CameraRecorder.this,
				android.R.layout.simple_spinner_item,CameraTimmer);

		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		pic_spinner_time.setAdapter(pic_spinner_adapter);

		pic_spinner_time.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
				((TextView)parent.getChildAt(0)).setTextColor(Color.BLACK);
				((TextView) parent.getChildAt(0)).setTextSize(16);
				switch (position) {
					case 0:
//						Toast.makeText(CameraRecorder.this, "5", Toast.LENGTH_SHORT).show();
						break;
					case 1:
//						Toast.makeText(CameraRecorder.this, "10", Toast.LENGTH_SHORT).show();
						break;
					case 2:
//						Toast.makeText(CameraRecorder.this, "20", Toast.LENGTH_SHORT).show();
						break;
					case 3:
//						Toast.makeText(CameraRecorder.this, "30", Toast.LENGTH_SHORT).show();
						break;
					case 4:
//						Toast.makeText(CameraRecorder.this, "40", Toast.LENGTH_SHORT).show();
						break;
					case 5:
//						Toast.makeText(CameraRecorder.this, "50", Toast.LENGTH_SHORT).show();
						break;
					case 6:
//						Toast.makeText(CameraRecorder.this, "60", Toast.LENGTH_SHORT).show();
						break;

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
			}
		});


		togglebtn_video = findViewById(R.id.togglebutton);
		togglebtn_video.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Intent intent = new Intent(CameraRecorder.this, RecorderService.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("VideoType",videotype.getSelectedItem().toString());
					intent.putExtra("CameraType",cameratype.getSelectedItem().toString());
					startService(intent);
				} else {
					stopService(new Intent(CameraRecorder.this, RecorderService.class));
				}
			}
		});


		togglebtn_pic = findViewById(R.id.togglebutton_pic);
		togglebtn_pic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					Intent intent = new Intent(CameraRecorder.this, ImageRecorder.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					intent.putExtra("CameraType",pic_cameratype.getSelectedItem().toString());
					intent.putExtra("PicTimmer",pic_spinner_time.getSelectedItem().toString());
					startService(intent);
				} else {
					stopService(new Intent(CameraRecorder.this, ImageRecorder.class));
				}
			}
		});



    }


	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	@Override
	public void onCaptureDone(String pictureUrl, byte[] pictureData) {

	}

	@Override
	public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {

	}
}
