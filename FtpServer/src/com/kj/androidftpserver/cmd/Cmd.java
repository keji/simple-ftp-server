package com.kj.androidftpserver.cmd;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.SelectionKey;

import com.kj.androidftpserver.ReceivedSocket;

public interface Cmd {
	void dealWith(String readLine, PrintWriter writer, BufferedReader reader, ReceivedSocket receivedSocket);
	public void dealWithInChan(String info, SelectionKey key);
}
