package com.bjxapp.worker.logic.impl;

import java.util.ArrayList;

import android.content.Context;

import com.bjxapp.worker.global.Constant;
import com.bjxapp.worker.logic.IUploadImagesLogic;
import com.bjxapp.worker.model.ImageInfo;
import com.bjxapp.worker.utils.FileUtils;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;
import com.bjxapp.worker.utils.image.PictureUploadUtils;

/**
 * 消息逻辑实现
 * @author jason
 */
public class UploadImagesLogicImpl implements IUploadImagesLogic {
	private static UploadImagesLogicImpl sInstance;
	private Context mContext;
	
	private UploadImagesLogicImpl(Context context) {
		mContext = context.getApplicationContext();
	}
	
	public static IUploadImagesLogic getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new UploadImagesLogicImpl(context);
		}
		
		return sInstance;
	}

	@Override
	public Boolean deleteUploadedImages(String dir, ArrayList<String> data) {
		return true;
		
		/*
		if(dir == null || data == null || data.size() == 0) return true;
		
		String fileNames = "";
		for(int i=0;i<data.size();i++){
			fileNames = fileNames + data.get(i) + ",";
		}
		fileNames = fileNames.substring(0, fileNames.length() - 1);
		
		return APIFactory.getUploadImagesAPI(mContext).deleteUploadedImages(dir, fileNames);
		*/
	}
	
	@Override
	public Boolean deleteLocalImages(DataType datatype, ArrayList<String> data){
		try {
			for(int i=0;i<data.size();i++){
				DiskCacheManager.getInstance(mContext).deleteFile(datatype, data.get(i));
			}
			return true;
		} 
		catch (Exception e) {
			return false;
		}
	}

	@Override
	public Boolean uploadImages(String uploadUrl, String dir, ArrayList<ImageInfo> images, ArrayList<String> uploadedFilenames) {
		if(dir == null || images == null || images.size() == 0 || uploadedFilenames == null) return true;
		
		for(int i=0;i<images.size();i++){
			if(images.get(i).getFlag() != 0){
				uploadedFilenames.add(getUploadedFileName(images.get(i).getUrl()));
				continue;
			}
			
			String fileName = String.valueOf(System.currentTimeMillis());
			String result = PictureUploadUtils.uploadImage(uploadUrl,images.get(i).getUrl(), mContext, dir, fileName, Constant.UPLOAD_IMAGE_SIZE);
			
			if(Utils.isNotEmpty(result)){
				uploadedFilenames.add(result);
			}
			else{
				return false;
			}
    	}
		
		return true;
	}
	
    private String getUploadedFileName(String url){
    	return FileUtils.getImgNameWithImageExt(url);
    }
}
