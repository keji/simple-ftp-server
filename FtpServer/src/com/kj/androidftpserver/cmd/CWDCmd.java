package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;
import com.kj.androidftpserver.util.ExceptionUtils;
import com.kj.androidftpserver.util.FileUtils;

/**
 * 对USER 命令的解析和回答
 * @author keke
 *
 */
public class CWDCmd implements Cmd{

	@Override
	public void dealWith(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {
		
		String path = readLine.substring(4);
		
		receivedSocket.setCurLocation(receivedSocket.getRootLocation()+path);
		
		writer.println("250 CWD successful. \""+path+"\" is current directory.");
		writer.flush();
		
	}

	@Override
	public void dealWithInChan(String info, SelectionKey key) {
		
		SocketChanInfo chanInfo = (SocketChanInfo)key.attachment();
		
		String path = info.substring(4);
		
		path = path.trim();
		
		
		/*
		 * 更新目录有两种形式,一种是绝对路径,一种是相对路径
		 */
		if(path.equals("..")){
			//如果已经是根目录,那么返回550错误信息
			if(chanInfo.getCurLocation().equals(chanInfo.getRootLocation()+"/")){
				ExceptionUtils.sendErrorCommend(key,"CWD");
				return;
			}
			path = FileUtils.preDirFromPath(chanInfo.getCurLocation());
			
		}else
		if(path.startsWith("/")){
			//如果绝对路径不是以/结尾,那么需要加上/,使其标准化
			if(!path.endsWith("/")){
				path = path+"/";
			}
			path = chanInfo.getRootLocation()+path;
		
		}else{
			path = chanInfo.getCurLocation()+path+"/";
		}

		File file = new File(path);
		if(!file.isDirectory()) {
			ExceptionUtils.sendErrorCommend(key,"CWD");
			return;
			//如果目录不存在,返回550错误信息
		}else if(!(file.exists())){
			ExceptionUtils.sendErrorCommend(key,"CWD");
			return;
		}
		
		chanInfo.setCurLocation(path);
		
		
		
		
		ByteBuffer buf = chanInfo.getSendBuf();
		
		//buf.clear();
		
		//System.out.println("250 CWD successful. \""+path+"\" is current directory.\r\n");
		try{
		buf.put(("250 CWD successful. \""+chanInfo.getPrintPath(path)+"\" is current directory.\r\n").getBytes(chanInfo.getEncoding()));
		
		
		
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
		
	}
	
}
