package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;
import com.kj.androidftpserver.util.FileUtils;

/**
 * 对SIZE 命令的解析和回答
 * @author keke
 *
 */
public class SIZECmd implements Cmd{

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {}
	
	public void dealWithInChan(String info,SelectionKey key){
		String filename = info.substring(5);
		filename = filename.trim();
		
		SocketChanInfo chanInfo = (SocketChanInfo)key.attachment();
		//得到指定文件大小
		long fileSize = FileUtils.getSingleFileSize(chanInfo.getCurLocation()+filename);
		
		ByteBuffer buf = chanInfo.getSendBuf();
		//buf.clear();
		try{
		buf.put(("213 "+fileSize+"\r\n").getBytes(chanInfo.getEncoding()));
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
}
