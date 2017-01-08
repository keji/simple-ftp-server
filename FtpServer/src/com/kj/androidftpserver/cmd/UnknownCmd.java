package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;

/**
 * 不能识别命令
 * @author keke
 *
 */
public class UnknownCmd implements Cmd{

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {
		
	
		
	}

	@Override
	public void dealWithInChan(String info, SelectionKey key) {

		SocketChanInfo chanInfo = (SocketChanInfo)key.attachment();
		ByteBuffer buf = chanInfo.getSendBuf();
		//buf.clear();
		try{
		buf.put("502 Command not implemented\r\n".getBytes(chanInfo.getEncoding()));
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
	
		
	}
}
