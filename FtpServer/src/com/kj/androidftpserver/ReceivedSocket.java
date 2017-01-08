package com.kj.androidftpserver;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
/**
 * 接收到 的用户信息集合
 * @author keji
 * @2013-4-8
 */
public class ReceivedSocket {
	Socket socket;
	
	String username;
	String password;
	//数据连接线程池
	ExecutorService dateTransExec;
	
	//用户所处位置
//	String curLocation = "/mnt/sdcard/";
//	String rootLocation = "/mnt/sdcard";
	
	String curLocation = "D:/";
	String rootLocation = "D:";
	
	public String getRootLocation() {
		return rootLocation;
	}

	public void setRootLocation(String rootLocation) {
		this.rootLocation = rootLocation;
	}

	public ReceivedSocket() {
		
	}
	
	public String getCurLocation() {
		return curLocation;
	}


	public void setCurLocation(String curLocation) {
		this.curLocation = curLocation;
	}


	public String getDecode() {
		return decode;
	}


	public void setDecode(String decode) {
		this.decode = decode;
	}
	String decode = "utf8";
	
	
	
	
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	public ExecutorService getDateTransExec() {
		return dateTransExec;
	}
	public void setDateTransExec(ExecutorService dateTransExec) {
		this.dateTransExec = dateTransExec;
	}
	/**
	 * 关闭Socket连接
	 */
	public void close(){
		try{
		if(socket!=null){
			socket.close();
		}
		}catch(IOException e){
			e.printStackTrace();
		}	
		}
}
