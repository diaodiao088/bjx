package com.bjxapp.worker.logic;

import java.util.List;
import java.util.Map;

import com.bjxapp.worker.model.CommonConsult;

public interface IConsultLogic {
    public List<CommonConsult> getConsultData(int type, Map<String, String> params);
    public String getConsultTitle(int type);
}
