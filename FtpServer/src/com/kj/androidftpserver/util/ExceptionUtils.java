package com.kj.androidftpserver.util;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.FTPServerConstants;
import com.kj.androidftpserver.SocketChanInfo;

public class ExceptionUtils {
	/**
	 * 命令错误处理
	 * @param sendBuf
	 * @param key
	 */
	public static void sendErrorCommend(SelectionKey key,String cmd){
		String errorStr = FTPServerConstants.errorCommend;
		
		/*
		 * 命令错误分类
		 */
		if("CWD".equals(cmd)){
			errorStr = FTPServerConstants.cwdErrorCommend;
		}else if("STOR".equals(cmd)){
			errorStr = FTPServerConstants.storErrorCommend;
		}else if("LIST".equals(cmd)){
			errorStr = FTPServerConstants.listErrorCommend;
		}
		
		
		//获取与命令SocketChannel 绑定的 SocketChanInfo
		SocketChanInfo socketInfo = (SocketChanInfo)key.attachment();
				
		//获取命令输出sendBuf
		ByteBuffer cmdSendBuf = socketInfo.getSendBuf();
		try{
		cmdSendBuf.put(errorStr.getBytes(socketInfo.getEncoding()));
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
}
