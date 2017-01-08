package com.kj.androidftpserver.ui;

import com.kj.androidftpserver.FTPServerConstants;

public class MainServerContext {
	private MainServerActivity mainActivity;
	private static MainServerContext mainContext = new MainServerContext();
	private  MainServerContext() {
		
	}
	public static  MainServerContext getInstance(){
		return mainContext;
	}
	public void setMainActivity(MainServerActivity mainActivity){
		this.mainActivity = mainActivity;
	}
	public MainServerActivity getMainActivity(){
		return mainActivity;
	}
	
	/**
	 * 设置信息面板
	 * @param info
	 */
	public void addInfo(final String info){
		mainActivity.setInfoView(info);
	}
	/**
	 * 设置按钮关闭
	 */
	public void setBtnClosed(){

	}
	
	/**
	 * 设置状态栏
	 * @param info
	 */
	public void setStatusView(String info){
		mainActivity.setStatusView(info);
	}

	/**
	 * 返回当前服务器状态
	 * @return
	 */
	public int getStatus(){
		return mainActivity.status;
	}
	
	/**
	 * 关闭服务器
	 */
	public void closeServer(){
		if(mainActivity!=null)
		mainActivity.closedServer();
	}
	
	
	/**
	 * 判断服务器是否在运行
	 * @return
	 */
	public boolean isRunning(){
		if(mainActivity==null) return false;
		return mainActivity.status == FTPServerConstants.statusRunning;
	}
}
