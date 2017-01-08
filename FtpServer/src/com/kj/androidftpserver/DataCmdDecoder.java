package com.kj.androidftpserver;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

import com.kj.androidftpserver.cmd.DataCmd;
import com.kj.androidftpserver.cmd.LISTCmd;

public class DataCmdDecoder {
	public static void dealWithDateCmd(String readLine, PrintWriter writer,
			BufferedReader reader, ReceivedSocket receivedSocket,ServerSocket dateServer) {

		if (readLine.toUpperCase().startsWith("LIST")) {/*
			DataCmd listCmd = new LISTCmd();
			listCmd.dealWith(readLine, writer, reader, receivedSocket, dateServer);
		*/}

	}
}
