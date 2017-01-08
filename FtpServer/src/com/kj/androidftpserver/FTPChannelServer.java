package com.kj.androidftpserver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;

import com.kj.androidftpserver.ui.MainServerContext;
import com.kj.androidftpserver.util.IpUtils;


public class FTPChannelServer {
	
	private boolean isExit = false;//退出标志
	
	private static final int TIMEOUT = 3000;//Wait timeout (milliseconds)
	
	Selector selector = null;
	
	//服务器信道
	ServerSocketChannel listnChannel = null;
	
	/*
	 * 单例模式
	 */
	private  static FTPChannelServer channelServer;
	
	private FTPChannelServer() {
		//
		
	}
	
	public static FTPChannelServer getInstance(int ftpCtrlPort){
		if(channelServer==null){
			channelServer = new FTPChannelServer();
		}
		return channelServer;
	}
	
	
	/**
	 * 初始化服务器
	 */
	private boolean initServer(){
		 InetAddress inetAddress = IpUtils.getAddress();
		 if(inetAddress==null){
		 	System.out.println("无网络连接");
			 return false;
		 }
		isExit = false;
		String status = "ftp://"+IpUtils.getAddress().getHostAddress()+":"+FTPServerConstants.ctrlCmdPort;
		//设置状态栏IP连接信息
		MainServerContext.getInstance().setStatusView(status);
		//控制台输出信息
		MainServerContext.getInstance().addInfo("建立连接:"+status);
		
		return true;
	}
	
	
	public boolean startServer(){
		
		//初始化服务器参数
		boolean success = initServer();
		if(!success) return false;
		
		try{
			//选择器打开,等待注册组件
			selector = Selector.open();
			
			
			listnChannel = ServerSocketChannel.open();
		//绑定端口
		listnChannel.socket().bind(new InetSocketAddress(FTPServerConstants.ctrlCmdPort));
		//监听为不可阻塞
		listnChannel.configureBlocking(false);
		
		SocketChanInfo svrChanInfo = new SocketChanInfo();
		svrChanInfo.setTag(SocketChanInfo.CMD_SERVER);
		
		listnChannel.register(selector, SelectionKey.OP_ACCEPT,svrChanInfo);
		//启动监听线程
		runServer();
		
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("服务器启动失败 ~_~,检查是否端口占用");
			return false;
		}
		
		return true;
		}

	
	/**
	 * 启动服务循环
	 */
	private void runServer(){
	new Thread(new Runnable(){
		
	@Override
	public void run() {
		
		TCPProtocol protocol = new FTPSelectorProtocol();
		
		while(!isExit){//isExit控制着程序是否关闭
			// 等待一些channel转备好,如果超过时间限制那么就返回0,继续循环
			try{
			if(selector.select(TIMEOUT) ==0){//返回0表示没有任何channel可以执行IO操作
			//	System.out.println(".");
				continue;
			}
			}catch(IOException e){
				System.out.println("服务器断开连接");
				break;
			}
			
			//得到SelectionKey的Set集合,循环判断SelectionKey可进行的操作
			Iterator<SelectionKey> keyIter = selector.selectedKeys().iterator();
			while(keyIter.hasNext()){
				SelectionKey key = keyIter.next();
				
				try{
				//ServerSocketChannel 有外部客户请求连接
				if(key.isValid() && key.isAcceptable()){
					protocol.handleAccept(key);
				}
				//Client socket channel has peding data?
				if(key.isValid() && key.isReadable()){
					protocol.handleRead(key);
				}
				//Client socket channel is available for writing and 
				if(key.isValid() && key.isWritable()){
					protocol.handleWrite(key);
				}
				
				}catch(Exception e){
					e.printStackTrace();
					//连接断开
					System.out.println("客户连接错误");
				}
				//由于select只是向Selector所关联的键集合中添加元素,因此如果不移出每个处理过的键,它就会在
				//下次调用select()方法是仍然保留在键集合中,而且可能会有无用的操作来调用它
				keyIter.remove();// remove from set of selected keys
			}//end while(keyIter.hasNext())
			
			
		}	
		
		try{
		System.out.println("退出了服务器");
		if(selector!=null)
		selector.close();
		if(listnChannel!=null){
		listnChannel.close();
		}
		MainServerContext.getInstance().setBtnClosed();
		
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}}).start();
	}
	/**
	 * 退出服务器
	 */
	public void closeServer(){
		isExit = true;
		MainServerContext.getInstance().addInfo("服务器关闭");
	}
	
	public static void main(String[] args) throws Exception {
		FTPChannelServer server = new FTPChannelServer();
		server.startServer();
		
	}
}
