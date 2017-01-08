package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;

/**
 * 退出命令,返回给客户端字符串  "221 Goodbye!"
 * @author keji
 * @2013-4-7
 */
public class QUITCmd implements Cmd{

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {
		
		writer.println("221 Goodbye!");
		writer.flush();
		receivedSocket.close();
		
	}

	@Override
	public void dealWithInChan(String info, SelectionKey key) {
		SocketChanInfo chanInfo = (SocketChanInfo)key.attachment();
		ByteBuffer buf = chanInfo.getSendBuf();
		//buf.clear();
		try{
		buf.put("221 Goodbye!\r\n".getBytes(chanInfo.getEncoding()));
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
		
	}
	
}
