package com.kj.androidftpserver.util;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IpUtils {
	/**
     * Get host IP address
     *
     * @return IP Address
     */
	public static InetAddress getAddress() {
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();) {
                NetworkInterface networkInterface = interfaces.nextElement();
                try{
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp() || networkInterface.getName().contains("vmnet")) {
                    continue;
                }
                }catch(Exception e){
                	continue;
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                	InetAddress address = addresses.nextElement();
                	
                	if(address instanceof Inet4Address){
                    return address;
                	}
                	
                }
            }
        } catch (SocketException e) {
        	System.out.println("获取IP失败");
        }
        return null;
    }
  
}
