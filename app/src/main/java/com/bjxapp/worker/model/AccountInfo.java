package com.bjxapp.worker.model;

/**
 * Created by zhangdan on 2018/11/5.
 * comments:
 */

public class AccountInfo {

    private float balanceAmount; // 账户余额

    private float canWithdrawalAmount; //可提现余额

    private int incomeRank; //收入排名

    private int orderQuantity; // 订单数量

    private float totalIncome; // 累计收入

    private float totalOrderAmount; //累计订单金额

    private float withdrawnAmount; // 已提现金额

    public AccountInfo(float balanceAmount, float canWithdrawalAmount, int incomeRank, int orderQuantity, float totalIncome, float totalOrderAmount, float withdrawnAmount) {
        this.balanceAmount = balanceAmount;
        this.canWithdrawalAmount = canWithdrawalAmount;
        this.incomeRank = incomeRank;
        this.orderQuantity = orderQuantity;
        this.totalIncome = totalIncome;
        this.totalOrderAmount = totalOrderAmount;
        this.withdrawnAmount = withdrawnAmount;
    }

    public float getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(float balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public float getCanWithdrawalAmount() {
        return canWithdrawalAmount;
    }

    public void setCanWithdrawalAmount(float canWithdrawalAmount) {
        this.canWithdrawalAmount = canWithdrawalAmount;
    }

    public int getIncomeRank() {
        return incomeRank;
    }

    public void setIncomeRank(int incomeRank) {
        this.incomeRank = incomeRank;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public float getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(float totalIncome) {
        this.totalIncome = totalIncome;
    }

    public float getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(float totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }

    public float getWithdrawnAmount() {
        return withdrawnAmount;
    }

    public void setWithdrawnAmount(float withdrawnAmount) {
        this.withdrawnAmount = withdrawnAmount;
    }
}
