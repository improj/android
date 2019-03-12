package com.yzxIM.tools;

import java.io.File;
import com.yzxIM.data.db.DBManager;
import com.yzxtcp.tools.FileTools;
import android.content.Context;
import android.os.Environment;
/**
 * 缓存管理（数据库，cache，SharedPreferences）
 * 
 * @author zhuqian
 */
public class CacheManager {
	//SharedPreferences文件夹路径
	private final String SPF_DIR;
	//数据库文件夹路径
	private final String DATA_DIR;
	//数据库文件夹路径
	private final String YUNZHIXUN_DIR;
	
	private static CacheManager instance;
	
	private Context context;
	
	
	public static CacheManager obtain(Context context){
		if(instance == null){
			synchronized (CacheManager.class) {
				if(instance == null){
					instance = new CacheManager(context);
				}
			}
		}
		return instance;
	}
	private CacheManager(Context context){
		SPF_DIR = "/data/data/"+context.getPackageName()+"/shared_prefs/";
		DATA_DIR = "/data/data/"+context.getPackageName()+"/databases/";
		YUNZHIXUN_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()+"/yunzhixun";
		this.context = context;
	}
	/**
	 * 删除shared_prefs目录下所有IM相关的SharedPreferences
	 */
	public void deleteIMSPF(){
		File spfDir = new File(SPF_DIR);
		if(!spfDir.exists()){
			return;
		}
		//遍历所有shared_prefs文件，找到IM的文件
		File[] spfFiles = spfDir.listFiles();
		for(File file : spfFiles){
			//删除所有以yzx开头的文件
			if(file.getName().startsWith("yzx")){
				FileTools.deleteFile(file);
			}
		}
	}
	/**
	 * 删除databases目录下所有IM相关的数据库
	 */
	public void deleteIMDatabases(){
		//关闭数据库
		DBManager.getInstance().closeDb();
		File databaseDir = new File(DATA_DIR);
		if(!databaseDir.exists()){
			return;
		}
		//遍历所有shared_prefs文件，找到IM的文件
		File[] databaseFiles = databaseDir.listFiles();
		for(File file : databaseFiles){
			//删除所有以YZXIM_开头的数据库
			if(file.getName().startsWith("YZXIM_")){
				this.context.deleteDatabase(file.getName());
			}
		}
	}
	/**
	 * 删除sd卡的/yunzhixun下面的文件（Log除外）
	 */
	public void deleteSDCardCache(){
		File yunzhixunDir = new File(YUNZHIXUN_DIR);
		if(!yunzhixunDir.exists()){
			return;
		}
		File[] yunzhixunFiles = yunzhixunDir.listFiles();
		for(File file : yunzhixunFiles){
			//删除除/crash，log文件夹下面的文件
			if(file.isDirectory() && (file.getName().equals("crash")
					|| file.getName().equals("log"))){
				continue;
			}
			FileTools.deleteFile(file);
		}
	}
}
