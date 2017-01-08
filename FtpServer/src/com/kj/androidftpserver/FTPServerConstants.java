package com.kj.androidftpserver;

public class FTPServerConstants {
	public final static int statusRunning = 11;
	public final static int statusClosed = 12;
	
	public static int ctrlCmdPort = 8080;
	
	public final static String newClientReady = "220 FTP server(V1.0) ready\r\n";
	public final static String unknownCommend = "502 Commend not implemented\r\n";
	//未执行命令
	public final static String errorCommend = "550 Commend error\r\n";
	
	public final static String cwdErrorCommend = "550 CWD failed.directory not found.\r\n";
	
	public final static String storErrorCommend = "550 STOR failed.上传文件失败.\r\n";
	
	public final static String listErrorCommend = "550 LIST failed.目录错误.\r\n";
	
	public static int cmdBufSize = 1024;
	public static int dataBufSize = 1024;
}
