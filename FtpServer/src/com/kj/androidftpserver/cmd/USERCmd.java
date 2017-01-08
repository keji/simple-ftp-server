package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;

/**
 * 对USER 命令的解析和回答
 * @author keke
 *
 */
public class USERCmd implements Cmd{

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {
		
		String username = readLine.substring(5);
		username = username.trim();
		
		receivedSocket.setUsername(username);
		
		writer.println("331 password required for "+username);
		writer.flush();
		
	}
	
	public void dealWithInChan(String info,SelectionKey key){
		String username = info.substring(5);
		username = username.trim();
		SocketChanInfo chanInfo = (SocketChanInfo)key.attachment();
		//设置客户登陆名
		chanInfo.setUsername(username);
		
		ByteBuffer buf = chanInfo.getSendBuf();
		//buf.clear();
		try{
		buf.put(("331 password required for "+username+"\r\n").getBytes(chanInfo.getEncoding()));
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
}
