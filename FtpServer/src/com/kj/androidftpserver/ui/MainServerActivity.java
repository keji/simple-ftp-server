package com.kj.androidftpserver.ui;

import com.kj.androidftpserver.FTPChannelServer;
import com.kj.androidftpserver.FTPServerConstants;

import java.util.Arrays;

/**
 * 主界面
 * @author keji
 * @2013-4-30
 */
public class MainServerActivity{

	FTPChannelServer server;  //服务器实例
	//此时服务器的状态
	int status = FTPServerConstants.statusClosed;

	public static void main(String[] args){
		int port = -1;
		if(args != null){
			for(int i=0; i< args.length; i++){
				if("-i".equals(args[i])){
					if(i+1 < args.length){
						port = Integer.parseInt(args[i+1]);
					}
				}
			}
		}
		if(port > 0) {
			FTPServerConstants.ctrlCmdPort = port;
		}
		MainServerActivity activity = new MainServerActivity();
		activity.start();
	}

	protected void start() {
		//向MainContext类中添加此类的实例
		MainServerContext.getInstance().setMainActivity(this);
		server = FTPChannelServer.getInstance(FTPServerConstants.ctrlCmdPort);

		server.startServer();

	}

	/**
	 * 添加消息面板消息
	 * @param info
	 */
	public void setInfoView(String info){

		System.out.println("info: "+info);
	}
	
	/**
	 * 设置状态栏信息
	 * @param info
	 */
	public void setStatusView(String info){
		System.out.println("status: "+info);
	}

	/**
	 * 关闭服务器
	 */
	public void closedServer(){

		server.closeServer();
		status = FTPServerConstants.statusClosed;
		System.exit(1);
		
	}
}
