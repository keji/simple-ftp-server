package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;
import com.kj.androidftpserver.ui.MainServerContext;

/**
 * 对RETR 命令的解析和回答 (下载文件)
 * @author keke3
 *
 */
public class RETRCmd implements Cmd{

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {}
	
	public void dealWithInChan(String info,SelectionKey key){
		String filename = info.substring(5);
		filename = filename.trim();
		
		
		SocketChanInfo socketInfo = (SocketChanInfo)key.attachment();
		
		//在SelectionKey中保存着一个数据服务器
	  ServerSocketChannel srvSocketChan = socketInfo.getDataServerChan();
	//重新设置为null
	 socketInfo.setDataServerChan(null);
	
	 if(srvSocketChan==null){
			System.out.println("命令错误!");
			return;
		}
	 
	 SocketChanInfo dataSocketInfo = new SocketChanInfo();
	
	//设置此信息类为目录服务器
			dataSocketInfo.setTag(SocketChanInfo.FILE_DOWN_SERVER);
		
			//设置命令SocketKey,为了当数据传输完后
			dataSocketInfo.setCmdSocketKey(key);	
		
			dataSocketInfo.setDownFilePath(socketInfo.getCurLocation()+filename);
			try{
			//注册选择器,进行监听
			srvSocketChan.register(key.selector(), SelectionKey.OP_ACCEPT,dataSocketInfo);
			}catch(Exception e){
				e.printStackTrace();
			}
		
		
		ByteBuffer buf = socketInfo.getSendBuf();
		
		MainServerContext.getInstance().addInfo("下载文件 "+filename);
		//buf.clear();
		try{
		buf.put(("150 Connection accepted\r\n").getBytes(socketInfo.getEncoding()));
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
}
