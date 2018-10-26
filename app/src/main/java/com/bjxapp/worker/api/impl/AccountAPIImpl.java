package com.bjxapp.worker.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;

import com.bjxapp.worker.api.APIConstants;
import com.bjxapp.worker.api.APIUtils;
import com.bjxapp.worker.api.IAccountAPI;
import com.bjxapp.worker.global.ConfigManager;
import com.bjxapp.worker.model.Account;
import com.bjxapp.worker.model.BankInfo;
import com.bjxapp.worker.model.ServiceSubItem;
import com.bjxapp.worker.model.UserApplyInfo;
import com.bjxapp.worker.model.WithdrawInfo;
import com.bjxapp.worker.model.WithdrawList;
import com.bjxapp.worker.model.XResult;
import com.bjxapp.worker.utils.Utils;
import com.bjxapp.worker.utils.http.HttpUtils;

/**
 * 用户逻辑实现
 *
 * @author jason
 */
public class AccountAPIImpl implements IAccountAPI {
    private static AccountAPIImpl sInstance;
    private Context mContext;

    private AccountAPIImpl(Context context) {
        mContext = context.getApplicationContext();
    }

    public static IAccountAPI getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AccountAPIImpl(context);
        }

        return sInstance;
    }

    @Override
    public int sendAuth(String mobile, String loginKey, String validateCode) {
        Map<String, String> params = getParams(mContext);
        params.put("user_code", mobile);
        params.put("login_key", loginKey);
        params.put("validate_code", validateCode);

        String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_SENDAUTH_API, params);
        if (!Utils.isNotEmpty(result))
            return 0;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = 0;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
            }

            return resultCode;
        } catch (JSONException ignore) {
            return 0;
        }
    }

    @Override
    public String getLoginKey() {
        Map<String, String> params = getParams(mContext);

        String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_KEY_GET_API, params);
        if (!Utils.isNotEmpty(result))
            return "";

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = 0;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
            }

            String loginKey = "";
            if (resultCode == APIConstants.RESULT_CODE_SUCCESS) {
                loginKey = json.getString("login_key");
            }

            return loginKey;
        } catch (JSONException ignore) {
            return "";
        }
    }

    @Override
    public Bitmap getVerifyCode(String loginKey) {
        Map<String, String> params = getParams(mContext);
        params.put("login_key", loginKey);
        Bitmap result = HttpUtils.getBitmapByGet(APIConstants.ACCOUNT_VERIFY_CODE_GET_API, params);
        return result;
    }

    @Override
    public XResult login(Account account) {
        Map<String, String> params = getParams(mContext);
        params.put("user_code", account.getMobile());
        params.put("auth_code", account.getAuthCode());

        String mobile = account.getMobile();

        String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_LOGIN_API, params);
        if (!Utils.isNotEmpty(result))
            return null;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = 0;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
            }

            String session = "";
            if (!json.isNull("session")) {
                session = json.getString("session");
            }

            int status = -1;
            if (!json.isNull("status")) {
                status = json.getInt("status");
            }

            account = new Account();
            account.setMobile(mobile);
            account.setSession(session);
            account.setStatus(status);

            XResult xResult = new XResult();
            xResult.setResultCode(resultCode);
            xResult.setDataObject(account);

            return xResult;

        } catch (JSONException ignore) {
            return null;
        }
    }

    @Override
    public int updateChannel() {
        Map<String, String> params = getParams(mContext);
        params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
        params.put("session", ConfigManager.getInstance(mContext).getUserSession());
        params.put("push_id", ConfigManager.getInstance(mContext).getUserChannelID());

        String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_CHANNEL_API, params);
        if (!Utils.isNotEmpty(result))
            return 0;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = 0;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
            }

            return resultCode;
        } catch (JSONException ignore) {
            return 0;
        }
    }


    @Override
    public int getRegisterStatus() {
        Map<String, String> params = getParams(mContext);
        params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
        params.put("session", ConfigManager.getInstance(mContext).getUserSession());

        String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_STATUS_API, params);
        if (!Utils.isNotEmpty(result))
            return -2;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = -2;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                int code = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
                if (code == APIConstants.RESULT_CODE_SUCCESS) {
                    if (!json.isNull("status")) {
                        resultCode = json.getInt("status");
                    }
                }
            }

            return resultCode;
        } catch (JSONException ignore) {
            return -2;
        }
    }

    @Override
    public List<ServiceSubItem> getServiceSubItems() {
        String userCode = ConfigManager.getInstance(mContext).getUserCode();
        String session = ConfigManager.getInstance(mContext).getUserSession();

        Map<String, String> params = getParams(mContext);
        params.put("user_code", userCode);
        params.put("session", session);

        String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_SERVICE_SUB_GET_API, params);
        if (!Utils.isNotEmpty(result))
            return null;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = 0;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
                if (resultCode != APIConstants.RESULT_CODE_SUCCESS)
                    return null;
            }

            if (json.isNull("service_sub")) {
                return null;
            }

            JSONArray itemsJsonArray = json.getJSONArray("service_sub");
            List<ServiceSubItem> items = new ArrayList<ServiceSubItem>(itemsJsonArray.length());
            for (int i = 0; i < itemsJsonArray.length(); i++) {
                JSONObject itemJson = (JSONObject) itemsJsonArray.get(i);

                ServiceSubItem item = new ServiceSubItem();

                if (itemJson.isNull("service_sub_id"))
                    continue;
                else
                    item.setId(itemJson.getInt("service_sub_id"));

                if (itemJson.isNull("service_sub_name"))
                    item.setName("");
                else
                    item.setName(itemJson.getString("service_sub_name"));

                items.add(item);
            }

            return items;

        } catch (JSONException ignore) {
            return null;
        }

    }

    @Override
    public int saveRegisterInfo(UserApplyInfo applyInfo) {
        Map<String, String> params = getParams(mContext);
        params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
        params.put("session", ConfigManager.getInstance(mContext).getUserSession());

        params.put("head", applyInfo.getHeadImageUrl());
        params.put("person_name", applyInfo.getPersonName());
        params.put("card_no", applyInfo.getCardNo());
        params.put("card_front", applyInfo.getCardFrontImageUrl());
        params.put("card_behind", applyInfo.getCardBehindImageUrl());
        params.put("rank", applyInfo.getRank());
        params.put("work_year", String.valueOf(applyInfo.getWorkYear()));
        params.put("province", "某省");
        params.put("city", applyInfo.getCity());
        params.put("country", "某区县");
        params.put("address", applyInfo.getAddress());
        params.put("latitude", String.valueOf(applyInfo.getLatitude()));
        params.put("longitude", String.valueOf(applyInfo.getLongitude()));
        params.put("service_sub_ids", applyInfo.getServiceSubIDs());

        String result = HttpUtils.getStrByPost(APIConstants.ACCOUNT_REGISTER_SAVE_API, params);
        if (!Utils.isNotEmpty(result))
            return 0;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = 0;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
            }

            return resultCode;
        } catch (JSONException ignore) {
            return 0;
        }
    }

    @Override
    public UserApplyInfo getRegisterInfo() {
        String userCode = ConfigManager.getInstance(mContext).getUserCode();
        String session = ConfigManager.getInstance(mContext).getUserSession();

        Map<String, String> params = getParams(mContext);
        params.put("user_code", userCode);
        params.put("session", session);

        String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_REGISTER_GET_API, params);
        if (!Utils.isNotEmpty(result))
            return null;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = 0;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
                if (resultCode != APIConstants.RESULT_CODE_SUCCESS)
                    return null;
            }

            if (json.isNull("user_detail")) {
                return null;
            }

            JSONObject applyJson = json.getJSONObject("user_detail");
            UserApplyInfo applyInfo = new UserApplyInfo();

            if (applyJson.isNull("user_code"))
                return null;
            else
                applyInfo.setUserCode(applyJson.getString("user_code"));

            if (applyJson.isNull("person_name"))
                applyInfo.setPersonName("");
            else
                applyInfo.setPersonName(applyJson.getString("person_name"));

            if (applyJson.isNull("head"))
                applyInfo.setHeadImageUrl("");
            else
                applyInfo.setHeadImageUrl(applyJson.getString("head"));

            if (applyJson.isNull("rank"))
                applyInfo.setRank("");
            else
                applyInfo.setRank(applyJson.getString("rank"));

            if (applyJson.isNull("status"))
                applyInfo.setStatus(-1);
            else
                applyInfo.setStatus(applyJson.getInt("status"));

            if (applyJson.isNull("work_year"))
                applyInfo.setWorkYear(1);
            else
                applyInfo.setWorkYear(applyJson.getInt("work_year"));

            if (applyJson.isNull("money_order"))
                applyInfo.setMoneyOrder("");
            else
                applyInfo.setMoneyOrder(applyJson.getString("money_order"));

            if (applyJson.isNull("order_count"))
                applyInfo.setOrderCount("");
            else
                applyInfo.setOrderCount(applyJson.getString("order_count"));

            if (applyJson.isNull("total_money"))
                applyInfo.setTotalMoney("");
            else
                applyInfo.setTotalMoney(applyJson.getString("total_money"));

            if (applyJson.isNull("service_sub_ids"))
                applyInfo.setServiceSubIDs("");
            else
                applyInfo.setServiceSubIDs(applyJson.getString("service_sub_ids"));

            if (applyJson.isNull("service_sub_names"))
                applyInfo.setServiceSubNames("");
            else
                applyInfo.setServiceSubNames(applyJson.getString("service_sub_names"));

            if (applyJson.isNull("card_no"))
                applyInfo.setCardNo("");
            else
                applyInfo.setCardNo(applyJson.getString("card_no"));

            if (applyJson.isNull("card_front"))
                applyInfo.setCardFrontImageUrl("");
            else
                applyInfo.setCardFrontImageUrl(applyJson.getString("card_front"));

            if (applyJson.isNull("card_behind"))
                applyInfo.setCardBehindImageUrl("");
            else
                applyInfo.setCardBehindImageUrl(applyJson.getString("card_behind"));

            if (applyJson.isNull("city"))
                applyInfo.setCity("");
            else
                applyInfo.setCity(applyJson.getString("city"));

            if (applyJson.isNull("address"))
                applyInfo.setAddress("");
            else
                applyInfo.setAddress(applyJson.getString("address"));

            if (applyJson.isNull("latitude"))
                applyInfo.setLatitude(0.0);
            else
                applyInfo.setLatitude(applyJson.getDouble("latitude"));

            if (applyJson.isNull("longitude"))
                applyInfo.setLongitude(0.0);
            else
                applyInfo.setLongitude(applyJson.getDouble("longitude"));

            if (applyJson.isNull("balance_money"))
                applyInfo.setBalanceMoney("0.0");
            else
                applyInfo.setBalanceMoney(applyJson.getString("balance_money"));

            if (applyJson.isNull("cash_money"))
                applyInfo.setCashMoney("0.0");
            else
                applyInfo.setCashMoney(applyJson.getString("cash_money"));

            return applyInfo;

        } catch (JSONException ignore) {
            return null;
        }
    }

    @Override
    public int getBalanceBankStatus() {
        Map<String, String> params = getParams(mContext);
        params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
        params.put("session", ConfigManager.getInstance(mContext).getUserSession());

        String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_BALANCE_BANK_EXISTS, params);
        if (!Utils.isNotEmpty(result))
            return -1;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = -1;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                int code = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
                if (code == APIConstants.RESULT_CODE_SUCCESS) {
                    if (!json.isNull("bank_exists")) {
                        resultCode = json.getInt("bank_exists");
                    }
                }
            }

            return resultCode;
        } catch (JSONException ignore) {
            return -1;
        }
    }

    @Override
    public int saveBalanceBankInfomation(BankInfo bankInfo) {
        Map<String, String> params = getParams(mContext);
        params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
        params.put("session", ConfigManager.getInstance(mContext).getUserSession());

        params.put("bank_account", bankInfo.getCard());
        params.put("bank_name", bankInfo.getName());
        params.put("account_name", bankInfo.getPerson());
        params.put("phone", bankInfo.getMobile());

        String result = HttpUtils.getStrByPost(APIConstants.ACCOUNT_BALANCE_BANK_SAVE, params);
        if (!Utils.isNotEmpty(result))
            return 0;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = 0;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
            }

            return resultCode;
        } catch (JSONException ignore) {
            return 0;
        }
    }

    @Override
    public BankInfo getBalanceBankInfomation() {
        String userCode = ConfigManager.getInstance(mContext).getUserCode();
        String session = ConfigManager.getInstance(mContext).getUserSession();

        Map<String, String> params = getParams(mContext);
        params.put("user_code", userCode);
        params.put("session", session);

        String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_BALANCE_BANK_GET, params);
        if (!Utils.isNotEmpty(result))
            return null;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = 0;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
                if (resultCode != APIConstants.RESULT_CODE_SUCCESS)
                    return null;
            }

            if (json.isNull("user_bank")) {
                return null;
            }

            JSONObject bankJson = json.getJSONObject("user_bank");
            BankInfo bankInfo = new BankInfo();

            if (bankJson.isNull("bank_account"))
                return null;
            else
                bankInfo.setCard(bankJson.getString("bank_account"));

            if (bankJson.isNull("bank_name"))
                return null;
            else
                bankInfo.setName(bankJson.getString("bank_name"));

            if (bankJson.isNull("account_name"))
                return null;
            else
                bankInfo.setPerson(bankJson.getString("account_name"));

            if (bankJson.isNull("phone"))
                return null;
            else
                bankInfo.setMobile(bankJson.getString("phone"));

            if (bankJson.isNull("balance_money"))
                bankInfo.setBalanceMoney(0.0);
            else
                bankInfo.setBalanceMoney(bankJson.getDouble("balance_money"));

            if (bankJson.isNull("cash_money"))
                bankInfo.setCashMoney(0.0);
            else
                bankInfo.setCashMoney(bankJson.getDouble("cash_money"));

            if (json.isNull("cash_rule")) {
                return null;
            }

            JSONObject cashJson = json.getJSONObject("cash_rule");

            if (cashJson.isNull("quality_day"))
                bankInfo.setPledgeDays(0);
            else
                bankInfo.setPledgeDays(cashJson.getInt("quality_day"));

            if (cashJson.isNull("cash_start_1"))
                bankInfo.setCashStart1(0);
            else
                bankInfo.setCashStart1(cashJson.getInt("cash_start_1"));

            if (cashJson.isNull("cash_end_1"))
                bankInfo.setCashEnd1(0);
            else
                bankInfo.setCashEnd1(cashJson.getInt("cash_end_1"));

            if (cashJson.isNull("cash_start_2"))
                bankInfo.setCashStart2(0);
            else
                bankInfo.setCashStart2(cashJson.getInt("cash_start_2"));

            if (cashJson.isNull("cash_end_2"))
                bankInfo.setCashEnd2(0);
            else
                bankInfo.setCashEnd2(cashJson.getInt("cash_end_2"));

            if (cashJson.isNull("cash_start_3"))
                bankInfo.setCashStart3(0);
            else
                bankInfo.setCashStart3(cashJson.getInt("cash_start_3"));

            if (cashJson.isNull("cash_end_3"))
                bankInfo.setCashEnd3(0);
            else
                bankInfo.setCashEnd3(cashJson.getInt("cash_end_3"));

            return bankInfo;

        } catch (JSONException ignore) {
            return null;
        }
    }

    @Override
    public int getWithdrawAllowStatus() {
        Map<String, String> params = getParams(mContext);
        params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
        params.put("session", ConfigManager.getInstance(mContext).getUserSession());

        String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_BALANCE_WITHDRAW_ALLOW, params);
        if (!Utils.isNotEmpty(result))
            return -1;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = -1;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                int code = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
                if (code == APIConstants.RESULT_CODE_SUCCESS) {
                    if (!json.isNull("allow_cash")) {
                        resultCode = json.getInt("allow_cash");
                    }
                }
            }

            return resultCode;
        } catch (JSONException ignore) {
            return -1;
        }
    }

    @Override
    public int saveWithdrawCashMoney(String cashMoney) {
        Map<String, String> params = getParams(mContext);
        params.put("user_code", ConfigManager.getInstance(mContext).getUserCode());
        params.put("session", ConfigManager.getInstance(mContext).getUserSession());

        params.put("cash_money", cashMoney);

        String result = HttpUtils.getStrByPost(APIConstants.ACCOUNT_BALANCE_WITHDRAW, params);
        if (!Utils.isNotEmpty(result))
            return 0;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = 0;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
            }

            return resultCode;
        } catch (JSONException ignore) {
            return 0;
        }
    }

    @Override
    public WithdrawList getWithdrawList(int batch, int count) {
        String userCode = ConfigManager.getInstance(mContext).getUserCode();
        String session = ConfigManager.getInstance(mContext).getUserSession();

        Map<String, String> params = getParams(mContext);
        params.put("user_code", userCode);
        params.put("session", session);
        params.put("page_index", String.valueOf(batch));
        params.put("page_size", String.valueOf(count));

        String result = HttpUtils.getStringByGet(APIConstants.ACCOUNT_BALANCE_WITHDRAW_LIST, params);
        if (!Utils.isNotEmpty(result))
            return null;

        try {
            JSONObject json = new JSONObject(result);

            int resultCode = 0;
            if (!json.isNull(APIConstants.JSON_RESULT_CODE_KEY)) {
                resultCode = json.getInt(APIConstants.JSON_RESULT_CODE_KEY);
                if (resultCode != APIConstants.RESULT_CODE_SUCCESS)
                    return null;
            }

            String totalMoney = "0";
            if (!json.isNull("total_cash")) {
                totalMoney = json.getString("total_cash");
            }

            if (json.isNull("cash")) {
                return null;
            }

            JSONArray itemsJsonArray = json.getJSONArray("cash");
            List<WithdrawInfo> items = new ArrayList<WithdrawInfo>(itemsJsonArray.length());
            for (int i = 0; i < itemsJsonArray.length(); i++) {
                JSONObject itemJson = (JSONObject) itemsJsonArray.get(i);

                WithdrawInfo item = new WithdrawInfo();

                if (itemJson.isNull("cash_date"))
                    item.setDate("");
                else
                    item.setDate(itemJson.getString("cash_date"));

                if (itemJson.isNull("cash_money"))
                    item.setMoney("0.0");
                else
                    item.setMoney(itemJson.getString("cash_money"));

                if (itemJson.isNull("status"))
                    item.setStatus(0);
                else
                    item.setStatus(itemJson.getInt("status"));

                items.add(item);
            }

            WithdrawList withdrawList = new WithdrawList();
            withdrawList.setTotalMoney(totalMoney);
            withdrawList.setDataObject(items);
            return withdrawList;

        } catch (JSONException ignore) {
            return null;
        }
    }

    /**
     * 组装请求参数
     *
     * @param context
     * @return 请求参数键值对
     */
    private static Map<String, String> getParams(Context context) {
        Map<String, String> params = APIUtils.getBasicParams(context);
        return params;
    }

}
