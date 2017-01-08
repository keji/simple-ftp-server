package com.kj.androidftpserver;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import com.kj.androidftpserver.ui.MainServerContext;

/**
 * FTP选择器协议
 * @author keji
 * @2013-4-17
 */
public class FTPSelectorProtocol implements TCPProtocol{
	
	public FTPSelectorProtocol(){
		
	}
	/**
	 * 接受连接
	 */
	public void handleAccept(SelectionKey key) throws IOException{
		//能接收accept事件的都是 ServerSocketChannel
		ServerSocketChannel ssc = (ServerSocketChannel)key.channel();
		//接受来自客户段的连接
		SocketChannel clntChan = ssc.accept();
		//设置此SocketChannel为非阻塞IO,这是SocketChannel的精华所在
		clntChan.configureBlocking(false);
		
		//得到与此ServerSocketChannel相绑定的SocketChanInfo,此对象可以分辨出此ServerSocketChannel,的类型
		//根据类型进行相应的操作
		SocketChanInfo svrChanInfo = (SocketChanInfo)key.attachment();
		
		//如果是有新客户端请求连接服务器
		if(svrChanInfo.getTag()==(SocketChanInfo.CMD_SERVER)){
			
			//每一个Socket连接都需要两个ByteBuffer供读写使用
			ByteBuffer sendBuf = ByteBuffer.allocate(FTPServerConstants.cmdBufSize);
			ByteBuffer receiveBuf = ByteBuffer.allocate(FTPServerConstants.cmdBufSize);
			
			//返回新客户端 连接成功信息
			sendBuf.put(FTPServerConstants.newClientReady.getBytes("utf-8"));
			
			//为新连接创建一个存储信息的对象
			SocketChanInfo chanInfo = new SocketChanInfo();
			//设置新连接的信息
			chanInfo.setSendBuf(sendBuf);
			chanInfo.setReceiveBuf(receiveBuf);
			
			//设置通道绑定信息类型为 命令Socket
			chanInfo.setTag(SocketChanInfo.CMD_SOCKET);
			//注册此连到选择器,并制定需要的操作是写操作
			clntChan.register(key.selector(), SelectionKey.OP_WRITE, chanInfo);
			
		     MainServerContext.getInstance().addInfo("新客户端接入连接");
			
			
			//当绑定信息的类别是数据传输(字符串传输时)
		}else if(svrChanInfo.getTag()==(SocketChanInfo.STR_DATA_SERVER)){
			
			
			
			svrChanInfo.setTag(SocketChanInfo.STR_DATA_SOCKET);
			
			ByteBuffer sendBuf = ByteBuffer.allocate(FTPServerConstants.dataBufSize);
			
			//如果为空目录
			if(svrChanInfo.getStrDatas()!=null){
				
			//将写出信息放入缓冲
			sendBuf.put(svrChanInfo.getDatasForBuf(FTPServerConstants.dataBufSize));
			svrChanInfo.setSendBuf(sendBuf);
			}else{
				svrChanInfo.setSendBuf(null);
			}
			
			//设置服务器Key为以后数据传输完的关闭用
			svrChanInfo.setDataServerKey(key);
			
			clntChan.register(key.selector(), SelectionKey.OP_WRITE, svrChanInfo);
			//如果是下载命令服务
		}else if(svrChanInfo.getTag()==SocketChanInfo.FILE_DOWN_SERVER){
			//设置消息类型为 FILE_DOWN_SOCKET
			svrChanInfo.setTag(SocketChanInfo.FILE_DOWN_SOCKET);
			
			ByteBuffer sendBuf = ByteBuffer.allocate(FTPServerConstants.dataBufSize);
			
			sendBuf = svrChanInfo.getBufForFile(sendBuf);
			svrChanInfo.setSendBuf(sendBuf);
			
			svrChanInfo.setDataServerKey(key);
			
			clntChan.register(key.selector(),SelectionKey.OP_WRITE,svrChanInfo);
			//如果是上传命令服务
		}else if(svrChanInfo.getTag()==SocketChanInfo.FILE_UP_SERVER){
			//设置消息类型为FILE_UP_SERVER
			svrChanInfo.setTag(SocketChanInfo.FILE_UP_SOCKET);
			
			ByteBuffer receiveBuf = ByteBuffer.allocate(FTPServerConstants.dataBufSize);
            svrChanInfo.setReceiveBuf(receiveBuf);
			
			svrChanInfo.setDataServerKey(key);
			//上传是从客户端读文件
			clntChan.register(key.selector(),SelectionKey.OP_READ,svrChanInfo);
		}
		
		
		
	}
	/**
	 * 协议读入部分
	 */
	public void handleRead(SelectionKey key) throws IOException {
		// Client socket channel has pending data
		SocketChannel clntChan = (SocketChannel)key.channel();
		
		SocketChanInfo socketChanInfo = (SocketChanInfo)key.attachment();
		
		ByteBuffer receiveBuf = socketChanInfo.getReceiveBuf();
		
		//如果此key为命令连接
		if(socketChanInfo.getTag()==SocketChanInfo.CMD_SOCKET){
		receiveBuf.clear();
		long bytesRead = clntChan.read(receiveBuf);
		
		//如果客户端关闭了流,就退出
		if(bytesRead == -1){//Did the other end close?
			System.out.println("客户关闭了Socket");
			MainServerContext.getInstance().addInfo("客户关闭了Socket");
			clntChan.close();
			key.cancel();
			return;
		}
		//得到buf中的所有字节
		byte[] bytes=new byte[receiveBuf.position()];
		receiveBuf.position(0);
		receiveBuf.get(bytes);
		
		String str = new String(bytes,"utf-8");
		
		if(str.indexOf("\n")!=-1){
			//得到传入的命令
			String info = str.substring(0, str.indexOf("\n")+1);
			System.out.println(info);
			
			
			
			//调用指令解码器处理传给服务器的指令
			CmdDecoder.dealWithCtrlCmdInChan(info,key);
			
			
			
		}else{
			//数据没有结束,继续读数据
			key.interestOps(SelectionKey.OP_READ);
			return;
		}
		//如果此key为文件上传
		}else if(socketChanInfo.getTag()==SocketChanInfo.FILE_UP_SOCKET){
			receiveBuf.clear();
			
			long bytesRead = clntChan.read(receiveBuf);
			
			//如果客户端关闭了流,就退出
			if(bytesRead == -1){//Did the other end close?
				socketChanInfo.saveBufToFile(null);
				System.out.println("文件传输结束!!");
				
				/*
				 * 给客户端返回成功传输命令
				 */
				SelectionKey cmdKey = socketChanInfo.getCmdSocketKey();
				SocketChanInfo cmdChanInfo = (SocketChanInfo)cmdKey.attachment();
				
				System.out.println("position--->"+cmdChanInfo.getSendBuf().position());
				
				cmdChanInfo.getSendBuf().put("226 Transfer OK\r\n".getBytes("utf-8"));
				
				cmdKey.interestOps(SelectionKey.OP_WRITE);
				
				
				/*
				 * 关闭数据连接(Server和Socket)
				 */
				clntChan.close();
				key.cancel();
				SelectionKey dataServerKey = socketChanInfo.getDataServerKey();
				dataServerKey.channel().close();
				dataServerKey.cancel();
				return;
			}
			socketChanInfo.saveBufToFile(receiveBuf);
			//数据没有结束,继续读数据
			key.interestOps(SelectionKey.OP_READ);
			return;
			
			
		}
		
	}
	/**
	 * 协议的写出部分
	 */
	public void handleWrite(SelectionKey key) throws Exception{
		
		
		
		SocketChanInfo socketChanInfo = (SocketChanInfo)key.attachment();
		
		if(socketChanInfo.getTag()==SocketChanInfo.STR_DATA_SOCKET){
			
			ByteBuffer buf = socketChanInfo.getSendBuf();
			SocketChannel clntChan = (SocketChannel) key.channel();
			
			try{
			//如果不是空字符串
			if(buf!=null){
			buf.flip();
			clntChan.write(buf);
			//如果缓存中还没有写出完,
			if(buf.hasRemaining()){
				key.interestOps(SelectionKey.OP_WRITE);
				buf.compact();
				return;
			}
			//如果需要发送的字节数组还没有发送完,那么取出buf.limit大小的字符数组发送
			if(socketChanInfo.getDatasToSendSize()!=0){
				buf.clear();
				
				buf.put(socketChanInfo.getDatasForBuf(buf.limit()));
				key.interestOps(SelectionKey.OP_WRITE);
				return;
			}
			
			}
				//给客户端返回成功传输命令
				SelectionKey cmdKey = socketChanInfo.getCmdSocketKey();
				SocketChanInfo cmdChanInfo = (SocketChanInfo)cmdKey.attachment();
				
				cmdChanInfo.getSendBuf().put("226 Transfer OK\r\n".getBytes("utf-8"));
				
				cmdKey.interestOps(SelectionKey.OP_WRITE);
				
			}catch(Exception e){
				//如果发生异常,那么关闭Server信道,和Socket信道
				
					clntChan.close();
					key.cancel();
					SelectionKey dataServerKey = socketChanInfo.getDataServerKey();
					dataServerKey.channel().close();
					dataServerKey.cancel();
					
					
				throw new Exception(e);
			}
			
				//关闭SocketChannel,SocketServerChannel,以及他们各自的key
				
				clntChan.close();
				key.cancel();
				SelectionKey dataServerKey = socketChanInfo.getDataServerKey();
				dataServerKey.channel().close();
				dataServerKey.cancel();
				
				
			
				
				//key.interestOps(SelectionKey.OP_READ);
				//buf.clear();
			
		}else if(socketChanInfo.getTag()==SocketChanInfo.FILE_DOWN_SOCKET){
		
			ByteBuffer buf = socketChanInfo.getSendBuf();
			SocketChannel clntChan = (SocketChannel) key.channel();
		try{	
			//如果不是空字符串
			if(buf!=null){
			buf.flip();
			clntChan.write(buf);
			//如果缓存中还没有写出完,
			if(buf.hasRemaining()){
				key.interestOps(SelectionKey.OP_WRITE);
				buf.compact();
				return;
			}
			/*//如果需要发送的字节数组还没有发送完,那么取出buf.limit大小的字符数组发送
			if(socketChanInfo.getExistBytesCount()>0){
				buf.clear();
				buf.put(socketChanInfo.getFileDatesForBuf(buf.limit()));
				key.interestOps(SelectionKey.OP_WRITE);
				return;
			}
			//如果文件还有每发送完的数据段,那么加载文件的下一段
			if(socketChanInfo.getMappedFileCntForTran()>0){
				//设置下一段文件段为内存映射文件
				socketChanInfo.getNextMappedFileBuf();
				
				buf.clear();
				buf.put(socketChanInfo.getFileDatesForBuf(buf.limit()));
				key.interestOps(SelectionKey.OP_WRITE);
				return;
			}*/
			buf = socketChanInfo.getBufForFile(buf);
			if(buf!=null){
				key.interestOps(SelectionKey.OP_WRITE);
				return;
			}
			}
			System.out.println("传输完成!!");
			
			//给客户端返回成功传输命令
			SelectionKey cmdKey = socketChanInfo.getCmdSocketKey();
			SocketChanInfo cmdChanInfo = (SocketChanInfo)cmdKey.attachment();
			cmdChanInfo.getSendBuf().put("226 Transfer OK\r\n".getBytes("utf-8"));
			cmdKey.interestOps(SelectionKey.OP_WRITE);
		}catch(Exception e){
			//关闭SocketChannel,SocketServerChannel,以及他们各自的key
			//e.printStackTrace();
			
			//给客户端返回异常关闭命令
			SelectionKey cmdKey = socketChanInfo.getCmdSocketKey();
			SocketChanInfo cmdChanInfo = (SocketChanInfo)cmdKey.attachment();
			cmdChanInfo.getSendBuf().put("426 Connection closed; transfer aborted.\r\n".getBytes("utf-8"));
			cmdKey.interestOps(SelectionKey.OP_WRITE);
			
			
			
			clntChan.close();
			key.cancel();
			SelectionKey dataServerKey = socketChanInfo.getDataServerKey();
			dataServerKey.channel().close();
			dataServerKey.cancel();
			
			throw new Exception(e);
		}
			//关闭SocketChannel,SocketServerChannel,以及他们各自的key
			
			clntChan.close();
			key.cancel();
			SelectionKey dataServerKey = socketChanInfo.getDataServerKey();
			dataServerKey.channel().close();
			dataServerKey.cancel();
			
		}else if(socketChanInfo.getTag()==SocketChanInfo.CMD_SOCKET){
			
			ByteBuffer buf = socketChanInfo.getSendBuf();
			buf.flip();
			
			SocketChannel clntChan = (SocketChannel) key.channel();
			try{
			clntChan.write(buf);
			}catch(IOException e){
				System.out.println("客户断开连接!");
				MainServerContext.getInstance().addInfo("客户断开连接!");
				clntChan.close();
				key.cancel();
				throw new IOException(e);
			}
			
			//如果缓存中还没有写出完,
			if(buf.hasRemaining()){
				key.interestOps(SelectionKey.OP_WRITE);
				buf.compact();
			}else{
				key.interestOps(SelectionKey.OP_READ);
				buf.clear();
			}
		}
		
		
		
		}
}











