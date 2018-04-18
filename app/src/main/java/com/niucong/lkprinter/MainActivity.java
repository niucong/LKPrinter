package com.niucong.lkprinter;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.GpUtils;
import com.gprinter.command.LabelCommand;
import com.gprinter.io.GpDevice;
import com.gprinter.service.GpPrintService;
import com.niucong.lkprinter.db.HotelCheckDB;
import com.niucong.lkprinter.printer.PrinterConnectDialog;
import com.niucong.lkprinter.util.PrintUtil;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout ll_room;
    private EditText etName, etPhone, etCard, etDay, etCost, etDeposit;
    private RadioButton rbTypeHour, rbTypeOverstay, rbPayAlipay, rbPayWechat, rbPayMeituan, rbPayXiecheng, rbPayFeizhu, rbPayPos,
            rbFromPhone, rbFromMeituan, rbFromXiecheng, rbFromFeizhu;
    private TextView tvTime, tvOut, tvCost;

    private String dateTimeStr;
    private SimpleDateFormat ymdhm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd 12:00:00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LitePal.initialize(this);
        connection();
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));

        initView();

        tvTime.setOnClickListener(this);
        tvOut.setOnClickListener(this);
        findViewById(R.id.add_room).setOnClickListener(this);
        findViewById(R.id.btn_print).setOnClickListener(this);

        addRoomView();

        etDay.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    tvTime.setText(ymdhm.format(new Date()));
                    tvOut.setText(ymd.format(new Date(System.currentTimeMillis() +
                            Long.valueOf(s.toString()) * 24 * 60 * 60 * 1000)));
                    calculateTotlePrice();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        rbTypeHour.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    etDay.setText("0");
                    etDay.setEnabled(false);
                    tvTime.setText(ymdhm.format(new Date()));
                    tvOut.setText(ymdhm.format(new Date(System.currentTimeMillis() + 4 * 60 * 60 * 1000)));
                } else {
                    etDay.setText("");
                    etDay.setEnabled(true);
                    tvTime.setText("");
                    tvOut.setText("");
                }
            }
        });
    }

    private void initView() {
        etName = (EditText) findViewById(R.id.et_name);
        etPhone = (EditText) findViewById(R.id.et_phone);
        etCard = (EditText) findViewById(R.id.et_card);
        rbTypeHour = (RadioButton) findViewById(R.id.rb_type_hour);
        rbTypeOverstay = (RadioButton) findViewById(R.id.rb_type_overstay);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvOut = (TextView) findViewById(R.id.tv_out);
        etDay = (EditText) findViewById(R.id.et_day);
        tvCost = (TextView) findViewById(R.id.tv_cost);
        etCost = (EditText) findViewById(R.id.et_cost);
        etDeposit = (EditText) findViewById(R.id.et_deposit);
        rbPayAlipay = (RadioButton) findViewById(R.id.rb_pay_alipay);
        rbPayWechat = (RadioButton) findViewById(R.id.rb_pay_wechat);
        rbPayMeituan = (RadioButton) findViewById(R.id.rb_pay_meituan);
        rbPayXiecheng = (RadioButton) findViewById(R.id.rb_pay_xiecheng);
        rbPayFeizhu = (RadioButton) findViewById(R.id.rb_pay_feizhu);
        rbPayPos = (RadioButton) findViewById(R.id.rb_pay_pos);
        rbFromPhone = (RadioButton) findViewById(R.id.rb_from_phone);
        rbFromMeituan = (RadioButton) findViewById(R.id.rb_from_meituan);
        rbFromXiecheng = (RadioButton) findViewById(R.id.rb_from_xiecheng);
        rbFromFeizhu = (RadioButton) findViewById(R.id.rb_from_feizhu);

        ll_room = (LinearLayout) findViewById(R.id.ll_room);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_time:
                selectDateTime(tvTime);
                break;
            case R.id.tv_out:
                selectDateTime(tvOut);
                break;
            case R.id.add_room:
                addRoomView();
                break;
            case R.id.btn_print:
                save();
                break;
        }
    }

    private void addRoomView() {
        final View view = LayoutInflater.from(this).inflate(
                R.layout.item_room_add, null);
        ((EditText) view.findViewById(R.id.et_price)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateTotlePrice();
            }
        });
        view.findViewById(R.id.remove_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ll_room.getChildCount() > 1) {
                    ll_room.removeView(view);
                    calculateTotlePrice();
                } else {
                    Toast.makeText(MainActivity.this, "只剩一个房间了", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ll_room.addView(view);
    }

    /**
     * 计算房间总价
     */
    private void calculateTotlePrice() {
        long totle = 0;
        try {
            int day = Integer.valueOf(etDay.getText().toString());
            for (int i = 0; i < ll_room.getChildCount(); i++) {
                View view = ll_room.getChildAt(i);
                EditText etPrice = view.findViewById(R.id.et_price);
                if (rbTypeHour.isChecked()) {
                    totle += Long.valueOf(etPrice.getText().toString());
                } else {
                    totle += Long.valueOf(Long.valueOf(day) * Integer.valueOf(etPrice.getText().toString()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        tvCost.setText("" + totle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_record:
                startActivity(new Intent(this, RecordListActivity.class));
                break;
            case R.id.action_settings:
                if (mGpService == null) {
                    Toast.makeText(this, "Print Service is not start, please check it", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(this, PrinterConnectDialog.class);
                    boolean[] state = getConnectState();
                    intent.putExtra("connect.status", state);
                    this.startActivity(intent);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        try {

            for (int i = 0; i < ll_room.getChildCount(); i++) {
                View view = ll_room.getChildAt(i);
                EditText etRoom = view.findViewById(R.id.et_room);
                EditText etPrice = view.findViewById(R.id.et_price);
                if (TextUtils.isEmpty(etRoom.getText().toString())) {
                    Toast.makeText(this, "房间号不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                if (TextUtils.isEmpty(etPrice.getText().toString())) {
                    Toast.makeText(this, "房间单价不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            String start = tvTime.getText().toString();
            if (TextUtils.isEmpty(start)) {
                Toast.makeText(this, "入住时间不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            String end = tvOut.getText().toString();
            if (TextUtils.isEmpty(end)) {
                Toast.makeText(this, "最晚退房时间不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            long startTime = ymdhm.parse(start).getTime();
            long endTime = ymdhm.parse(end).getTime();// 结束时间
            if (startTime > endTime) {
                Toast.makeText(this, "最晚退房时间不能早于入住时间", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(etDay.getText().toString())) {
                Toast.makeText(this, "入住天数不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            if (!rbTypeHour.isChecked() && "0".equals(etDay.getText().toString())) {
                Toast.makeText(this, "非钟点房入住天数不能为0", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(etCost.getText().toString())) {
                Toast.makeText(this, "已交费用不能为空", Toast.LENGTH_LONG).show();
                return;
            }
            if (TextUtils.isEmpty(etDeposit.getText().toString())) {
                Toast.makeText(this, "退房押金不能为空", Toast.LENGTH_LONG).show();
                return;
            }

            long serial_number = System.currentTimeMillis();
            final List<HotelCheckDB> list = new ArrayList<>();
            for (int i = 0; i < ll_room.getChildCount(); i++) {
                View view = ll_room.getChildAt(i);
                EditText etRoom = view.findViewById(R.id.et_room);
                EditText etPrice = view.findViewById(R.id.et_price);
                final HotelCheckDB hotelCheckDB = new HotelCheckDB();
                hotelCheckDB.setSerial_number(serial_number);
                hotelCheckDB.setName(etName.getText().toString());
                hotelCheckDB.setPhone(etPhone.getText().toString());
                hotelCheckDB.setCard(etCard.getText().toString());
                hotelCheckDB.setRoom(etRoom.getText().toString());
                hotelCheckDB.setPrice(Integer.valueOf(etPrice.getText().toString()));
                if (rbTypeHour.isChecked()) {
                    hotelCheckDB.setType("钟点");
                } else if (rbTypeOverstay.isChecked()) {
                    hotelCheckDB.setType("续住");
                } else {
                    hotelCheckDB.setType("全天");
                }
                hotelCheckDB.setTime(startTime);
                hotelCheckDB.setOut(endTime);
                hotelCheckDB.setDay(Integer.valueOf(etDay.getText().toString()));
                hotelCheckDB.setCost(Integer.valueOf(etCost.getText().toString()));
                hotelCheckDB.setDeposit(Integer.valueOf(etDeposit.getText().toString()));
                if (rbPayAlipay.isChecked()) {
                    hotelCheckDB.setPay("支付宝");
                } else if (rbPayWechat.isChecked()) {
                    hotelCheckDB.setPay("微信");
                } else if (rbPayMeituan.isChecked()) {
                    hotelCheckDB.setPay("美团");
                } else if (rbPayXiecheng.isChecked()) {
                    hotelCheckDB.setPay("携程");
                } else if (rbPayFeizhu.isChecked()) {
                    hotelCheckDB.setPay("飞猪");
                } else if (rbPayPos.isChecked()) {
                    hotelCheckDB.setPay("刷卡");
                } else {
                    hotelCheckDB.setPay("现金");
                }
                if (rbFromPhone.isChecked()) {
                    hotelCheckDB.setFrom("电话预定");
                } else if (rbFromMeituan.isChecked()) {
                    hotelCheckDB.setFrom("美团");
                } else if (rbFromXiecheng.isChecked()) {
                    hotelCheckDB.setFrom("携程");
                } else if (rbFromFeizhu.isChecked()) {
                    hotelCheckDB.setFrom("飞猪");
                } else {
                    hotelCheckDB.setFrom("其它");
                }
                list.add(hotelCheckDB);
            }
            DataSupport.saveAll(list);

            PrintUtil.printStick(mGpService, list);

//            new AlertDialog.Builder(MainActivity.this)
//                    .setTitle("提示")
//                    .setMessage("是否继续打印小票")
//                    .setPositiveButton("继续打印",
//                            new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    PrintUtil.printStick(mGpService, list);
//                                }
//                            }).setNegativeButton("取消", null).show();

            etName.setText("");
            etPhone.setText("");
            etCard.setText("");
            etDay.setText("");
            tvTime.setText("");
            tvOut.setText("");
            tvCost.setText("");
            etCost.setText("");
            etDeposit.setText("");
            ll_room.removeAllViews();
            addRoomView();
        } catch (Exception e) {
            Toast.makeText(this, "输入信息错误", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 选择日期时间
     */
    private void selectDateTime(final TextView textView) {
        final Calendar c = Calendar.getInstance();
        final TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                StringBuffer timeBuffer = new StringBuffer("");
                if (hourOfDay < 10) {
                    timeBuffer.append("0");
                }
                timeBuffer.append(hourOfDay + ":");
                if (minute < 10) {
                    timeBuffer.append("0");
                }
                timeBuffer.append(minute);
                textView.setText(dateTimeStr + timeBuffer);
            }
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true);

        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateTimeStr = "";
                dateTimeStr += year + "-";
                if (monthOfYear < 9) {
                    dateTimeStr += "0";
                }
                dateTimeStr += (monthOfYear + 1) + "-";
                if (dayOfMonth < 10) {
                    dateTimeStr += "0";
                }
                dateTimeStr += dayOfMonth + " ";
                if (!timePickerDialog.isShowing()) {
                    timePickerDialog.show();
                }
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private GpService mGpService = null;
    private PrinterServiceConnection conn = null;

    private int mPrinterIndex = 0;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_LABEL = 0xfd;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;

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

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // GpCom.ACTION_DEVICE_REAL_STATUS 为广播的IntentFilter
            if (action.equals(GpCom.ACTION_DEVICE_REAL_STATUS)) {

                // 业务逻辑的请求码，对应哪里查询做什么操作
                int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
                // 判断请求码，是则进行业务操作
                if (requestCode == MAIN_QUERY_PRINTER_STATUS) {

                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    String str;
                    if (status == GpCom.STATE_NO_ERR) {
                        str = "打印机正常";
                    } else {
                        str = "打印机 ";
                        if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                            str += "脱机";
                        }
                        if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                            str += "缺纸";
                        }
                        if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                            str += "打印机开盖";
                        }
                        if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                            str += "打印机出错";
                        }
                        if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
                            str += "查询超时";
                        }
                    }

                    Toast.makeText(getApplicationContext(), "打印机：" + mPrinterIndex + " 状态：" + str, Toast.LENGTH_SHORT)
                            .show();
                } else if (requestCode == REQUEST_PRINT_LABEL) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status == GpCom.STATE_NO_ERR) {
                        sendLabel();
                    } else {
                        Toast.makeText(MainActivity.this, "query printer status error", Toast.LENGTH_SHORT).show();
                    }
                } else if (requestCode == REQUEST_PRINT_RECEIPT) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status == GpCom.STATE_NO_ERR) {
                        sendReceipt();
                    } else {
                        Toast.makeText(MainActivity.this, "query printer status error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    void sendLabel() {
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(60, 60); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(0); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(LabelCommand.DIRECTION.BACKWARD, LabelCommand.MIRROR.NORMAL);// 设置打印方向
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(EscCommand.ENABLE.ON); // 撕纸模式开启
        tsc.addCls();// 清除打印缓冲区
        // 绘制简体中文
        tsc.addText(20, 20, LabelCommand.FONTTYPE.SIMPLIFIED_CHINESE, LabelCommand.ROTATION.ROTATION_0, LabelCommand.FONTMUL.MUL_1, LabelCommand.FONTMUL.MUL_1,
                "Welcome to use SMARNET printer!");
        // 绘制图片
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_printer);
        tsc.addBitmap(20, 50, LabelCommand.BITMAP_MODE.OVERWRITE, b.getWidth(), b);

        tsc.addQRCode(250, 80, LabelCommand.EEC.LEVEL_L, 5, LabelCommand.ROTATION.ROTATION_0, " www.smarnet.cc");
        // 绘制一维条码
        tsc.add1DBarcode(20, 250, LabelCommand.BARCODETYPE.CODE128, 100, LabelCommand.READABEL.EANBEL, LabelCommand.ROTATION.ROTATION_0, "SMARNET");
        tsc.addPrint(1, 1); // 打印标签
        tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);
        Vector<Byte> datas = tsc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try {
            rel = mGpService.sendLabelCommand(mPrinterIndex, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    void sendReceipt() {
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON, EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);// 设置为倍高倍宽
        esc.addText("Sample\n"); // 打印文字
        esc.addPrintAndLineFeed();

        /* 打印文字 */
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);// 取消倍高倍宽
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐
        esc.addText("Print text\n"); // 打印文字
        esc.addText("Welcome to use SMARNET printer!\n"); // 打印文字

        /* 打印繁体中文 需要打印机支持繁体字库 */
        String message = "佳博智匯票據打印機\n";
        // esc.addText(message,"BIG5");
        esc.addText(message, "GB2312");
        esc.addPrintAndLineFeed();

        /* 绝对位置 具体详细信息请查看GP58编程手册 */
        esc.addText("智汇");
        esc.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
        esc.addSetAbsolutePrintPosition((short) 6);
        esc.addText("网络");
        esc.addSetAbsolutePrintPosition((short) 10);
        esc.addText("设备");
        esc.addPrintAndLineFeed();

        /* 打印图片 */
        // esc.addText("Print bitmap!\n"); // 打印文字
        // Bitmap b = BitmapFactory.decodeResource(getResources(),
        // R.drawable.gprinter);
        // esc.addRastBitImage(b, b.getWidth(), 0); // 打印图片

        /* 打印一维条码 */
        esc.addText("Print code128\n"); // 打印文字
        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);//
        // 设置条码可识别字符位置在条码下方
        esc.addSetBarcodeHeight((byte) 60); // 设置条码高度为60点
        esc.addSetBarcodeWidth((byte) 1); // 设置条码单元宽度为1
        esc.addCODE128(esc.genCodeB("SMARNET")); // 打印Code128码
        esc.addPrintAndLineFeed();

        /*
         * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
         */
        esc.addText("Print QRcode\n"); // 打印文字
        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31); // 设置纠错等级
        esc.addSelectSizeOfModuleForQRCode((byte) 3);// 设置qrcode模块大小
        esc.addStoreQRCodeData("www.smarnet.cc");// 设置qrcode内容
        esc.addPrintQRCode();// 打印QRCode
        esc.addPrintAndLineFeed();

        /* 打印文字 */
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);// 设置打印左对齐
        esc.addText("Completed!\r\n"); // 打印结束
        esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
        // esc.addGeneratePluseAtRealtime(LabelCommand.FOOT.F2, (byte) 8);

        esc.addPrintAndFeedLines((byte) 8);

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs = mGpService.sendEscCommand(mPrinterIndex, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    public boolean[] getConnectState() {
        boolean[] state = new boolean[GpPrintService.MAX_PRINTER_CNT];
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            state[i] = false;
        }
        for (int i = 0; i < GpPrintService.MAX_PRINTER_CNT; i++) {
            try {
                if (mGpService.getPrinterConnectStatus(i) == GpDevice.STATE_CONNECTED) {
                    state[i] = true;
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return state;
    }
}
