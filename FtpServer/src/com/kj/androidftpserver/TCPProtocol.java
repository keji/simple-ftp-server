package com.kj.androidftpserver;
import java.io.IOException;
import java.nio.channels.SelectionKey;


public interface TCPProtocol {
	void handleAccept(SelectionKey key) throws Exception;
	void handleRead(SelectionKey key) throws Exception;
	void handleWrite(SelectionKey key) throws Exception;
}
