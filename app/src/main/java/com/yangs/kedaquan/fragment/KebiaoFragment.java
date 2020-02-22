package com.yangs.kedaquan.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;
import com.yangs.kedaquan.R;
import com.yangs.kedaquan.APPAplication;
import com.yangs.kedaquan.activity.KebiaoDetailActivity;
import com.yangs.kedaquan.utils.DateUtil;
import com.yangs.kedaquan.utils.FrescoImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


/**
 * Created by yangs on 2017/5/6.
 */

public class KebiaoFragment extends LazyLoadFragment implements Toolbar.OnMenuItemClickListener, View.OnClickListener {
    private Activity activity;
    private View mLayKebiao;
    private TextView kebiao_tv_start;
    private TextView kebiao_tv_monday;
    private TextView kebiao_tv_tuesday;
    private TextView kebiao_tv_wednesday;
    private TextView kebiao_tv_fourthday;
    private TextView kebiao_tv_friday;
    private TextView kebiao_tv_Staday;
    private TextView kebiao_tv_Sunday;
    private TextView kebiao_tv_monday_h;
    private TextView kebiao_tv_tuesday_h;
    private TextView kebiao_tv_wednesday_h;
    private TextView kebiao_tv_fourthday_h;
    private TextView kebiao_tv_friday_h;
    private TextView kebiao_tv_Staday_h;
    private TextView kebiao_tv_Sunday_h;
    private LinearLayout kebiao_header_ll_1;
    private LinearLayout kebiao_header_ll_2;
    private LinearLayout kebiao_header_ll_3;
    private LinearLayout kebiao_header_ll_4;
    private LinearLayout kebiao_header_ll_5;
    private LinearLayout kebiao_header_ll_6;
    private LinearLayout kebiao_header_ll_7;
    private int firstDayOfWeek;
    private TextView kebiao_extra;
    private LinearLayout kebiao_ll_extra;
    private LinearLayout[] linearLayout;
    private ArrayList<Integer> myImageList;
    private int heightPixels;
    private int widthPixels;
    private int week;
    private int kebiao_show_ct;
    public TextView toolbar_login;
    public TextView toolbar_time;
    private OnKebiaoRefreshListener onLoginListener;
    private LinearLayout kebiao_ll;

    @Override
    protected int setContentView() {
        return R.layout.kebiao_layout;
    }

    @Override
    protected void lazyLoad() {
        if (isInit) {
            if (!isLoad) {
                initView();
                if (APPAplication.login_stat != 0)
                    initKebiao();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.kebiao_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public interface OnKebiaoRefreshListener {
        void onKebiaoRefresh(Intent data);
    }

    private void initView() {
        activity = getActivity();
        mLayKebiao = getContentView();
        setHasOptionsMenu(true);
        kebiao_ll = mLayKebiao.findViewById(R.id.kebiao_ll);
        onLoginListener = (OnKebiaoRefreshListener) activity;
        Toolbar toolbar = mLayKebiao.findViewById(R.id.kebiao_toolbar);
        toolbar_login = mLayKebiao.findViewById(R.id.kebiao_toolbar_login);
        toolbar_time = mLayKebiao.findViewById(R.id.kebiao_toolbar_time);
        toolbar.setTitle("");
        toolbar_login.setText(APPAplication.save.getString("name", "未登录"));
        toolbar_time.setText("第" + APPAplication.week + "周 ▾");
        toolbar_time.setOnClickListener(this);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.inflateMenu(R.menu.kebiao_menu);
        toolbar.setOnMenuItemClickListener(this);
        week = APPAplication.week;
        kebiao_show_ct = APPAplication.kebiao_show_ct;
        if (new File(APPAplication.getPath() + "/background.jpg").exists()) {
            try {
                Drawable drawable = Drawable.createFromPath(APPAplication.getPath() + "/background.jpg");
                kebiao_ll.setBackground(drawable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        kebiao_tv_start = mLayKebiao.findViewById(R.id.textView_Start);
        kebiao_tv_monday = mLayKebiao.findViewById(R.id.textView_Monday);
        kebiao_tv_tuesday = mLayKebiao.findViewById(R.id.textView_Tuesday);
        kebiao_tv_wednesday = mLayKebiao.findViewById(R.id.textView_Wednesday);
        kebiao_tv_fourthday = mLayKebiao.findViewById(R.id.textView_Thursday);
        kebiao_tv_friday = mLayKebiao.findViewById(R.id.textView_Friday);
        kebiao_tv_Staday = mLayKebiao.findViewById(R.id.textView_Saturday);
        kebiao_tv_Sunday = mLayKebiao.findViewById(R.id.textView_Sunday);
        kebiao_tv_monday_h = mLayKebiao.findViewById(R.id.textView_Monday_h);
        kebiao_tv_tuesday_h = mLayKebiao.findViewById(R.id.textView_Tuesday_h);
        kebiao_tv_wednesday_h = mLayKebiao.findViewById(R.id.textView_Wednesday_h);
        kebiao_tv_fourthday_h = mLayKebiao.findViewById(R.id.textView_Thursday_h);
        kebiao_tv_friday_h = mLayKebiao.findViewById(R.id.textView_Friday_h);
        kebiao_tv_Staday_h = mLayKebiao.findViewById(R.id.textView_Saturday_h);
        kebiao_tv_Sunday_h = mLayKebiao.findViewById(R.id.textView_Sunday_h);
        kebiao_header_ll_1 = mLayKebiao.findViewById(R.id.kebiao_header_ll_one);
        kebiao_header_ll_2 = mLayKebiao.findViewById(R.id.kebiao_header_ll_two);
        kebiao_header_ll_3 = mLayKebiao.findViewById(R.id.kebiao_header_ll_three);
        kebiao_header_ll_4 = mLayKebiao.findViewById(R.id.kebiao_header_ll_four);
        kebiao_header_ll_5 = mLayKebiao.findViewById(R.id.kebiao_header_ll_five);
        kebiao_header_ll_6 = mLayKebiao.findViewById(R.id.kebiao_header_ll_six);
        kebiao_header_ll_7 = mLayKebiao.findViewById(R.id.kebiao_header_ll_seven);
        initKebiaoHeader(new Date(System.currentTimeMillis()));
        LinearLayout side_index_layout = mLayKebiao.findViewById(R.id.side_index_layout);
        kebiao_extra = mLayKebiao.findViewById(R.id.kebiao_tv_shixi);
        kebiao_ll_extra = mLayKebiao.findViewById(R.id.kebiao_ll_extra);
        linearLayout = new LinearLayout[7];
        linearLayout[0] = mLayKebiao.findViewById(R.id.linearLayout1);
        linearLayout[1] = mLayKebiao.findViewById(R.id.linearLayout2);
        linearLayout[2] = mLayKebiao.findViewById(R.id.linearLayout3);
        linearLayout[3] = mLayKebiao.findViewById(R.id.linearLayout4);
        linearLayout[4] = mLayKebiao.findViewById(R.id.linearLayout5);
        linearLayout[5] = mLayKebiao.findViewById(R.id.linearLayout6);
        linearLayout[6] = mLayKebiao.findViewById(R.id.linearLayout7);
        myImageList = new ArrayList<>();
        myImageList.add(R.drawable.textview_border_bohelv);
        myImageList.add(R.drawable.textview_border_cheng);
        myImageList.add(R.drawable.textview_border_lan);
        myImageList.add(R.drawable.textview_border_huang);
        myImageList.add(R.drawable.textview_border_lv);
        myImageList.add(R.drawable.textview_border_pulan);
        myImageList.add(R.drawable.textview_border_fen);
        myImageList.add(R.drawable.textview_border_cyan);
        myImageList.add(R.drawable.textview_border_zi);
        myImageList.add(R.drawable.textview_kafei);
        myImageList.add(R.drawable.texivew_border_qing);
        myImageList.add(R.drawable.textview_border_molan);
        myImageList.add(R.drawable.textview_border_tuhuang);
        myImageList.add(R.drawable.textview_border_tao);
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        heightPixels = dm.heightPixels;
        widthPixels = dm.widthPixels;
        for (int i = 1; i <= 5; i++) {
            TextView textView = new TextView(side_index_layout.getContext());
            textView.setTextColor(getResources().getColor(R.color.kebiao_bg_gray2));
            switch (i) {
                case 1:
                    textView.setText("08:00\n" + "第" + i + "节\n" + "09:40");
                    break;
                case 2:
                    textView.setText("10:00\n" + "第" + i + "节\n" + "11:40");
                    break;
                case 3:
                    textView.setText("14:00\n" + "第" + i + "节\n" + "15:40");
                    break;
                case 4:
                    textView.setText("16:00\n" + "第" + i + "节\n" + "17:40");
                    break;
                case 5:
                    textView.setText("19:00\n" + "第" + i + "节\n" + "20:40");
                    break;
            }
            textView.setTextSize(12f);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, heightPixels / 6);
            textView.setLayoutParams(params);
            side_index_layout.addView(textView);
        }
    }

    private void initKebiaoHeader(Date d) {
        Calendar cal2 = Calendar.getInstance();
        cal2.setTimeInMillis(DateUtil.getFirstDayOfWeek(d).getTime());
        int startMonth = cal2.get(Calendar.MONTH) + 1;
        Date td = DateUtil.getFirstDayOfWeek(d);
        kebiao_tv_start.setText(startMonth + "");
        kebiao_tv_start.setTextColor(Color.rgb(27, 124, 220));
        kebiao_tv_start.setGravity(Gravity.CENTER);
        kebiao_tv_monday.setText(DateUtil.geDayAfter(td, 0) + "");
        kebiao_tv_tuesday.setText(DateUtil.geDayAfter(td, 1) + "");
        kebiao_tv_wednesday.setText(DateUtil.geDayAfter(td, 2) + "");
        kebiao_tv_fourthday.setText(DateUtil.geDayAfter(td, 3) + "");
        kebiao_tv_friday.setText(DateUtil.geDayAfter(td, 4) + "");
        kebiao_tv_Staday.setText(DateUtil.geDayAfter(td, 5) + "");
        kebiao_tv_Sunday.setText(DateUtil.geDayAfter(td, 6) + "");
        kebiao_header_ll_1.setBackgroundResource(R.drawable.kebiao_header_unselect);
        kebiao_header_ll_2.setBackgroundResource(R.drawable.kebiao_header_unselect);
        kebiao_header_ll_3.setBackgroundResource(R.drawable.kebiao_header_unselect);
        kebiao_header_ll_4.setBackgroundResource(R.drawable.kebiao_header_unselect);
        kebiao_header_ll_5.setBackgroundResource(R.drawable.kebiao_header_unselect);
        kebiao_header_ll_6.setBackgroundResource(R.drawable.kebiao_header_unselect);
        kebiao_header_ll_7.setBackgroundResource(R.drawable.kebiao_header_unselect);
        kebiao_tv_monday_h.setTextColor(getResources().getColor(R.color.global_blue));
        kebiao_tv_tuesday_h.setTextColor(getResources().getColor(R.color.global_blue));
        kebiao_tv_wednesday_h.setTextColor(getResources().getColor(R.color.global_blue));
        kebiao_tv_fourthday_h.setTextColor(getResources().getColor(R.color.global_blue));
        kebiao_tv_friday_h.setTextColor(getResources().getColor(R.color.global_blue));
        kebiao_tv_Staday_h.setTextColor(getResources().getColor(R.color.global_blue));
        kebiao_tv_Sunday_h.setTextColor(getResources().getColor(R.color.global_blue));
        kebiao_tv_Sunday.setTextColor(getResources().getColor(R.color.kebiao_bg_gray2));
        kebiao_tv_monday.setTextColor(getResources().getColor(R.color.kebiao_bg_gray2));
        kebiao_tv_tuesday.setTextColor(getResources().getColor(R.color.kebiao_bg_gray2));
        kebiao_tv_wednesday.setTextColor(getResources().getColor(R.color.kebiao_bg_gray2));
        kebiao_tv_friday.setTextColor(getResources().getColor(R.color.kebiao_bg_gray2));
        kebiao_tv_Staday.setTextColor(getResources().getColor(R.color.kebiao_bg_gray2));
        if (DateUtil.geDayAfter(d, 0) != DateUtil.geDayAfter(new Date(System.currentTimeMillis()), 0))
            return;
        int w = DateUtil.getDayofWeek(d);
        switch (w) {
            case 0:
                kebiao_header_ll_7.setBackgroundResource(R.drawable.kebiao_header_select);
                kebiao_tv_Sunday_h.setTextColor(getResources().getColor(R.color.white));
                kebiao_tv_Sunday.setTextColor(getResources().getColor(R.color.white));
                break;
            case 1:
                kebiao_header_ll_1.setBackgroundResource(R.drawable.kebiao_header_select);
                kebiao_tv_monday_h.setTextColor(getResources().getColor(R.color.white));
                kebiao_tv_monday.setTextColor(getResources().getColor(R.color.white));
                break;
            case 2:
                kebiao_header_ll_2.setBackgroundResource(R.drawable.kebiao_header_select);
                kebiao_tv_tuesday_h.setTextColor(getResources().getColor(R.color.white));
                kebiao_tv_tuesday.setTextColor(getResources().getColor(R.color.white));
                break;
            case 3:
                kebiao_header_ll_3.setBackgroundResource(R.drawable.kebiao_header_select);
                kebiao_tv_wednesday_h.setTextColor(getResources().getColor(R.color.white));
                kebiao_tv_wednesday.setTextColor(getResources().getColor(R.color.white));
                break;
            case 4:
                kebiao_header_ll_4.setBackgroundResource(R.drawable.kebiao_header_select);
                kebiao_tv_fourthday_h.setTextColor(getResources().getColor(R.color.white));
                kebiao_tv_fourthday.setTextColor(getResources().getColor(R.color.white));
                break;
            case 5:
                kebiao_header_ll_5.setBackgroundResource(R.drawable.kebiao_header_select);
                kebiao_tv_friday_h.setTextColor(getResources().getColor(R.color.white));
                kebiao_tv_friday.setTextColor(getResources().getColor(R.color.white));
                break;
            case 6:
                kebiao_header_ll_6.setBackgroundResource(R.drawable.kebiao_header_select);
                kebiao_tv_Staday_h.setTextColor(getResources().getColor(R.color.white));
                kebiao_tv_Staday.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    public void initKebiao() {
        try {
            int[] counts = new int[7];
            for (int nn = 0; nn < 7; nn++) {
                linearLayout[nn].removeAllViews();
                counts[nn] = 0;
            }
            String extra = APPAplication.save.getString("extra", "");
            if (TextUtils.isEmpty(extra))
                kebiao_ll_extra.setVisibility(View.GONE);
            else {
                kebiao_ll_extra.setVisibility(View.VISIBLE);
                kebiao_extra.setText(extra);
            }
            for (int i = 1; i <= 7; i++) {
                for (int j = 1; j <= 5; j++) {
                    String sql = "select * from course where 星期=" + i + " and 节次=" + j + ";";
                    Cursor cursor = null;
                    try {
                        cursor = APPAplication.db.rawQuery(sql, null);
                        if (cursor.getCount() > 0) {
                            final List<HashMap<String, String>> course_info = new ArrayList<HashMap<String, String>>();
                            if (cursor.moveToFirst()) {
                                do {
                                    HashMap<String, String> map = new HashMap<>();
                                    map.put("课程代码", cursor.getString(cursor.getColumnIndex("课程代码")));
                                    map.put("课程名", cursor.getString(cursor.getColumnIndex("课程名")));
                                    map.put("老师", cursor.getString(cursor.getColumnIndex("老师")));
                                    map.put("教室", cursor.getString(cursor.getColumnIndex("教室")));
                                    map.put("节次", i + " " + j);
                                    map.put("颜色代码", cursor.getString(cursor.getColumnIndex("颜色代码")));
                                    map.put("周次", cursor.getString(cursor.getColumnIndex("周次")));
                                    map.put("index", cursor.getInt(0) + "");
                                    course_info.add(map);
                                } while (cursor.moveToNext());
                            }
                            Boolean flag = false;
                            TextView textView = new TextView(linearLayout[i - 1].getContext());
                            textView.setTextSize(12f);
                            textView.setPadding(5, 15, 5, 15);
                            textView.setGravity(Gravity.CENTER_HORIZONTAL);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, heightPixels / 6);
                            params.setMargins(0, (j - 1 - counts[i - 1]) * heightPixels / 6 + (j - counts[i - 1]) * 6, 0, 0);
                            textView.setLayoutParams(params);
                            linearLayout[i - 1].addView(textView);
                            for (int m = 0; m < course_info.size(); m++) {
                                try {
                                    String[] t5 = course_info.get(m).get("周次").replaceAll("\\(单周\\)|\\(周\\)|\\(双周\\)", "").split(",");
                                    for (int n = 0; n < t5.length; n++) {
                                        String[] t6 = t5[n].split("-");
                                        int start, end;
                                        start = Integer.parseInt(t6[0]);
                                        try {
                                            end = Integer.parseInt(t6[1]);
                                        } catch (Exception e) {
                                            end = start;
                                        }
                                        if (start <= week && week <= end) {
                                            flag = true;
                                            break;
                                        } else {
                                            if (n == t5.length - 1) {
                                                break;
                                            }
                                        }
                                    }
                                } catch (Exception e) {
                                    APPAplication.showToast(course_info.get(m).get("课程名") + " 的周次有语法问题，请修改!", 1);
                                }
                                if (flag) {
                                    textView.setTextColor(Color.WHITE);
                                    if (course_info.get(m).get("教室").trim().isEmpty()) {
                                        textView.setText(course_info.get(m).get("课程名"));
                                    } else {
                                        textView.setText(course_info.get(m).get("课程名") + "@" + course_info.get(m).get("教室"));
                                    }
                                    textView.setBackgroundResource(myImageList.get(Integer.parseInt(course_info.get(m).get("颜色代码"))));
                                    textView.getBackground().setAlpha(220);
                                    break;
                                } else {
                                    if (m == course_info.size() - 1) {
                                        if (kebiao_show_ct == 0) {
                                            textView.setTextColor(Color.rgb(167, 174, 174));
                                            textView.setBackgroundResource(R.drawable.textview_border_hui);
                                            textView.getBackground().setAlpha(200);
                                            if (course_info.get(0).get("教室").trim().isEmpty())
                                                textView.setText(course_info.get(0).get("课程名"));
                                            else
                                                textView.setText(course_info.get(0).get("课程名") + "@" + course_info.get(0).get("教室"));
                                        }
                                    }
                                }
                            }
                            final Intent intent = new Intent(activity, KebiaoDetailActivity.class);
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (course_info.size() > 1) {
                                        final String[] items = new String[course_info.size()];
                                        for (int i = 0; i < course_info.size(); i++) {
                                            items[i] = "[" + (i + 1) + "] " + course_info.get(i).get("课程名");
                                        }
                                        AlertDialog.Builder listDialog = new AlertDialog.Builder(activity);
                                        listDialog.setTitle("有多个课程");
                                        listDialog.setItems(items, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final Intent intent = new Intent(activity, KebiaoDetailActivity.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString("kcm", course_info.get(which).get("课程名"));
                                                bundle.putString("kcdm", course_info.get(which).get("课程代码"));
                                                bundle.putString("ls", course_info.get(which).get("老师"));
                                                bundle.putString("js", course_info.get(which).get("教室"));
                                                bundle.putString("zc", course_info.get(which).get("周次"));
                                                bundle.putString("jc", course_info.get(which).get("节次"));
                                                bundle.putString("index", course_info.get(which).get("index"));
                                                intent.putExtras(bundle);
                                                startActivityForResult(intent, 3);
                                            }
                                        });
                                        listDialog.show();
                                    } else {
                                        Bundle bundle = new Bundle();
                                        bundle.putString("kcm", course_info.get(0).get("课程名"));
                                        bundle.putString("kcdm", course_info.get(0).get("课程代码"));
                                        bundle.putString("ls", course_info.get(0).get("老师"));
                                        bundle.putString("js", course_info.get(0).get("教室"));
                                        bundle.putString("zc", course_info.get(0).get("周次"));
                                        bundle.putString("jc", course_info.get(0).get("节次"));
                                        bundle.putString("index", course_info.get(0).get("index"));
                                        intent.putExtras(bundle);
                                        startActivityForResult(intent, 3);
                                    }

                                }
                            });
                            counts[i - 1] = j;
                        }
                    } catch (Exception e) {
                        APPAplication.showDialog(getContext(), e.toString());
                    } finally {
                        if (cursor != null)
                            cursor.close();
                    }
                }
            }
        } catch (Exception e) {
            APPAplication.showDialog(getContext(), "载入课表发生了严重的错误，请反馈!\n" + e.toString());
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.kebiao_menu_add:
                View view = LayoutInflater.from(activity).inflate(R.layout.kebiao_add_dialog, null);
                final EditText c_kcm = view.findViewById(R.id.kebiao_detail_kcm_c2);
                final EditText c_kcdm = view.findViewById(R.id.kebiao_detail_kcdm_c2);
                final EditText c_ls = view.findViewById(R.id.kebiao_detail_ls_c2);
                final EditText c_js = view.findViewById(R.id.kebiao_detail_js_c2);
                final EditText c_sj = view.findViewById(R.id.kebiao_detail_sj_c2);
                final EditText c_zc = view.findViewById(R.id.kebiao_detail_zc_c2);
                Button c_bt1 = view.findViewById(R.id.kebiao_detail_bt1);
                Button c_bt2 = view.findViewById(R.id.kebiao_detail_bt2);
                final Dialog dialog = new AlertDialog.Builder(activity).setView(view).setTitle("添加课程")
                        .setCancelable(false).create();
                c_bt2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (dialog != null)
                            dialog.dismiss();
                    }
                });
                c_bt1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (c_kcm.getText().toString().equals("")) {
                            c_kcm.setError("课程名不能为空");
                            return;
                        } else {
                            c_kcm.setError(null);
                        }
                        if (c_sj.getText().toString().equals("")) {
                            c_sj.setError("时间不能为空");
                            return;
                        } else {
                            c_sj.setError(null);
                        }
                        if (c_zc.getText().toString().equals("")) {
                            c_zc.setError("周次不能为空");
                            return;
                        } else {
                            c_zc.setError(null);
                        }
                        Cursor cursor = null;
                        try {
                            ContentValues cv = new ContentValues();
                            cv.put("课程代码", c_kcdm.getText().toString());
                            cv.put("课程名", c_kcm.getText().toString());
                            cv.put("教室", c_js.getText().toString());
                            cv.put("老师", c_ls.getText().toString());
                            cv.put("周次", c_zc.getText().toString());
                            cv.put("星期", c_sj.getText().toString().split("\\s+")[0]);
                            cv.put("节次", c_sj.getText().toString().split("\\s+")[1]);
                            String sql = "select * from course where 课程名='" + cv.get("课程名") + "';";
                            cursor = APPAplication.db.rawQuery(sql, null);
                            if (cursor.getCount() > 0) {
                                cursor.moveToFirst();
                                cv.put("颜色代码", cursor.getInt(8) + "");
                            } else {
                                sql = "select 颜色代码 from course order by 颜色代码 desc";
                                cursor = APPAplication.db.rawQuery(sql, null);
                                cursor.moveToFirst();
                                int co = Integer.parseInt(cursor.getString(cursor.getColumnIndex("颜色代码")));
                                if (co == 13) {
                                    APPAplication.showToast("客官你的课程数量超过14了,目前还没有足够的颜色区分" +
                                            ",将使用重复的颜色!", 1);
                                    co = 0;
                                } else {
                                    co++;
                                }
                                cv.put("颜色代码", co);
                            }
                            APPAplication.db.insert("course", null, cv);
                            if (dialog != null)
                                dialog.dismiss();
                            initKebiao();
                            APPAplication.sendRefreshKebiao(activity);
                            APPAplication.showToast("添加成功!", 0);
                        } catch (Exception ee) {
                            APPAplication.showToast("输入的课程信息格式不对,请检查!", 1);
                        } finally {
                            if (cursor != null)
                                cursor.close();
                        }
                    }
                });
                dialog.show();
                break;
            case R.id.kebiao_menu_refresh:
                if (TextUtils.isEmpty(APPAplication.save.getString("xh", ""))) {
                    APPAplication.showToast("请先绑定账号!", 0);
                    return true;
                }
                new AlertDialog.Builder(getContext()).setTitle("提示")
                        .setMessage("从教务系统重新获取课表,之前已修改的课表数据将丢失,是否继续?")
                        .setCancelable(false)
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent data = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString("user", APPAplication.save.getString("xh", ""));
                                bundle.putString("pwd", APPAplication.save.getString("pwd", ""));
                                bundle.putString("term", APPAplication.term);
                                data.putExtra("data", bundle);
                                onLoginListener.onKebiaoRefresh(data);
                            }
                        }).setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
            case R.id.kebiao_menu_changeterm:
                final String[] datalist = new String[8];
                datalist[0] = "2016-2017-1";
                datalist[1] = "2016-2017-2";
                datalist[2] = "2017-2018-1";
                datalist[3] = "2017-2018-2";
                datalist[4] = "2018-2019-1";
                datalist[5] = "2018-2019-2";
                datalist[6] = "2019-2020-1";
                datalist[7] = "2019-2020-2";
                int index = 6;
                for (int i = 0; i < datalist.length; i++) {
                    if (datalist[i].equals(APPAplication.term))
                        index = i;
                }
                if (TextUtils.isEmpty(APPAplication.save.getString("xh", ""))) {
                    APPAplication.showToast("请先绑定账号!", 0);
                } else {
                    new android.support.v7.app.AlertDialog.Builder(activity).setTitle("更改学期")
                            .setSingleChoiceItems(datalist, index, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (!APPAplication.term.equals(datalist[which])) {
                                        APPAplication.term = datalist[which];
                                        APPAplication.save.edit().putString("term", datalist[which]).apply();
                                        Intent data = new Intent();
                                        Bundle bundle = new Bundle();
                                        bundle.putString("user", APPAplication.save.getString("xh", ""));
                                        bundle.putString("pwd", APPAplication.save.getString("pwd", ""));
                                        bundle.putString("term", APPAplication.term);
                                        data.putExtra("data", bundle);
                                        onLoginListener.onKebiaoRefresh(data);
                                    } else {
                                        APPAplication.showToast("当前已经是 " + datalist[which] + "学期了", 0);
                                    }
                                }
                            }).show();
                }
                break;
            case R.id.kebiao_menu_changebg:
                GalleryConfig galleryConfig = new GalleryConfig.Builder()
                        .imageLoader(new FrescoImageLoader())    // ImageLoader 加载框架（必填）
                        .iHandlerCallBack(iHandlerCallBack)     // 监听接口（必填）
                        .provider("com.yangs.just.fileprovider")   // provider (必填)
                        .isShowCamera(true)                     // 是否现实相机按钮  默认：false
                        .filePath("/Gallery/Pictures")          // 图片存放路径
                        .build();
                GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(getActivity());
                break;
            case R.id.kebiao_menu_change:
                if (TextUtils.isEmpty(APPAplication.save.getString("xh", ""))) {
                    APPAplication.showToast("请先绑定账号!", 0);
                    return true;
                }
                if (kebiao_show_ct == 0) {
                    kebiao_show_ct = 1;
                    APPAplication.save.edit().putInt("kebiao_show_ct", kebiao_show_ct).apply();
                    initKebiao();
                } else {
                    kebiao_show_ct = 0;
                    APPAplication.save.edit().putInt("kebiao_show_ct", kebiao_show_ct).apply();
                    initKebiao();
                }
                break;
        }
        return true;
    }

    IHandlerCallBack iHandlerCallBack = new IHandlerCallBack() {
        @Override
        public void onStart() {

        }

        @Override
        public void onSuccess(List<String> photoList) {
            Drawable background = Drawable.createFromPath(photoList.get(0));
            kebiao_ll.setBackground(background);
            try {
                FileInputStream fosfrom = new FileInputStream(new File(photoList.get(0)));
                FileOutputStream fosto = new FileOutputStream(new File(APPAplication.getPath() + "/background.jpg"));
                byte[] bt = new byte[1024];
                int c;
                while ((c = fosfrom.read(bt)) > 0) {
                    fosto.write(bt, 0, c);
                }
                fosfrom.close();
                fosto.close();
            } catch (Exception e) {
                APPAplication.showToast(e.getMessage(), 1);
            }
            APPAplication.showToast("设置成功!", 0);
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onFinish() {

        }

        @Override
        public void onError() {
            APPAplication.showDialog(activity, "发生了错误,请反馈!");
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.kebiao_toolbar_time:
                if (APPAplication.login_stat != 0) {
                    View view = View.inflate(activity, R.layout.changweek_layout, null);
                    ListView weekList = view.findViewById(R.id.weekList);
                    ArrayList<String> strList = new ArrayList<String>();
                    for (int i = 1; i < 21; i++) {
                        if (i == APPAplication.week) {
                            strList.add(">第" + i + "周<");
                        } else {
                            strList.add("第" + i + "周");
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, R.layout.item, strList);
                    weekList.setAdapter(adapter);
                    view.measure(0, 0);
                    final PopupWindow pop = new PopupWindow(view, 400, 600, true);
                    pop.setBackgroundDrawable(new ColorDrawable(0x00000000));
                    int xOffSet = -(pop.getWidth() - v.getWidth()) / 2;
                    pop.showAsDropDown(v, xOffSet, 0);
                    if (week >= 3) {
                        weekList.setSelection(week - 3);
                    }
                    weekList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapter, View view,
                                                int positon, long id) {
                            Date d = new Date(System.currentTimeMillis());
                            initKebiaoHeader(DateUtil.getDateAfter(d, 7 * (positon + 1 - APPAplication.week)));
                            week = positon + 1;
                            toolbar_time.setText("第" + week + "周 ▾");
                            initKebiao();
                            pop.dismiss();
                        }
                    });
                } else {
                    APPAplication.showToast("请先绑定账号!", 0);
                }
                break;
        }
    }
}
