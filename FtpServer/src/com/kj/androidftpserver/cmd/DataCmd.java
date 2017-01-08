package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.ReceivedSocket;

public interface DataCmd {
	void dealWith(String readLine, PrintWriter writer, BufferedReader reader, ReceivedSocket receivedSocket, ServerSocket dateServer);
	public void dealWithInChan(String info, SelectionKey key);
}

