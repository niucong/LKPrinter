package com.niucong.lkprinter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.niucong.lkprinter.db.HotelCheckDB;

import org.litepal.crud.DataSupport;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordListActivity extends AppCompatActivity {
    private static final String TAG = "RecordListActivity";

    private RecyclerView mRecyclerView;
    private EditText et_search;

    private List<HotelCheckDB> mDatas;

    private SimpleDateFormat ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

    private Date startDate, endDate;

    private String startTip;
    private int showType;// 0:按订单查看、1：按药品查看

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.record_list_rv);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        showToday();
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));

        mRecyclerView.requestFocus();

        et_search = (EditText) findViewById(R.id.et_search);
        et_search.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s.toString())) {
                    setAdapter(new ArrayList<HotelCheckDB>());
                } else {
                    searchRecord(s.toString());
                }
            }
        });
    }

    private void showToday() {
        try {
            selectData(ymd.parse(ymd.format(new Date())), new Date());
        } catch (ParseException e) {
            mDatas = new ArrayList<>();
        }
        setAdapter(mDatas);
    }

    private void setAdapter(List<HotelCheckDB> srs) {
        if (srs == null) {
            srs = new ArrayList<>();
        }
        mRecyclerView.setAdapter(new RecordAdapter(srs));
    }

    private void selectData(Date st, Date et) {
//        mDatas = DBUtil.getDaoSession().getSellRecordDao().queryBuilder().where(SellRecordDao.Properties.SellDate.ge(st),
//                SellRecordDao.Properties.SellDate.le(et)).orderDesc(SellRecordDao.Properties.SellDate).list();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.menu_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_all:
                mDatas = DataSupport.findAll(HotelCheckDB.class);
                setAdapter(mDatas);
                break;
            case R.id.action_day:
                try {
                    selectData(ymd.parse(ymd.format(new Date())), new Date());
                    setAdapter(mDatas);
                } catch (ParseException e) {
                    mDatas = new ArrayList<>();
                }
                break;
            case R.id.action_mouth:
                String startDate = "";
                try {
                    Calendar c = Calendar.getInstance();
                    if (c.get(Calendar.MONTH) < 9) {
                        startDate = c.get(Calendar.YEAR) + "-0" + (c.get(Calendar.MONTH) + 1) + "-01";
                    } else {
                        startDate = c.get(Calendar.YEAR) + "-" + (c.get(Calendar.MONTH) + 1) + "-01";
                    }
                    selectData(ymd.parse(startDate), new Date());
                    setAdapter(mDatas);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_select:
//                showSubmitDia();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    /**
//     * 选择日期对话框
//     */
//    private void showSubmitDia() {
//        final NiftyDialogBuilder submitDia = NiftyDialogBuilder.getInstance(this);
//        View selectDateView = LayoutInflater.from(this).inflate(R.layout.dialog_select_date, null);
//        final DateTimeSelectView ds = (DateTimeSelectView) selectDateView.findViewById(R.id.date_start);
//        final DateTimeSelectView de = (DateTimeSelectView) selectDateView.findViewById(R.id.date_end);
//
//        final Calendar c = Calendar.getInstance();
//        try {
//            startDate = ymdhm.parse(ymdhm.format(new Date()));// 当日00：00：00
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        endDate = new Date();
//
//        submitDia.withTitle("选择查询日期");
//        submitDia.withButton1Text("取消", 0).setButton1Click(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                submitDia.dismiss();
//            }
//        });
//        submitDia.withButton2Text("确定", 0).setButton2Click(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                    startDate = ymdhm.parse(ds.getDate());
//                    Log.d(TAG, "开始：" + ds.getDate() + "，结束：" + de.getDate() + "，当前：" + ymdhm.format(new Date()));
////                    if (ymd.format(new Date()).equals(de.getDate())) {// 结束日期是今天
////                        endDate = new Date();// 当前时间
////                    } else {
////                        endDate = new Date(ymd.parse(de.getDate()).getTime() + 1000 * 60 * 60 * 24 - 1);// 当日23：59：59
////                    }
//                    endDate = ymdhm.parse(de.getDate());
//                    if (endDate.before(startDate)) {
//                        Snackbar.make(mRecyclerView, "开始日期不能大于结束日期", Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
//                    } else {
//                        selectData(startDate, endDate);
//                        setAdapter(mDatas);
//                        startTip = ymdhm.format(startDate) + "到" + ymdhm.format(endDate) + "的";
//                        setStatistics();
//                        submitDia.dismiss();
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        submitDia.setCustomView(selectDateView, this);// "请选择查询日期"
//        submitDia.withMessage(null).withDuration(400);
//        submitDia.isCancelable(false);
//        submitDia.show();
//    }

    private void searchRecord(String result) {
        long code = Long.valueOf(result);
        List<HotelCheckDB> sDatas = null;
        sDatas = DataSupport.where("room = ? or name = ? or phone = ? or card = ?",
                result, result, result, result).find(HotelCheckDB.class);
        setAdapter(sDatas);
    }

    class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.MyViewHolder> {

        List<HotelCheckDB> hotelCheckDBS;

        public RecordAdapter(List<HotelCheckDB> hotelCheckDBS) {
            this.hotelCheckDBS = hotelCheckDBS;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = new MyViewHolder(LayoutInflater.from(RecordListActivity.this).inflate(R.layout.item_record, parent, false));
            return holder;
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            HotelCheckDB hotelCheckDB = hotelCheckDBS.get(position);
            holder.tv_num.setText(position + 1 + "");
            holder.tv_name.setText(hotelCheckDB.getName());
            holder.tv_room.setText(hotelCheckDB.getRoom());
            holder.tv_time.setText(ymdhm.format(new Date(hotelCheckDB.getTime())));
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    startActivity(new Intent(StatisticsActivity.this, OrderActivity.class).putExtra("Date", d.getTime()));
                }
            });

            holder.tv_delete.setTextColor(Color.RED);
            holder.tv_delete.setText("删除");
            holder.tv_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return hotelCheckDBS.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_num, tv_name, tv_room, tv_time, tv_delete;

            public MyViewHolder(View view) {
                super(view);
                tv_num = (TextView) view.findViewById(R.id.item_record_num);
                tv_name = (TextView) view.findViewById(R.id.item_record_name);
                tv_room = (TextView) view.findViewById(R.id.item_record_room);
                tv_time = (TextView) view.findViewById(R.id.item_record_time);
                tv_delete = (TextView) view.findViewById(R.id.item_record_operate);
            }
        }
    }

}
