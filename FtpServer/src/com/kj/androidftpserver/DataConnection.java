package com.kj.androidftpserver;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class DataConnection {
	
	//static ServerSocket server = null;
	
	public static ServerSocket startConnection(int port){
		ServerSocket server = null;
		try{
			server = new ServerSocket(port);
		
		}catch(IOException e){
			//端口被占用
			return null;
		}
		return server;
	}
	/*
	public static boolean listFiles(final PrintWriter commendWriter){
		new Thread(new Runnable(){
			@Override
			public void run() {
				Socket socket = null;
				try{
				if((socket = server.accept())!=null){
					final PrintWriter writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(),"utf-8"));
					//final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					writer.println("-rwxrwxrwx 1 ftp ftp        5451692 Mar 23 2005      ke ji.txt");
					writer.println("-r--r--r-- 1 ftp ftp        5451692 Mar 23 13:35 科技.txt");
					writer.println("-r--r--r-- 1 ftp ftp        5451692 Mar 23 13:35 keji.pdf");
					writer.flush();
					commendWriter.println("226 Transfer OK");
					commendWriter.flush();
					
					if(socket!=null)
					socket.close();
				}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
		}).start();
	}*/
	
}
