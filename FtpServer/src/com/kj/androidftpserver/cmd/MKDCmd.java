package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.kj.androidftpserver.FTPServerConstants;
import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;
import com.kj.androidftpserver.ui.MainServerContext;
import com.kj.androidftpserver.util.ExceptionUtils;
import com.kj.androidftpserver.util.FileUtils;

/**
 * 对MKD 命令的解析和回答
 * @author keke3
 *
 */
public class MKDCmd implements Cmd{

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {}
	
	public void dealWithInChan(String info,SelectionKey key){
		String filename = info.substring(4);
		filename = filename.trim();
		
		
		
		//获取与命令SocketChannel 绑定的 SocketChanInfo
		SocketChanInfo socketInfo = (SocketChanInfo)key.attachment();
		//获取命令输出sendBuf
		ByteBuffer cmdSendBuf = socketInfo.getSendBuf();
		//创建文件夹
		boolean success = FileUtils.createNewDir(socketInfo.getCurLocation()+filename);
		
		//显示创建信息
			MainServerContext.getInstance().addInfo("新建文件夹: "+filename);
		//如果创建文件失败,就发回550错误信息.
			if(!success){
				ExceptionUtils.sendErrorCommend(key,"MKD");
				return;
			}
		
		
		try{
		cmdSendBuf.put(("257 Directory created successfully\r\n").getBytes(socketInfo.getEncoding()));
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
}
