package com.bjxapp.worker.api;

import java.util.List;

import com.bjxapp.worker.model.Account;
import com.bjxapp.worker.model.BankInfo;
import com.bjxapp.worker.model.ServiceSubItem;
import com.bjxapp.worker.model.UserApplyInfo;
import com.bjxapp.worker.model.WithdrawList;
import com.bjxapp.worker.model.XResult;

import android.graphics.Bitmap;

/**
 * 消息API
 * @author jason
 */
public interface IAccountAPI {
	/**
	 * 帐号登录
	 */
	public int sendAuth(String mobile, String loginKey, String validateCode);
	public String getLoginKey();
	public Bitmap getVerifyCode(String loginKey);
	
	/**
	 * 帐号登录
	 */
	public XResult login(Account account);
	
	/**
	 * 更新推送使用的channelid
	 */
	public int updateChannel();
	
	/**
	 * 获取工人注册的状态
	 */
	public int getRegisterStatus();
	
	/**
	 * 获取所有服务子项目
	 */
	public List<ServiceSubItem> getServiceSubItems();
	
	/**
	 * 保存工人注册信息
	 */
	public int saveRegisterInfo(UserApplyInfo applyInfo);
	
	/**
	 * 查询工人注册信息
	 */
	public UserApplyInfo getRegisterInfo();
	
	/**
	 * 获取银行卡是否存在
	 */
	public int getBalanceBankStatus();
	
	/**
	 * 保存银行卡信息
	 */
	public int saveBalanceBankInfomation(BankInfo bankInfo);
	
	/**
	 * 获取银行卡提现信息
	 */
	public BankInfo getBalanceBankInfomation();
	
	/**
	 * 获取是否允许提现
	 */
	public int getWithdrawAllowStatus();
	
	/**
	 * 保存提现信息
	 */
	public int saveWithdrawCashMoney(String cashMoney);
	
	/**
	 * 获取提现列表
	 */
	public WithdrawList getWithdrawList(int batch, int count);
	
}
