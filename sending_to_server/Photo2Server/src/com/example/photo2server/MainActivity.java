package com.example.photo2server;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Button select_button;
	private Button take_photo_button;
	private Button send_button;
	private TextView text;		
	private static final int SELECT_PHOTO = 100;
	private File sendFile;
	private String filePath;
	private EditText ipField;
	private String defaultIP = "10.34.17.50";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		select_button = (Button) findViewById(R.id.button1);   //reference to the select button
		take_photo_button = (Button) findViewById(R.id.button2);   //reference to the take photo button
		send_button = (Button) findViewById(R.id.button3);   //reference to the send button	
		text = (TextView) findViewById(R.id.textView1);   //reference to the text view
		ipField = (EditText) findViewById(R.id.editText1);   //reference to the ip field view
		
		ipField.setText(defaultIP);

		//Select button press event listener
		select_button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				startPhotoIntent();

			}
		});
		//Take photo button press event listener
		take_photo_button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				dispatchTakePictureIntent();
			}
		});

		//Send button press event listener
		send_button.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {

				// sendFile = "/mnt/sdcard/test_receipt.jpg"

				if(sendFile != null){
					System.out.println("Sending...");
					String ip = ipField.getText().toString();
					String ipRegex = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
					if(ip.matches(ipRegex))
						new SendFile(sendFile, ip).execute("");
					else
						System.out.println("Not a valid IP address...");
				}
				else
					System.out.println("sendFile is null");
			}
		});
	}

	public void startPhotoIntent() {
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);  
	}

	protected void onActivityResult(int requestCode, int resultCode, 
			Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 
		System.out.println("onActivityResult");

		switch(requestCode) { 
		case SELECT_PHOTO:
			if(resultCode == RESULT_OK){  
				Uri selectedImage = imageReturnedIntent.getData();
				String[] filePathColumn = {MediaStore.Images.Media.DATA};

				Cursor cursor = getContentResolver().query(
						selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();

				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				filePath = cursor.getString(columnIndex);
				cursor.close();

				System.out.println("CHOOSEN: " + filePath);
				if(filePath != null)
					sendFile = new File(filePath);	

				Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);

			}
			break;
		case REQUEST_TAKE_PHOTO:
			System.out.println("TAKEN: " + filePath);
			if(filePath != null)
				sendFile = new File(filePath);
			break;
		}
	}

	/* Take photo */

	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 1;


	public File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
				);

		// Save a file: path for use with ACTION_VIEW intents
		filePath = image.getAbsolutePath();
		System.out.println("createImageFile(): " + filePath);

		return image;
	}

	public void dispatchTakePictureIntent() {

		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Create the File where the photo should go
		File photoFile = null;

		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	private void setPic() {
		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW/10, photoH/10);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);
	}
}