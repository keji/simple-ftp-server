package com.kj.androidftpserver;

import java.net.ServerSocket;
import java.net.Socket;
/**
 * 数据传输线程
 * @author keji
 * @2013-4-16
 */
public abstract class DataThread implements Runnable{
	//数据连接服务器
	ServerSocket dataServer = null;
	//用于数据传输的Socket
	Socket dataSocket = null;
	
	/**
	 * 关闭传输线程
	 */
	public void closeDataThread(){
		try{
		if(dataServer!=null){
			dataServer.close();
		}
		if(dataSocket!=null){
			dataSocket.close();
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("关闭了数据传输");
	}
			
}
