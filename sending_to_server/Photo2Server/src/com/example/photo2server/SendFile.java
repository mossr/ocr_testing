package com.example.photo2server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;

public class SendFile extends AsyncTask<String, Integer, Void> {
	private Socket client;
	private FileInputStream fileInputStream;
	private BufferedInputStream bufferedInputStream;
	private OutputStream outputStream;
	private File file;
	private String serverIP;
	
	public SendFile(File file, String serverIP) {
		this.file = file;
		this.serverIP = serverIP;
	}
	
	protected void onProgressUpdate() {
		//called when the background task makes any progress
	}

	protected void onPreExecute() {
		//called before doInBackground() is started
	}
	protected void onPostExecute() {
		//called after doInBackground() has finished 
	}

	@Override
	protected Void doInBackground(String... params) {
		// TODO Auto-generated method stub
		sendData(this.file, this.serverIP);
		receiveData();
		return null;
	}
	
	public void sendData(File file, String serverIP) {
		try {
			String localIP = getLocalIpAddress();
			
			System.out.println(localIP + " -> " + serverIP);
			
			client = new Socket(serverIP, 7788);

			byte[] mybytearray = new byte[(int) file.length()]; //create a byte array to file

			fileInputStream = new FileInputStream(file);
			bufferedInputStream = new BufferedInputStream(fileInputStream);  

			bufferedInputStream.read(mybytearray, 0, mybytearray.length); //read the file

			outputStream = client.getOutputStream();

			outputStream.write(mybytearray, 0, mybytearray.length); //write file to the output stream byte by byte
			outputStream.flush();
			bufferedInputStream.close();
			outputStream.close();
			client.close();

//			text.setText("File Sent");


		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void receiveData() {
		
	}
	
	public String getLocalIpAddress() {
		String ipv4;
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface
	                .getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf
	                    .getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();

	      // for getting IPV4 format
	      if (!inetAddress.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4 = inetAddress.getHostAddress())) {

	                    String ip = inetAddress.getHostAddress().toString();
	                    return ipv4;
	                }
	            }
	        }
	    } catch (Exception ex) {
	        Log.e("IP Address", ex.toString());
	    }
	    return null;
	}
}
