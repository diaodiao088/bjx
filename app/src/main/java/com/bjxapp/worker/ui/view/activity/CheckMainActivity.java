package com.bjxapp.worker.ui.view.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.bjx.master.R;
import com.bjxapp.worker.adapter.check_info.Article;
import com.bjxapp.worker.adapter.check_info.ArticleAdapter;
import com.bjxapp.worker.controls.XTextView;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.group.GroupItemDecoration;
import com.haibin.calendarview.group.GroupRecyclerView;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CheckMainActivity extends Activity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener,
        View.OnClickListener {

    @BindView(R.id.calendarView)
    CalendarView mCalendarView;

    @BindView(R.id.title_text_tv)
    XTextView mTitleTextView;

    @BindView(R.id.calendarLayout)
    CalendarLayout mCalendarLayout;

    @BindView(R.id.recyclerView)
    GroupRecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_main_activity);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void initView() {

        mCalendarLayout = (CalendarLayout) findViewById(R.id.calendarLayout);
        mCalendarView.setOnYearChangeListener(this);
        mCalendarView.setOnCalendarSelectListener(this);

        mTitleTextView.setText(mCalendarView.getCurYear() + "年" + mCalendarView.getCurMonth() + "月");

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new GroupItemDecoration<String, Article>());
        mRecyclerView.setAdapter(new ArticleAdapter(this));
        mRecyclerView.notifyDataSetChanged();
    }

    private void initData() {
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();
        int day = mCalendarView.getCurDay();

        Map<String, Calendar> map = new HashMap<>();
        map.put(getSchemeCalendar(year, month, 3, 0xFF40db25, "假").toString(),
                getSchemeCalendar(year, month, 3, 0xFF40db25, "假"));
        map.put(getSchemeCalendar(year, month, 6, 0xFFe69138, "事").toString(),
                getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
        map.put(getSchemeCalendar(year, month, 9, 0xFFdf1356, "议").toString(),
                getSchemeCalendar(year, month, 9, 0xFFdf1356, "议"));
        map.put(getSchemeCalendar(year, month, 13, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 13, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, day, 0xff00a551, "记").toString(),
                getSchemeCalendar(year, month, day, 0xff00a551, "记"));
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onYearChange(int year) {

    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        mTitleTextView.setText(calendar.getYear() + "年" + calendar.getMonth() + "月");
    }
}
