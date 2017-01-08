package com.kj.androidftpserver.ui;

import com.kj.androidftpserver.FTPServer;

public class MainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		FTPServer server = new FTPServer();
		//启动服务器
		server.startServer();
		//捕获 客户端的连接w
		server.listenSocket();
		
		try{
		Thread.sleep(25000);
		}catch(Exception e){
			e.printStackTrace();
		}
		//server.closeServer();
	}

}
