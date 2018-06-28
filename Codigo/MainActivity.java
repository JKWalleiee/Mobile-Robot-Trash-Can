package com.example.helloyuki;


import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera.PictureCallback;




public class MainActivity extends Activity {
	  private Camera camera;
	  public static final int DONE=1;  
      public static final int NEXT=2;  
      public static final int PERIOD=1;   
      private Timer timer; 
      private int cameraId = 0; 
   // Punto de salida del canal de comunicación
   // Se usa para enviar hacia el robot
   private DataOutputStream DataOut = null;
 
    //Target NXTs for communication
    		String nxt1 = "00:16:53:05:C6:8E";

    		BluetoothAdapter localAdapter;
    		BluetoothSocket socket_nxt1;
    		boolean success=false;
	  //Layout donde se verá la imagen de la camara
	Bitmap Imag, Imag1, ImagM; ImageView view, view1; Button But; TextView text,text1,text2;
	int picw, pich; int pix[],cnt=0, ci, cj ;
	byte[] tempdata;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);	  
		 // do we have a camera?  
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {  
         Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)  
           .show();  
        } else {  
         cameraId = findFrontFacingCamera();  
         if (cameraId < 0) {  
          Toast.makeText(this, "No back facing camera found.",  
            Toast.LENGTH_LONG).show();  
         } else {  
              safeCameraOpen(cameraId);   
         }  
        }
        SurfaceView fake = new SurfaceView(this);  
        try {  
                camera.setPreviewDisplay(fake.getHolder());  
           } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
           }  
      
        camera.startPreview();  
        Camera.Parameters params = camera.getParameters();  
        params.setJpegQuality(100);  
        camera.setParameters(params);
		view = (ImageView)findViewById(R.id.Image);
		view1 = (ImageView)findViewById(R.id.Image1);
		But = (Button)findViewById(R.id.Butt);
		text = (TextView)findViewById(R.id.text);
		text1 = (TextView)findViewById(R.id.text1);
		text2 = (TextView)findViewById(R.id.text2);
		But.setOnClickListener(buttonListener);
		enableBT();
    }  
	
	 

	private Handler threadHandler = new Handler() {  
         public void handleMessage(android.os.Message msg) {       
               switch(msg.what){  
               case DONE:  
                   // Trigger camera callback to take pic  
                    camera.takePicture(null, null, myJpeg);  
                    Imag1=ImagM;
                    break;  
               case NEXT:  
            	   
                    timer=new Timer(getApplicationContext(),threadHandler);  
                    timer.execute();   
                    Imag=ImagM;
        			pix = new int[picw*pich];
        			cnt++;
        			if(cnt>3){
        				Operation();
        			}
                    break;  
               }  
               }  
          };  
        
	private OnClickListener buttonListener = new OnClickListener() {

		public void onClick(View v) {
			
			connectToNXTs();
			timer=new Timer(getApplicationContext(),threadHandler);  
	        timer.execute();  
            
	  //      boolean s=connectToNXTs();
	   //     text1.setText(String.valueOf(s));
			
		}
	
	
	};
	
	//Enables Bluetooth if not enabled
			public void enableBT(){
			    localAdapter=BluetoothAdapter.getDefaultAdapter();
			    //If Bluetooth not enable then do it
			    if(localAdapter.isEnabled()==false){
			        localAdapter.enable();
			        while(!(localAdapter.isEnabled())){

			        }
			    }

			}
	//connect to both NXTs
			public  boolean connectToNXTs(){



			    //get the BluetoothDevice of the NXT
			    BluetoothDevice nxt_1 = localAdapter.getRemoteDevice(nxt1);
			    //try to connect to the nxt
			    try {
			        socket_nxt1 = nxt_1.createRfcommSocketToServiceRecord
			        		(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			            socket_nxt1.connect();
			        success = true;
			        DataOut = new DataOutputStream(socket_nxt1.getOutputStream());
			   //     DataIN = new DataInputStream(socket_nxt1.getInputStream());
			    } catch (IOException e) {
			        Log.d("Bluetooth","Err: Device not found or cannot connect");
			        success=false;
			    }
		
					


			    return success;

			}
	private int findFrontFacingCamera() {  
        int cameraId = -1;  
        // Search for the front facing camera  
        int numberOfCameras = Camera.getNumberOfCameras();  
        for (int i = 0; i < numberOfCameras; i++) {  
             CameraInfo info = new CameraInfo();  
             Camera.getCameraInfo(i, info);  
             if (info.facing == CameraInfo.CAMERA_FACING_BACK) {  
                  Log.v("MyActivity", "Camera found");  
            cameraId = i;  
            break;  
           }  
          }  
          return cameraId;  
         }  
	@Override  
    protected void onPause() {  
		 if (timer!=null){  
             timer.cancel(true);  
        } 
      releaseCamera();  
      super.onPause();  
     }  
	private boolean safeCameraOpen(int id) {  
        boolean qOpened = false;  

        try {  
          releaseCamera();  
          camera = Camera.open(id);
          qOpened = (camera != null);  
        } catch (Exception e) {  
        	Log.e(getString(R.string.app_name), "failed to open Camera");  
            e.printStackTrace();
        }  
        return qOpened;    
      }  
	private void releaseCamera() {  
        if (camera != null) {  
             camera.stopPreview();  
          camera.release();
          camera = null;  
        }  
      }  

	
	PictureCallback myJpeg = new PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera myCamera) {
		   // TODO Auto-generated method 
			 camera.startPreview();
		   tempdata=data;
		   ImagM= BitmapFactory.decodeByteArray(data, 0, data.length);
	//	   view.setImageBitmap(ImagM);
		   picw = ImagM.getWidth(); pich = ImagM.getHeight();
		
		   Message.obtain(threadHandler, MainActivity.NEXT, "").sendToTarget();  
		   
		      
		   
		  
		}
		   
		};
		
		
		public void Operation()
		{
			

		
			
			// open the camera and pass in the current view
			
			Bitmap bm = Bitmap.createBitmap(picw, pich, Bitmap.Config.ARGB_4444);
			pix = Resta(Imag,Imag1);
			bm.setPixels(pix, 0, picw, 0, 0, picw, pich);
			view.setImageBitmap(bm);
			
   
		}	

	public int[] Resta(Bitmap mBitmap, Bitmap aBitmap)
	{
		int picw, pich;
		int r31 = 0;
		int g31 =0;
		int b31=0;
		picw = mBitmap.getWidth();
		pich = mBitmap.getHeight();
		int[] pix = new int[picw * pich];
		mBitmap.getPixels(pix, 0, picw, 0, 0, picw, pich);
		int[] pix1 = new int[picw * pich];
		int[] dilate =new int[picw*pich];
		int[] dilate1 =new int[picw*pich];
		int[] dilate2 =new int[picw*pich];
		int[] dilate3 =new int[picw*pich];
		aBitmap.getPixels(pix1, 0, picw, 0, 0, picw, pich);
		int[] pixt = new int[picw * pich];
		for (int y = 0; y < pich; y++)
		for (int x = 0; x < picw; x++)
		{
			// Pixels are located sequentially inside a very long array
			int pixelLocation = x + y*picw;
			// The R,G,B values from our 2 images
			float r1 = (pix[pixelLocation] >> 16) & 0xff;
			float g1 = (pix[pixelLocation] >> 8) & 0xff;
			float b1 = pix[pixelLocation] & 0xff;
			float r2 = (pix1[pixelLocation] >> 16) & 0xff;
			float g2 = (pix1[pixelLocation] >> 8) & 0xff;
			float b2 = pix1[pixelLocation] & 0xff;
			// We substract pixel by pixel (RGB values): 3rd image = 2nd - 1st
			float r3 = Math.abs(r2 - r1);
			float g3 = Math.abs(g2 - g1);
			float b3 = Math.abs(b2 - b1);
			if(r3<1 && g3<1 && b3<1){
				r31=(int)r1;
				g31=(int)g1;
				b31=(int)b1;
			}
			
			else{
				r31=(int)r3;
				g31=(int)g3;
				b31=(int)b3;
			}
			int R = (int)(0.299*r31 + 0.587*g31 + 0.114*b31);
		
			pixt[pixelLocation] = 0xff000000 | (R << 16) | (R << 8) | R;
			
			
			if(pixt[pixelLocation]>0xff000000){
				pixt[pixelLocation]=0xff000000;
			}
			
			else{
				pixt[pixelLocation]=0xffffffff;
				 
			}
			
			     
		}
		dilate3=dilate(pixt);
		dilate2=dilate(dilate3);
		dilate1=dilate(dilate2);
		dilate=dilate(dilate1);
		dilate=centroid(dilate);
		Bitmap bm = Bitmap.createBitmap(picw, pich, Bitmap.Config.ARGB_4444);
		bm.setPixels(pixt, 0, picw, 0, 0, picw, pich);
		//view1.setImageBitmap(bm);
		text.setText(Integer.toString(ci));
		text1.setText(Integer.toString(cj));
		Log.d(Integer.toString(cj),Integer.toString(picw));
		
		return dilate;
	}
	public int[] dilate(int[] pixt){
		int[] dilate =new int[picw*pich];
		for (int y = 1; y < pich-1; y++)
		for (int x = 1; x < picw-1; x++)
				{
				   int pixelLocation = x + y*picw;
				   dilate[pixelLocation]=pixt[pixelLocation];
				 
				   if(pixt[pixelLocation]==0xff000000 && 
						   (pixt[pixelLocation-1]==0xffffffff||pixt[pixelLocation+1]==0xffffffff
						   ||pixt[pixelLocation-picw]==0xffffffff||pixt[pixelLocation+picw]==0xffffffff)){

				   		dilate[pixelLocation]=0xffffffff;
				   }
				  
				}
		return dilate;
	}
	
	public int[] centroid(int[] pixt){

		int num_pixeles=0, sum_i=0, sum_j=0,pixelLocation;
		for (int y = 1; y < pich-1; y++)
		for (int x = 1; x < picw-1; x++){
			    pixelLocation = x + y*picw;
				if(pixt[pixelLocation]==0xff000000){
					num_pixeles= num_pixeles + 1;
		            sum_i = sum_i + x;
		            sum_j = sum_j + y;
				}}
		ci = sum_i/num_pixeles;
		cj = sum_j/num_pixeles;
		text2.setText(Integer.toString(num_pixeles));
		pixelLocation = ci+ cj*picw;
		for(int i=0; i<10; i++){
		pixt[pixelLocation+i]=0xff00ff00;	
				;}
	
		try {
			DataOut.writeInt(cj);
		    DataOut.writeInt(pich);
			DataOut.writeInt(num_pixeles);
			DataOut.flush(); 
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pixt;
	}
	
}
