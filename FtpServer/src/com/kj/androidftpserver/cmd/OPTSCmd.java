package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;

/**
 * 对OPTS 命令的解析和回答
 * @author keke
 *
 */
public class OPTSCmd implements Cmd{

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {
		
		String[] strs = readLine.split(" ");
		//System.out.println(strs[1]);
		receivedSocket.setDecode(strs[1]);
		
		writer.println("200 "+strs[1]+" mode enabled");
		writer.flush();
		
	}

	@Override
	public void dealWithInChan(String info, SelectionKey key) {
		
		SocketChanInfo chanInfo = (SocketChanInfo)key.attachment();
		
		String[] strs = info.split(" ");
		chanInfo.setEncoding(strs[1]);
		
		ByteBuffer buf = chanInfo.getSendBuf();
		//buf.clear();
		try{
		buf.put(("200 "+strs[1]+" mode enabled\r\n").getBytes(chanInfo.getEncoding()));
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
		
	}
	
}
