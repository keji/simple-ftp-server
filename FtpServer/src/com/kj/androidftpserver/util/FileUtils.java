package com.kj.androidftpserver.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * 文件列表工具类
 * @author keji
 * @2013-4-11
 */
public class FileUtils {
	/**
	 * 根据文件夹位置列出文件夹下面所有文件
	 * @param folderPath
	 * @return
	 */
	
	public static String getFilesList(String folderPath){
		File desFolder = new File(folderPath);
		if(!desFolder.isDirectory()){
			return null;
		}
		File[] files = desFolder.listFiles();
		
		if(files==null){
			return "";
		}
		
		if(files.length!=0){
			Arrays.sort(files, new Comparator<File>() {
				@Override
				public int compare(File o1, File o2) {
					return o1.getName().compareTo(o2.getName());
				}
			});
			}
		
		StringBuffer sb = new StringBuffer();
		
		for(int i=0; i<files.length; i++){
			File file = files[i];
			if(file.isDirectory()){
				sb.append("d");
			}else{
				sb.append("-");
			}
			if(file.canRead()){
				sb.append("r");
			}else{
				sb.append("-");
			}
			if(file.canWrite()){
				sb.append("w");
			}else{
				sb.append("-");
			}
			if(file.canExecute()){
				sb.append("x");
			}else{
				sb.append("-");
			}
			String temStr = sb.substring(sb.length()-3);
			sb.append(temStr).append(temStr);
			
			sb.append(" ");
			sb.append("1");
			sb.append(" ");
			sb.append("ftp");
			sb.append(" ");
			sb.append("ftp");
			sb.append(" ");
			
			if(file.isDirectory()){
				sb.append("            0");
			}else{
				long fileSize = file.length();
				String fileSizeStr = fileSize+"";
				for(int j=0; j<13-fileSizeStr.length(); j++){
					sb.append(" ");
				}
				sb.append(fileSizeStr);
				
				
			}
			sb.append(" ");
			
			long lastModifiedTime = file.lastModified();
			
			
			sb.append(getDateStr(lastModifiedTime));
			sb.append(" ");
			sb.append(file.getName());
			sb.append("\r\n");
			//sb.append("")
		}
		
		
		
		return sb.toString();
	}
	
	private static String getDateStr(long lastModifiedTime){
		Date date = new Date(lastModifiedTime);
		
		Calendar calendar = Calendar.getInstance();
		
		int year = calendar.get(Calendar.YEAR);
		calendar.setTime(date);
		int fileYear = calendar.get(Calendar.YEAR);
		
		SimpleDateFormat sdf = null;
		
		if(year!=fileYear){
			sdf = new SimpleDateFormat("MMM dd  yyyy",Locale.US);
		}else{
			sdf = new SimpleDateFormat("MMM dd HH:mm",Locale.US);
		}
		String resultStr = sdf.format(date);
		
		
		
	return resultStr;	
	}
	
	
	/**
	 * 得到指定路径的文件大小
	 * @param filePath
	 * @return
	 */
	public static long getSingleFileSize(String filePath){
		File file = new File(filePath);
		if(!file.exists()){
			return 0;
		}
		if(file.isDirectory()){
			return 0;
		}
		return file.length();
	}
	/**
	 * 创建文件夹
	 * @param dirPath 新文件夹位置
	 * @return true 创建成功,false 创建失败
	 */
	public static boolean createNewDir(String dirPath){
		File newDir = new File(dirPath);
		return newDir.mkdir();
	}
	/**
	 * 得到需要重命名的文件
	 * @param filePath
	 * @return null 表示此文件不存在,否则返回此文件的File对象
	 */
	public static File fileToRename(String filePath){
		File renameFile = new File(filePath);
		if(renameFile.exists()){
			return renameFile;
		}else{
			return null;
		}
	}
	/**
	 * 重命名文件
	 * @param file  需要重命名的文件
	 * @param newFilePath 新文件的路径,等于文件所在路径加上新文件名字
	 * @return true表示重命名成功,false表示重命名失败
	 */
	public static boolean renameFile(File file,String newFilePath){
		return file.renameTo(new File(newFilePath));
	}
	/**
	 * 删除单个文件或空文件夹
	 * @param filePath
	 * @return
	 */
	public static boolean deleteFile(String filePath){
		return new File(filePath).delete();
	}
	/**
	 * 根据当前路径得到上一级路径
	 * @param dirPath 当前路径
	 * @return 上一级路径
	 */
	public static String preDirFromPath(String dirPath){
		String path = dirPath.substring(0, dirPath.length()-1);
		path  = path.substring(0, path.lastIndexOf("/")+1);
		return path;
	}
	
	
	
}
