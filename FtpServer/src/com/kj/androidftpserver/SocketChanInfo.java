package com.kj.androidftpserver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.util.Arrays;
/**
 * 存储连接信息的类
 * @author keji
 * @2013-4-18
 */
public class SocketChanInfo {
	
	public static final int NOT_SET = -1; //未设置
	public static final int CMD_SERVER = 0;  //控制命令服务器标志
	public static final int STR_DATA_SERVER = 1;//打印目录服务器标志
	public static final int CMD_SOCKET = 2; // 控制命令
	public static final int STR_DATA_SOCKET = 3; //打印目录Socket标志
	public static final int FILE_DOWN_SOCKET = 4; //下载文件socket标志
	public static final int FILE_DOWN_SERVER = 5; //下载文件Server标志
	public static final int FILE_UP_SOCKET = 6;//上传文件socket标志
	public static final int FILE_UP_SERVER = 7;//上传文件server标志
	
	private ByteBuffer sendBuf = null;
	private ByteBuffer receiveBuf = null;
	
	private String username = null;
	private String password = null;
	private String encoding = "utf-8";
	
	//String curLocation = "D:/";
	//String rootLocation = "D:";
	
	volatile String curLocation = "/home/";
	volatile String rootLocation = "/home";
	
	private ServerSocketChannel dataServerChan = null; // 数据服务器信道
	
	private int tag = NOT_SET; //此信息的标志
	
	/*
	 * 目录传输
	 */
	private byte[] strDatas; //字符串数据
	private int contentLen = 0;
	
	/*
	 * 文件传输
	 */
	private String downFilePath; //传输到服务器的文件路径
	private String upFilePath;//上传服务器文件地址
	
	
	private FileChannel downFileChannel; //需要下载的文件的通道;
	private long  totalSize = 0; //需要下载的文件大小
	private FileChannel upFileChannel;//需要上传的文件通道
	
	
	private SelectionKey dataServerKey = null;//传输数据服务器Key
	private SelectionKey cmdSocketKey = null;//命令socket,发送完数据,命令回复用
	
	/*
	 * 重命名
	 */
	private File rnfrFile  = null; //需要重命名的文件 
	
	
	
	public String getUpFilePath() {
		return upFilePath;
	}
	
	/**
	 * 设置上传文件的路径
	 * @param upFilePath 上传文件路径
	 * @throws FileNotFoundException 上传文件不存在
	 */
	public void setUpFilePath(String upFilePath) throws IOException {
		/*
		 * 如果上传文件路径为null,那么就关闭 upFileChannel(上传文件信道)
		 */
		if(upFilePath==null){
			if(upFileChannel!=null){
				try{
					upFileChannel.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		
		//上传文件赋值
		this.upFilePath = upFilePath;
		
		/*
		 * 如果文件不存在,就创建新文件
		 */
		File upFile = new File(upFilePath);
		
		System.out.println(upFilePath);
		
		if(!upFile.exists()){
			upFile.createNewFile();
		}
	
			upFileChannel = new RandomAccessFile(upFile,"rw").getChannel();
			
		
		
		
		
	}
	/**
	 * 存储buf到文件中
	 * @param buf
	 */
	public boolean saveBufToFile(ByteBuffer buf) throws IOException{
		if(buf==null){
			if(upFileChannel!=null){
				upFileChannel.close();
			}
			return false;
		}
		buf.flip();
		upFileChannel.write(buf);
		//如果buf中没有写完,继续写,知道写完为止
		while(buf.hasRemaining()){
			System.out.println("循环读写中!!!!");
			upFileChannel.write(buf);
		}
		return true;
	}
	
	
	
	public String getDownFilePath() {
		return downFilePath;
	}
	/**
	 * 设置传出文件路径,同时获取传出文件映射buf
	 * @param downFilePath
	 */
	public void setDownFilePath(String downFilePath) {
		if(downFilePath==null){
		 if(downFileChannel!=null){
			try{
			 downFileChannel.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		 }
		}
		this.downFilePath = downFilePath;
		try{
		downFileChannel = new RandomAccessFile(new File(downFilePath),"r").getChannel();
		//得到文件总字节数
		totalSize = downFileChannel.size();
		System.out.println("totalSize--->"+totalSize);
		
		
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 得到剩余需要传输的内存映射文件段个数
	 * @return
	 */
	/*public int getMappedFileCntForTran(){
		return mappedFileBufCount - tranMappedFileBufCount;
	}*/
	
	/**
	 * 从文件中读取数据到buf
	 * @param buf
	 * @return
	 * @throws IOException
	 */
	public ByteBuffer getBufForFile(ByteBuffer buf) throws IOException{
		if(buf==null) return null;
		buf.clear();
		int len = downFileChannel.read(buf);
		if(len==-1){
			downFileChannel.close();
			return null;
		}
		return buf;
	}
	
	/**
	 * 得到需要传输出的字节数组
	 * @return
	 */
	/*public byte[] getFileDatesForBuf(int bufSize){
		//剩余需要传送的字节数
		long existBytesCount = getExistBytesCount();
		
		//如果剩余需要传送的字节数,比bufSize小,那么数组大小要设为剩余字节数大小
		byte[] rtnBytes;
		if(existBytesCount<=bufSize){
			rtnBytes = new byte[(int)existBytesCount];
			tranBytesCountInBuf = totalBytesCountInBuf;
		}else{
			rtnBytes = new byte[bufSize];
			tranBytesCountInBuf = tranBytesCountInBuf + bufSize;
		}
		
		mappedFileBuffer.get(rtnBytes);
		return rtnBytes;
	}*/
	/**
	 * 得到文件传送的剩余字节数
	 * @return
	 */
	/*public long getExistBytesCount(){
		return totalBytesCountInBuf-tranBytesCountInBuf;
	}*/
	
	
	
	
	
	
	public SelectionKey getDataServerKey() {
		return dataServerKey;
	}
	public void setDataServerKey(SelectionKey dataServerKey) {
		this.dataServerKey = dataServerKey;
	}
	public ServerSocketChannel getDataServerChan() {
		return dataServerChan;
	}
	public void setDataServerChan(ServerSocketChannel dataServerChan) {
		this.dataServerChan = dataServerChan;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public byte[] getStrDatas() {
		return strDatas;
	}
	public void setStrDatas(byte[] strDatas) {
		this.strDatas = strDatas;
		//如果不是空文件夹
		if(strDatas!=null)
		contentLen = strDatas.length;
	}
	
	
	/**
	 * 从字符数组中取出 bufSize 个长度的字节数组
	 * @param bufSize
	 * @return bufSize长度的字节数组
	 */
	public byte[] getDatasForBuf(int bufSize){
		if(bufSize>=contentLen){
			//得到数据字节数组中剩余的数据,因为 bufSize 比剩余字节大,因此可以全部装入buf中
			byte[] bytes = Arrays.copyOfRange(strDatas, strDatas.length-contentLen, strDatas.length);
			contentLen = 0;
			return bytes;
		}
		byte[] retnBytes = Arrays.copyOfRange(strDatas,strDatas.length-contentLen,strDatas.length-contentLen+bufSize);
		contentLen = contentLen - bufSize;
		return retnBytes;
	}
	/**
	 * 返回需要发送的数据长度
	 * @return
	 */
	public int getDatasToSendSize(){
		return contentLen;
	}
	
	
	
	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	public ByteBuffer getSendBuf() {
		return sendBuf;
	}
	public void setSendBuf(ByteBuffer buf) {
		this.sendBuf = buf;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCurLocation() {
		return curLocation;
	}
	public void setCurLocation(String curLocation) {
		this.curLocation = curLocation;
	}
	public String getRootLocation() {
		return rootLocation;
	}
	public void setRootLocation(String rootLocation) {
		this.rootLocation = rootLocation;
	}
	public ByteBuffer getReceiveBuf() {
		return receiveBuf;
	}
	public void setReceiveBuf(ByteBuffer receiveBuf) {
		this.receiveBuf = receiveBuf;
	}
	public SelectionKey getCmdSocketKey() {
		return cmdSocketKey;
	}
	public void setCmdSocketKey(SelectionKey cmdSocketKey) {
		this.cmdSocketKey = cmdSocketKey;
	}
	
	
	public FileChannel getUpFileChannel() {
		return upFileChannel;
	}
	public void setUpFileChannel(FileChannel upFileChannel) {
		this.upFileChannel = upFileChannel;
	}
	
	
  public String getPrintPath(String curPath){
	
	return curPath.substring(curPath.indexOf(rootLocation)+rootLocation.length());
}
  
  public File getRnfrFile() {
		return rnfrFile;
	}

	public void setRnfrFile(File rnfrFile) {
		this.rnfrFile = rnfrFile;
	}

}
