package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;

import com.kj.androidftpserver.DataCmdDecoder;
import com.kj.androidftpserver.DataConnection;
import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;
import com.kj.androidftpserver.util.IpUtils;
/**
 * 被动模式的数据连接
 * @author keji
 * @2013-4-8
 */
public class PASVCmd implements Cmd{
@Override
public void dealWith(String readLine, PrintWriter writer,
		BufferedReader reader, ReceivedSocket receivedSocket) {
	//判断是否绑定端口成功
	boolean isConnected = false;
	ServerSocket dateServer = null;
	int datePort;
	
	
	
	do{
	//数据传输临时端口
		datePort = 1025 + (int)(Math.random()*64511);
	//如果端口被占用返回null
	dateServer = DataConnection.startConnection(datePort);
	
	//当端口被占用时isConnected为false
	isConnected = (dateServer!=null)?true:false;
	
	}while(!isConnected);
	
	
	try {
		//服务器的IP地址
		//String hostIp = receivedSocket.getSocket().getInetAddress().getHostAddress();
		String hostIp = IpUtils.getAddress().getHostAddress();
		
		String retStr = "227 Entering Passive Mode ("+hostIp+","+datePort/256+","+datePort%256+")";
		//将字符串中的 ".",全部换成 ",",因为 返回的格式为:  "227 Entering Passive Mode (10,100,41,204,207,146)"
		writer.println(retStr.replaceAll("\\.", ","));
		writer.flush();
		
		//获取请求被动模式的数据连接的操作
		String dateCmdStr = reader.readLine();
		DataCmdDecoder.dealWithDateCmd(dateCmdStr, writer, reader, receivedSocket, dateServer);
		
		
	} catch (UnknownHostException e) {
		System.out.println("获取主机IP失败");
	}catch (IOException e){
		System.out.println("客户端断开");
	}
	
}

@Override
public void dealWithInChan(String info, SelectionKey key) {
	//判断是否绑定端口成功
		boolean isConnected = false;
		
		int datePort;
		
		
		//ServerSocket信道
		ServerSocketChannel listnChannel = null;
		try{
			listnChannel = ServerSocketChannel.open();
		}catch(IOException e){
			e.printStackTrace();
			return;
		}
		
		//随即选择端口绑定,如果被占用循环执行
		do{
			//数据传输临时端口
				datePort = 1025 + (int)(Math.random()*64511);
				
				try{
				
				listnChannel.socket().bind(new InetSocketAddress(datePort));
				isConnected = true;
				
				}catch(Exception e){
					//绑定识别,重新绑定
					isConnected = false;
				}
		}while(!isConnected);
		
		//将listnChannel设为不可阻塞
		try{
		listnChannel.configureBlocking(false);
		//注册此信道
		//listnChannel.register(key.selector(), SelectionKey.OP_ACCEPT,"dataCmdServer");
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//服务器的IP地址
				//String hostIp = receivedSocket.getSocket().getInetAddress().getHostAddress();
				String hostIp = IpUtils.getAddress().getHostAddress();
				
				String retStr = "227 Entering Passive Mode ("+hostIp+","+datePort/256+","+datePort%256+")\r\n";
				//将字符串中的 ".",全部换成 ",",因为 返回的格式为:  "227 Entering Passive Mode (10,100,41,204,207,146)"
				String sendOut = retStr.replaceAll("\\.", ",");
				
				SocketChanInfo chanInfo = (SocketChanInfo)key.attachment();
				
				//设置数据服务器信道
				chanInfo.setDataServerChan(listnChannel);
				
				ByteBuffer buf = chanInfo.getSendBuf();
			
				//buf.clear();
				try{
				//将要发送的字符串指令写入buf中
				buf.put(sendOut.getBytes(chanInfo.getEncoding()));
				}catch(Exception e){
					e.printStackTrace();
				}
				key.interestOps(SelectionKey.OP_WRITE);
				
				
				
				
				
		
		
		
}
}
