package com.bjxapp.worker.logic;

import java.util.ArrayList;

import com.bjxapp.worker.model.ImageInfo;
import com.bjxapp.worker.utils.diskcache.DiskCacheManager.DataType;

/**
 * 订单图片功能逻辑
 * @author jason
 */
public interface IUploadImagesLogic {
	/**
	 * 删除服务器图片
	 */
	public Boolean deleteUploadedImages(String dir, ArrayList<String> data);
	
	/**
	 * 删除服务器图片
	 */
	public Boolean deleteLocalImages(DataType dataType, ArrayList<String> data);
	
	/**
	 * 上传图片到服务器
	 */
	public Boolean uploadImages(String uploadUrl, String dir, ArrayList<ImageInfo> images, ArrayList<String> uploadedFilenames);

}
