package com.niucong.lkprinter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gprinter.aidl.GpService;
import com.gprinter.service.GpPrintService;
import com.niucong.lkprinter.db.HotelCheckDB;
import com.niucong.lkprinter.util.PrintUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordDetailActivity extends AppCompatActivity {
    private static final String TAG = "RecordDetailActivity";

    private TextView tv_number, tv_name, tv_phone, tv_card, tv_room, tv_type, tv_time, tv_out,
            tv_day, tv_price, tv_cost, tv_paid, tv_deposit, tv_pay, tv_from;

    private SimpleDateFormat ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private HotelCheckDB hotelCheckDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_detail);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        hotelCheckDB = getIntent().getParcelableExtra("HotelCheckDB");

        initView();

        tv_number.setText(hotelCheckDB.getSerial_number() + "");
        tv_name.setText(hotelCheckDB.getName());
        tv_phone.setText(hotelCheckDB.getPhone());
        tv_card.setText(hotelCheckDB.getCard());
        tv_room.setText(hotelCheckDB.getRoom());
        tv_type.setText(hotelCheckDB.getType());
        tv_time.setText(ymdhm.format(new Date(hotelCheckDB.getTime())));
        tv_out.setText(ymdhm.format(new Date(hotelCheckDB.getOut())));
        tv_day.setText(hotelCheckDB.getDay() + "");
        tv_price.setText(hotelCheckDB.getPrice() + "");
        tv_cost.setText(hotelCheckDB.getDay() * hotelCheckDB.getPrice() + "");
        tv_paid.setText(hotelCheckDB.getCost() + "");
        tv_deposit.setText(hotelCheckDB.getDeposit() + "");
        tv_pay.setText(hotelCheckDB.getPay());
        tv_from.setText(hotelCheckDB.getFrom());

        findViewById(R.id.btn_print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrintUtil.printStick(mGpService, hotelCheckDB);
            }
        });

        conn = new PrinterServiceConnection();
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_card = (TextView) findViewById(R.id.tv_card);
        tv_room = (TextView) findViewById(R.id.tv_room);
        tv_type = (TextView) findViewById(R.id.tv_type);
        tv_time = (TextView) findViewById(R.id.tv_time);
        tv_out = (TextView) findViewById(R.id.tv_out);
        tv_day = (TextView) findViewById(R.id.tv_day);
        tv_price = (TextView) findViewById(R.id.tv_price);
        tv_cost = (TextView) findViewById(R.id.tv_cost);
        tv_paid = (TextView) findViewById(R.id.tv_paid);
        tv_deposit = (TextView) findViewById(R.id.tv_deposit);
        tv_pay = (TextView) findViewById(R.id.tv_pay);
        tv_from = (TextView) findViewById(R.id.tv_from);
    }

    private PrinterServiceConnection conn = null;
    public GpService mGpService = null;

    class PrinterServiceConnection implements ServiceConnection {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i("ServiceConnection", "onServiceDisconnected() called");
            mGpService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mGpService = GpService.Stub.asInterface(service);
        }
    }

}
