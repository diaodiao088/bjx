package com.bjxapp.worker.logic.impl;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;

import com.bjxapp.worker.api.APIFactory;
import com.bjxapp.worker.logic.IAccountLogic;
import com.bjxapp.worker.model.Account;
import com.bjxapp.worker.model.BankInfo;
import com.bjxapp.worker.model.ServiceSubItem;
import com.bjxapp.worker.model.UserApplyInfo;
import com.bjxapp.worker.model.WithdrawList;
import com.bjxapp.worker.model.XResult;

/**
 * 帐号逻辑实现
 * @author jason
 */
public class AccountLogicImpl implements IAccountLogic {
	private static AccountLogicImpl sInstance;
	private Context mContext;
	
	private AccountLogicImpl(Context context) {
		mContext = context.getApplicationContext();
	}
	
	public static IAccountLogic getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new AccountLogicImpl(context);
		}
		
		return sInstance;
	}

	@Override
	public int sendAuth(String mobile,String loginKey,String validateCode) {
		return APIFactory.getAccountAPI(mContext).sendAuth(mobile,loginKey,validateCode);
	}
	
	@Override
	public String getLoginKey() {
		return APIFactory.getAccountAPI(mContext).getLoginKey();
	}

	@Override
	public Bitmap getVerifyCode(String loginKey) {
		return APIFactory.getAccountAPI(mContext).getVerifyCode(loginKey);
	}
	
	@Override
	public XResult login(Account account) {
		XResult xResult = APIFactory.getAccountAPI(mContext).login(account);
		return xResult;
	}
	
	@Override
	public int updateChannelID() {
		return APIFactory.getAccountAPI(mContext).updateChannel();
	}

	@Override
	public int getRegisterStatus() {
		return APIFactory.getAccountAPI(mContext).getRegisterStatus();
	}

	@Override
	public List<ServiceSubItem> getServiceSubItems() {
		return APIFactory.getAccountAPI(mContext).getServiceSubItems();
	}

	@Override
	public int saveRegisterInfo(UserApplyInfo applyInfo) {
		return APIFactory.getAccountAPI(mContext).saveRegisterInfo(applyInfo);
	}

	@Override
	public UserApplyInfo getRegisterInfo() {
		return APIFactory.getAccountAPI(mContext).getRegisterInfo();
	}

	@Override
	public int getBalanceBankStatus() {
		return APIFactory.getAccountAPI(mContext).getBalanceBankStatus();
	}

	@Override
	public int saveBalanceBankInfomation(BankInfo bankInfo) {
		return APIFactory.getAccountAPI(mContext).saveBalanceBankInfomation(bankInfo);
	}

	@Override
	public BankInfo getBalanceBankInfomation() {
		return APIFactory.getAccountAPI(mContext).getBalanceBankInfomation();
	}

	@Override
	public int getWithdrawAllowStatus() {
		return APIFactory.getAccountAPI(mContext).getWithdrawAllowStatus();
	}

	@Override
	public int saveWithdrawCashMoney(String cashMoney) {
		return APIFactory.getAccountAPI(mContext).saveWithdrawCashMoney(cashMoney);
	}

	@Override
	public WithdrawList getWithdrawList(int batch,int count) {
		return APIFactory.getAccountAPI(mContext).getWithdrawList(batch,count);
	}

}
