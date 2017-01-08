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

/**
 * 对RETR 命令的解析和回答(上传文件)
 * @author keke3
 *
 */
public class STORCmd implements Cmd{

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {}
	
	public void dealWithInChan(String info,SelectionKey key){
		String filename = info.substring(5);
		filename = filename.trim();
		
		//获取与命令SocketChannel 绑定的 SocketChanInfo
		SocketChanInfo socketInfo = (SocketChanInfo)key.attachment();
		//获取命令输出sendBuf
		ByteBuffer cmdSendBuf = socketInfo.getSendBuf();
		
		//在SelectionKey中保存着一个数据服务器
	  ServerSocketChannel srvSocketChan = socketInfo.getDataServerChan();
	//重新设置为null
	 socketInfo.setDataServerChan(null);
	
	 if(srvSocketChan==null){
			System.out.println("命令错误!");
			//向客户端返回命令错误
			ExceptionUtils.sendErrorCommend(key,"STOR");
			return;
		}
	 
	 SocketChanInfo dataSocketInfo = new SocketChanInfo();
	
	//设置此信息类为目录服务器
			dataSocketInfo.setTag(SocketChanInfo.FILE_UP_SERVER);
		
			//设置命令SocketKey,为了当数据传输完后,向客户端发送传输完成信息
			dataSocketInfo.setCmdSocketKey(key);	
			
			try{
				//设置上传文件路径
			dataSocketInfo.setUpFilePath(socketInfo.getCurLocation()+filename);
			
			/*将此ServerSocket注册,监听来自客户端的数据传输连接请求,此ServerSocket只会连接一个请求,
			/*传输完数据后就会关闭
			 */
			srvSocketChan.register(key.selector(), SelectionKey.OP_ACCEPT,dataSocketInfo);
			
			}catch(Exception e){
				e.printStackTrace();
				//向客户段返回命令错误
				ExceptionUtils.sendErrorCommend(key,"STOR");
				return;
			}
		
		
			MainServerContext.getInstance().addInfo("上传文件 "+filename);
		
		//buf.clear();
		try{
		cmdSendBuf.put(("150 Connection accepted\r\n").getBytes(socketInfo.getEncoding()));
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
}
