package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.kj.androidftpserver.DataThread;
import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;
import com.kj.androidftpserver.util.ExceptionUtils;
import com.kj.androidftpserver.util.FileUtils;
/**
 * LIST命令处理逻辑
 * @author keji
 * @2013-4-8
 */
public class LISTCmd implements Cmd{

	public void dealWithInChan(String info,SelectionKey key){
		SocketChanInfo socketInfo = (SocketChanInfo)key.attachment();
		//在SelectionKey中保存着一个数据服务器
		ServerSocketChannel srvSocketChan = socketInfo.getDataServerChan();
		//重新设置为null
		socketInfo.setDataServerChan(null);
		if(srvSocketChan==null){
			System.out.println("命令错误!");
			//向客户端返回命令错误
			ExceptionUtils.sendErrorCommend(key,"LIST");
			return;
		}
		
		SocketChanInfo dataSocketInfo = new SocketChanInfo();
		//设置此信息类为目录服务器
		dataSocketInfo.setTag(SocketChanInfo.STR_DATA_SERVER);
		//设置命令SocketKey
		dataSocketInfo.setCmdSocketKey(key);
		//得到当前窗口的文件夹目录
		String fileListStr = FileUtils.getFilesList(socketInfo.getCurLocation());
		
		if(fileListStr==null){
			System.out.println("目录错误!");
			//向客户端返回命令错误
			ExceptionUtils.sendErrorCommend(key,"LIST");
			return;
		}
		
		
		try{
			
			
			if(!fileListStr.equals("")){
				dataSocketInfo.setStrDatas(fileListStr.getBytes(socketInfo.getEncoding()));
			}else{
				dataSocketInfo.setStrDatas(null);
			}
		srvSocketChan.register(key.selector(), SelectionKey.OP_ACCEPT,dataSocketInfo);
		
		//设置命令连接要返回的数据
		SocketChannel socketChan = (SocketChannel)key.channel();
		ByteBuffer sendBuffer = socketInfo.getSendBuf();
		sendBuffer.put("150 Connection accepted\r\n".getBytes(socketInfo.getEncoding()));
		key.interestOps(SelectionKey.OP_WRITE);
		
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {
		// TODO Auto-generated method stub
		
	}
}
	
	
	
	/*@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket,
			ServerSocket dateServer) {
		
		ListThread listThread = new ListThread(dateServer,writer,receivedSocket);
		receivedSocket.getDateTransExec().execute(listThread);
		
	}
	*//**
	 * List应答线程
	 * @author keji
	 * @2013-4-8
	 *//*
	class ListThread extends DataThread{
		//数据连接服务器
		ServerSocket dataServer = null;
		//命令连接的输出
		PrintWriter commendWriter = null;
		
		ReceivedSocket receivedSocket = null;
		
		//用于数据传输的Socket
		Socket dataSocket = null;
		
		public ListThread(ServerSocket dateServer,PrintWriter commendWriter,ReceivedSocket receivedSocket) {
			this.dataServer = dateServer;
			this.commendWriter = commendWriter;
			this.receivedSocket = receivedSocket;
		}
		
		
		@Override
		public void run() {
			
			try{
			if((dataSocket = dataServer.accept())!=null){
				
				final PrintWriter writer = new PrintWriter(new OutputStreamWriter(dataSocket.getOutputStream(),"utf-8"));
				//final BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				String fileListStr = FileUtils.getFilesList(receivedSocket.getCurLocation());
				if(!fileListStr.equals("")){
				writer.print(fileListStr);
				writer.flush();
				}
				commendWriter.println("226 Transfer OK");
				commendWriter.flush();
				
			}
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				try{
				if(dataSocket!=null)
					dataSocket.close();
				if(dataServer!=null)
					dataServer.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
	}
	*/
	


