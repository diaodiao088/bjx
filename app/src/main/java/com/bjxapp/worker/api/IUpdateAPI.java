package com.bjxapp.worker.api;

import com.bjxapp.worker.model.UpdateInfo;

/**
 * 更新最新的APK包API
 * @author jason
 */
public interface IUpdateAPI {
	/**
	 * 获取APK更新信息
	 */
	public UpdateInfo getUpdateInfo();
}
