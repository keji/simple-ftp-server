package com.kj.androidftpserver;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.cmd.CWDCmd;
import com.kj.androidftpserver.cmd.Cmd;
import com.kj.androidftpserver.cmd.DELECmd;
import com.kj.androidftpserver.cmd.DataCmd;
import com.kj.androidftpserver.cmd.FEATCmd;
import com.kj.androidftpserver.cmd.LISTCmd;
import com.kj.androidftpserver.cmd.MKDCmd;
import com.kj.androidftpserver.cmd.NOOPCmd;
import com.kj.androidftpserver.cmd.OPTSCmd;
import com.kj.androidftpserver.cmd.PASSCmd;
import com.kj.androidftpserver.cmd.PASVCmd;
import com.kj.androidftpserver.cmd.PWDCmd;
import com.kj.androidftpserver.cmd.QUITCmd;
import com.kj.androidftpserver.cmd.RETRCmd;
import com.kj.androidftpserver.cmd.RMDCmd;
import com.kj.androidftpserver.cmd.RNFRCmd;
import com.kj.androidftpserver.cmd.RNTOCmd;
import com.kj.androidftpserver.cmd.SITECmd;
import com.kj.androidftpserver.cmd.SIZECmd;
import com.kj.androidftpserver.cmd.STORCmd;
import com.kj.androidftpserver.cmd.SYSTCmd;
import com.kj.androidftpserver.cmd.TYPECmd;
import com.kj.androidftpserver.cmd.USERCmd;
import com.kj.androidftpserver.cmd.UnknownCmd;

public class CmdDecoder {
	/**
	 * 对每个命令分配合适的解释器
	 */
	public static void dealWithCtrlCmdInChan(String info,SelectionKey key){
		if (info.toUpperCase().startsWith("USER")) {

			Cmd cmd = new USERCmd();
			cmd.dealWithInChan(info,key);

		} else if (info.toUpperCase().startsWith("PASS")) {

			Cmd cmd = new PASSCmd();
			cmd.dealWithInChan(info,key);
		} else if (info.toUpperCase().startsWith("EPRT")) {

		} else if (info.toUpperCase().startsWith("SYST")) {

			Cmd cmd = new SYSTCmd();
			cmd.dealWithInChan(info,key);
			
		} else if (info.toUpperCase().startsWith("SITE")) {
			Cmd cmd = new SITECmd();
			cmd.dealWithInChan(info,key);

		}else if(info.toUpperCase().startsWith("SIZE")){
			Cmd cmd = new SIZECmd();
			cmd.dealWithInChan(info, key);
		}else if(info.toUpperCase().startsWith("RETR")){
			Cmd cmd = new RETRCmd();
			cmd.dealWithInChan(info, key);
		}else if(info.toUpperCase().startsWith("STOR")){
			Cmd cmd = new STORCmd();
			cmd.dealWithInChan(info, key);
		}else if (info.toUpperCase().startsWith("LIST")) {
			Cmd cmd = new LISTCmd();
			cmd.dealWithInChan(info,key);

		}else if (info.toUpperCase().startsWith("PASV")) {

			Cmd cmd = new PASVCmd();
			cmd.dealWithInChan(info,key);
			
		} else if (info.toUpperCase().startsWith("OPTS")) {
			Cmd cmd = new OPTSCmd();
			cmd.dealWithInChan(info,key);
			
		} else if (info.toUpperCase().startsWith("CWD")) {
			Cmd cmd = new CWDCmd();
			cmd.dealWithInChan(info,key);
			
		} else if (info.toUpperCase().startsWith("PWD")) {
			
			Cmd cmd = new PWDCmd();
			cmd.dealWithInChan(info,key);
			
		} else if (info.toUpperCase().startsWith("TYPE")) {
			Cmd cmd = new TYPECmd();
			cmd.dealWithInChan(info,key);
		} else if (info.toUpperCase().startsWith("MKD")) {
			Cmd cmd = new MKDCmd();
			cmd.dealWithInChan(info,key);
			
		} else if (info.toUpperCase().startsWith("RNFR")) {
			Cmd cmd = new RNFRCmd();
			cmd.dealWithInChan(info,key);
		} else if (info.toUpperCase().startsWith("RNTO")) {
			Cmd cmd = new RNTOCmd();
			cmd.dealWithInChan(info,key);
		}else if (info.toUpperCase().startsWith("RMD")) {
			Cmd cmd = new RMDCmd();
			cmd.dealWithInChan(info,key);
		}else if (info.toUpperCase().startsWith("DELE")) {
			Cmd cmd = new DELECmd();
			cmd.dealWithInChan(info,key);
		}/*else if (info.toUpperCase().startsWith("FEAT")) {
			Cmd cmd = new FEATCmd();
			cmd.dealWithInChan(info,key);
		}*/
		
		else if (info.toUpperCase().startsWith("NOOP")) {
			Cmd cmd = new NOOPCmd();
			cmd.dealWithInChan(info,key);
			
		}else if (info.toUpperCase().startsWith("QUIT")) {
			
			Cmd cmd = new QUITCmd();
			cmd.dealWithInChan(info,key);
			
			
		}else{//如果不能识别此命令
			Cmd cmd = new UnknownCmd();
			cmd.dealWithInChan(info, key);
		}

		
		
	}
	
	/**
	 * 处理Socket模式的消息
	 * @param readLine 	读到的命令
	 * @param writer 	流写出
	 * @param reader	流读入
	 * @param receivedSocket socket的封转对象
	 */
	public static void dealWithCtrlCmd(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket) {

		if (readLine.toUpperCase().startsWith("USER")) {

			Cmd cmd = new USERCmd();
			cmd.dealWith(readLine, writer, reader, receivedSocket);

		} else if (readLine.toUpperCase().startsWith("PASS")) {

			Cmd cmd = new PASSCmd();
			cmd.dealWith(readLine, writer, reader, receivedSocket);

		} else if (readLine.toUpperCase().startsWith("EPRT")) {

		} else if (readLine.toUpperCase().startsWith("SYST")) {

			Cmd cmd = new SYSTCmd();
			cmd.dealWith(readLine, writer, reader, receivedSocket);
			
		} else if (readLine.toUpperCase().startsWith("SITE")) {
			Cmd cmd = new SITECmd();
			cmd.dealWith(readLine, writer, reader, receivedSocket);

		} else if (readLine.toUpperCase().startsWith("PASV")) {

			Cmd cmd = new PASVCmd();
			cmd.dealWith(readLine, writer, reader, receivedSocket);
			
		} else if (readLine.toUpperCase().startsWith("OPTS")) {
			Cmd cmd = new OPTSCmd();
			cmd.dealWith(readLine, writer, reader, receivedSocket);
			
		} else if (readLine.toUpperCase().startsWith("CWD")) {
			Cmd cmd = new CWDCmd();
			cmd.dealWith(readLine, writer, reader, receivedSocket);
			
		} else if (readLine.toUpperCase().startsWith("PWD")) {
			
			Cmd cmd = new PWDCmd();
			cmd.dealWith(readLine, writer, reader, receivedSocket);
			
		} else if (readLine.toUpperCase().startsWith("TYPE")) {
			Cmd cmd = new TYPECmd();
			cmd.dealWith(readLine, writer, reader, receivedSocket);
		}

		else if (readLine.toUpperCase().startsWith("NOOP")) {
			Cmd cmd = new NOOPCmd();
			cmd.dealWith(readLine, writer, reader, receivedSocket);
			
		}else if (readLine.toUpperCase().startsWith("QUIT")) {
			
			Cmd cmd = new QUITCmd();
			cmd.dealWith(readLine, writer, reader, receivedSocket);
		}

	}
}
