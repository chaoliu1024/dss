package com.artisan.dance.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Properties;

public class ToolUtil {
	
	/***
	 * 创建连接数据配置信息文件
	 * @param parentPath
	 */
   public static void createDBConfig(File parentPath,String ip){
	   Properties props = new Properties();
	   File file=new File(parentPath,"dbConfig.properties");
	   try{
	   OutputStream out=new FileOutputStream(file);						
		props.setProperty("ip",ip);
		props.store(out, "update");
		out.close();
	   }catch(Exception e){
		   e.printStackTrace();
	   }
   }
   
   /**
    * 移除IP
    * @param parentPath
    */
   public static void removeDBConfig(File parentPath){
	   Properties props = new Properties();
	   File file=new File(parentPath,"dbConfig.properties");
	   try{
	   OutputStream out=new FileOutputStream(file);						
		props.remove("ip");
		props.store(out, "update");
		out.close();
	   }catch(Exception e){
		   e.printStackTrace();
	   }  
   }
   
   /***
    * 获得数据库连接信息
    * @param parentPath
    * @return
    */
   public static HashMap<String,Object> getDBConfig(File parentPath){
	   Properties props = new Properties();
	   File file=new File(parentPath,"dbConfig.properties");
	   HashMap<String,Object> one=new HashMap<String,Object>();
	   try{
	   InputStream in=new FileInputStream(file);
		props.load(in);
		String ip=props.getProperty("ip");
		one.put("ip",ip);
	   }catch(Exception e){
		   e.printStackTrace();
	   }
	   return one;
   }  
   
}
