package com.bjxapp.worker.api;

/**
 * 上传imagesAPI
 * @author jason
 */
public interface IUploadImagesAPI {
	/**
	 * 删除服务器图片
	 */
	public Boolean deleteUploadedImages(String dir, String data);

}
