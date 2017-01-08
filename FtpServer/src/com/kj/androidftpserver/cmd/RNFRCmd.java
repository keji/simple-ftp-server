package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.ReceivedSocket;
import com.kj.androidftpserver.SocketChanInfo;
import com.kj.androidftpserver.ui.MainServerContext;
import com.kj.androidftpserver.util.ExceptionUtils;
import com.kj.androidftpserver.util.FileUtils;

/**
 * 对RNFR 命令的解析和回答
 * @author keke3
 *
 */
public class RNFRCmd implements Cmd{

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
        //得到重命名文件的File对象
		File rnfrFile = FileUtils.fileToRename(socketInfo.getCurLocation()+filename);
		
		if(rnfrFile==null){
			//如果文件不存在,就发回550错误信息.
			ExceptionUtils.sendErrorCommend(key,"RNFR");
			return;
		}
		//设置重命名文件
		socketInfo.setRnfrFile(rnfrFile);
		
		
		//显示创建信息
			MainServerContext.getInstance().addInfo("重命名文件: "+filename);
		
			String fileType =null;
			if(rnfrFile.isFile()){
				fileType = "File";
			}else{
				fileType = "Directory";
			}
		
		try{
			
		cmdSendBuf.put(("350 "+fileType+" exists, ready for destination name.\r\n").getBytes(socketInfo.getEncoding()));
		}catch(Exception e){
			e.printStackTrace();
		}
		key.interestOps(SelectionKey.OP_WRITE);
	}
	
}
