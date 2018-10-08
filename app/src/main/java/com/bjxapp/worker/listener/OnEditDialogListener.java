package com.bjxapp.worker.listener;

public interface OnEditDialogListener {
	/**
	 * click ok button
	 * @param value
	 */
    void onPositive(String value);
    
	/**
	 * click cancel button
	 * @param value
	 */
    void onNagative();
}
