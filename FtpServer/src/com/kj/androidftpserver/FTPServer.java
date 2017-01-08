package com.kj.androidftpserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import com.kj.androidftpserver.cmd.Cmd;
import com.kj.androidftpserver.cmd.USERCmd;
/**
 * FTP服务器功能模块
 * @author keji
 * @2013-4-7
 */
public class FTPServer {
		private int ftpCtrlPort = 8888; //FTP控制连接端口 
		private ServerSocket server  = null;
		private int clientCount = 10;//线程池总数 
	
	public boolean startServer(){
		
		//初始化服务器参数
		initServer();
		
		try{
		server = new ServerSocket(ftpCtrlPort);//开启端口监听连接
		
		}catch(IOException e){
			e.printStackTrace();
			System.out.println("端口占用!");
			return false;
		}
		return true;
	}
	/**
	 * 监听Socket连接
	 */
	public void listenSocket(){
		
		new Thread(new acceptThread()).start();
		
	}
	
	private void initServer(){
		ftpCtrlPort = 21;
	}
	
	/**
	 * server接受客户端请求连接线程
	 * @author keke
	 *
	 */
	private class acceptThread implements Runnable{

		@Override
		public void run() {
			
			
			//线程池,同时最多有clientCount个线程工作
			//ExecutorService exec = Executors.newFixedThreadPool(clientCount);
			
			//线程池,没有限制,可以回收旧线程,如果线程空闲60秒以上,则移出线程池
			ExecutorService exec = Executors.newCachedThreadPool();
			
			
			//拒绝 服务线程池
			ExecutorService refuseExec = Executors.newSingleThreadExecutor();
			
			//数据传输线程池
			ExecutorService dateTransExec = Executors.newCachedThreadPool();
			
			//ThreadPoolExecutor
			try{
				Socket socket =  null;
			while((socket=server.accept())!=null){
				
				ReceivedSocket receivedSocket = new ReceivedSocket();
				receivedSocket.setSocket(socket);
				
				//设置数据传输线程池
				receivedSocket.setDateTransExec(dateTransExec);
				
				//查询线程池中活动的线程
				int linkedClientCount = ((ThreadPoolExecutor)(exec)).getActiveCount();
				//如果活动的线程已经到达最大线程数目
				if(linkedClientCount>=clientCount){
					refuseExec.execute(new RefuseThread(receivedSocket));
				}else{
				exec.execute(new SocketThread(receivedSocket));
				}
				
				
				///////////////////测试//////////////////////////
				//BlockingQueue<Runnable> tasks = ((ThreadPoolExecutor)exec).getQueue();
				
				////////////////////////////////////////////
			}
			
			}catch(IOException e){
				e.printStackTrace();
				System.out.println("server关闭!");
				
			}finally{
				//关闭线程池, 必须关闭
				//List<Runnable> tasks = exec.shutdownNow();
				//循环关闭线程
			/*	for(int i=0;i<tasks.size();i++){
					SocketThread socketTask = (SocketThread)tasks.get(i);
					socketTask.close();
				}*/
				
				
				exec.shutdown();
				refuseExec.shutdown();
				
				
				
				dateTransExec.shutdown();
			}
			
			
			
		}
		
	}
	
	
	/**
	 * 处理接收的socket
	 * @author keji
	 * @2013-4-7
	 */
	private class SocketThread implements Runnable{
		
		ReceivedSocket receivedSocket = null;
		
		public SocketThread(ReceivedSocket receivedSocket) {
			this.receivedSocket = receivedSocket;
		}
		
		@Override
		public void run() {
			
			try{
				
			//得到此链接的输入输出流
			final BufferedReader reader = new BufferedReader(new InputStreamReader(receivedSocket.socket.getInputStream()));
			final PrintWriter writer = new PrintWriter(receivedSocket.socket.getOutputStream());
			
			
			
				//连接成功后,向服务器发送220 新用户准备就绪
				writer.print(FTPServerConstants.newClientReady);
				writer.flush();
				
			String readLine = null;
			//读取从客户端传来的一条命令
			while((readLine=reader.readLine())!=null){
				System.out.println(readLine);
				//调用指令解码器处理传给服务器的指令
				CmdDecoder.dealWithCtrlCmd(readLine,writer,reader,receivedSocket);
				
			}
			
			}catch(IOException e){
				System.out.println("用户退出");
				e.printStackTrace();
			}finally{
				System.out.println("用户退出");
			}
		
		}
		
		//关闭Socket
		public void close(){
			if(receivedSocket!=null){
			receivedSocket.close();
			}
		}
	}
	
	
	/**
	 * 当用户超过限制时,就拒绝连接,返回给客户端    
	 * "421  Too many users are connected, please try again later"
	 * 此消息 421表示服务器拒绝服务
	 * @author keji
	 * @2013-4-7
	 */
	private class RefuseThread implements Runnable{

		ReceivedSocket receivedSocket = null;
		
		public RefuseThread(ReceivedSocket receivedSocket) {
			this.receivedSocket = receivedSocket;
		}
		
		@Override
		public void run() {
			
			try{
				
				//得到此链接的输入输出流
				final BufferedReader reader = new BufferedReader(new InputStreamReader(receivedSocket.socket.getInputStream()));
				final PrintWriter writer = new PrintWriter(receivedSocket.socket.getOutputStream());
				
				
					//连接成功后,向服务器发送220 新用户准备就绪
					writer.println(FTPServerConstants.newClientReady);
					writer.flush();
					
				String readLine = null;
				//读取从客户端传来的一条命令
				while((readLine=reader.readLine())!=null){
					System.out.println(readLine);
					
					if (readLine.toUpperCase().startsWith("USER")) {

						Cmd cmd = new USERCmd();
						cmd.dealWith(readLine, writer, reader, receivedSocket);

					} else if (readLine.toUpperCase().startsWith("PASS")) {
						//因为用户过多,拒绝提供服务
						writer.println("421  Too many users are connected, please try again later");
						writer.flush();
						receivedSocket.getSocket().close();
					} 
				}
				
				
				}catch(IOException e){
					System.out.println("用户退出");
					//e.printStackTrace();
				}
			
		}
	} 
	//关闭服务器
	public boolean closeServer(){
		if(server!=null){
			try{
				server.close();
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
}


