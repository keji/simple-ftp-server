package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;

/**
 * 对USER 命令的解析和回答
 * @author keke
 *
 */
public class NOOPCmd implements Cmd{

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {
		
		writer.println("200 OK");
		writer.flush();
	}

	@Override
	public void dealWithInChan(String info, SelectionKey key) {
		
		SocketChanInfo chanInfo = (SocketChanInfo)key.attachment();
		ByteBuffer buf = chanInfo.getSendBuf();
		
		try{
			buf.put("200 OK\r\n".getBytes(chanInfo.getEncoding()));
			}catch(Exception e){
				e.printStackTrace();
			}
			key.interestOps(SelectionKey.OP_WRITE);
		
	}
	
}
