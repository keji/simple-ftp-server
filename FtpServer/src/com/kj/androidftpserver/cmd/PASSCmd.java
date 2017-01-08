package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;
/**
 * 服务器对PASS命令的解析和回答
 * @author keke
 *
 */
public class PASSCmd implements Cmd{

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {
		
		String password = readLine.substring(5);
		password = password.trim();
		writer.println("230 User "+receivedSocket.getUsername()+" logged in");
		//writer.println("530 User logged in");
		
		writer.flush();
	
		
		
	}

	@Override
	public void dealWithInChan(String info, SelectionKey key) {
		String password = info.substring(5);
		password = password.trim();
		
		SocketChanInfo chanInfo = (SocketChanInfo)key.attachment();
		
		ByteBuffer buf = chanInfo.getSendBuf();
		
		//buf.clear();
		try{
		buf.put(("230 User "+chanInfo.getUsername()+" logged in\r\n").getBytes(chanInfo.getEncoding()));
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
		
	}

}
