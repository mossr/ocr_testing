/*
 * This is a simple server application
 * This server receive a file from a Android client and save it in a given place.
 * Author by Lak J Comspace
 */
package com.example.photo2server;
 
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
 
public class Main {
 
    private static ServerSocket serverSocket;
    private static Socket clientSocket;
    private static InputStream inputStream;
    private static FileOutputStream fileOutputStream;
    private static BufferedOutputStream bufferedOutputStream;
    private static int filesize = 10000000; // filesize temporary hardcoded 
    private static int bytesRead;
    private static int current = 0;
    private static String outputDir = "C:\\Users\\mossr\\Desktop\\WIT\\";
    private static String outputFile = outputDir + "output.jpg";
    private static String outputTextFile = outputDir + "output";
 
    public static void main(String[] args) throws IOException  {
    	receiveData();
    	
    	sendData();
    }
    
    public static void receiveData() throws IOException {
        serverSocket = new ServerSocket(7788);  //Server socket
 
        System.out.println("Server started. Listening to the port 7788");
 
 
        clientSocket = serverSocket.accept();
 
 
        byte[] mybytearray = new byte[filesize];    //create byte array to buffer the file
 
        inputStream = clientSocket.getInputStream();
        fileOutputStream = new FileOutputStream(outputFile);
        bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
 
        System.out.println("Receiving...");
 
        //following lines read the input slide file byte by byte
        bytesRead = inputStream.read(mybytearray, 0, mybytearray.length);
        current = bytesRead;
 
        do {
            bytesRead = inputStream.read(mybytearray, current, (mybytearray.length - current));
            if (bytesRead >= 0) {
                current += bytesRead;
            }
        } while (bytesRead > -1);
 
 
        bufferedOutputStream.write(mybytearray, 0, current);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
        inputStream.close();
        clientSocket.close();
        serverSocket.close();
 
        System.out.println("Sever recieved the file");
        
        callTesseract(outputFile, outputTextFile);
    }
    
    public static void sendData() {
    	
    }
    
    public static void callTesseract(String input, String output) {
    	System.out.println("Server calling tesseract ocr...");
    	String outputExt = output + ".txt";
    	try {
    		// Call tesseract command line executable
    	    Process process_tess = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", "tesseract", input, output, "-l", "eng+rcp"});
    	    // Open the output of tesseract
    	    process_tess.waitFor();
    	    Process process_out = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", outputExt});
    	    Process process_photo = Runtime.getRuntime().exec(new String[]{"cmd.exe", "/c", input});
    	    System.out.println("Opening " + outputExt + " and photo...");
    	    } catch (Exception ex) {}
    }
}