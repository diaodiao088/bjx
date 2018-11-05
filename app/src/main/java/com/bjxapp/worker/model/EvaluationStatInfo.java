package com.bjxapp.worker.model;

import java.util.ArrayList;

/**
 * Created by zhangdan on 2018/11/5.
 * comments:
 */

public class EvaluationStatInfo {

    private float appearanceLevel;

    private float attitudeLevel;

    private float skillLevel;

    private ArrayList<LabelStat> mLabelList = new ArrayList<>();

    public float getAppearanceLevel() {
        return appearanceLevel;
    }

    public void setAppearanceLevel(float appearanceLevel) {
        this.appearanceLevel = appearanceLevel;
    }

    public float getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(float skillLevel) {
        this.skillLevel = skillLevel;
    }

    public float getAttitudeLevel() {
        return attitudeLevel;
    }

    public void setAttitudeLevel(float attitudeLevel) {
        this.attitudeLevel = attitudeLevel;
    }

    public ArrayList<LabelStat> getmLabelList() {
        return mLabelList;
    }

    public void setmLabelList(ArrayList<LabelStat> mLabelList) {
        this.mLabelList = mLabelList;
    }
}
