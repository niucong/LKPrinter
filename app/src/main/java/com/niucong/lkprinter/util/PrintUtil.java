package com.niucong.lkprinter.util;

import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Base64;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.io.utils.GpUtils;
import com.niucong.lkprinter.db.HotelCheckDB;

import java.text.SimpleDateFormat;
import java.util.Vector;

/**
 * Created by think on 2017/3/2.
 */

public class PrintUtil {

    /**
     * 打印小票
     *
     * @param mGpService
     * @param hotelCheckDB
     */
    public static void printStick(GpService mGpService, HotelCheckDB hotelCheckDB) {
        EscCommand esc = new EscCommand();
        esc.addPrintAndFeedLines((byte) 2);
        // 设置打印居中
        esc.addSelectJustification(EscCommand.JUSTIFICATION.CENTER);

        // 设置为倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.ON,
                EscCommand.ENABLE.ON, EscCommand.ENABLE.OFF);
        // 打印文字
        esc.addText("蓝凯宾馆\n");
        esc.addPrintAndLineFeed();

        // 取消倍高倍宽
        esc.addSelectPrintModes(EscCommand.FONT.FONTA, EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF,
                EscCommand.ENABLE.OFF, EscCommand.ENABLE.OFF);
        esc.addSelectJustification(EscCommand.JUSTIFICATION.LEFT);// 设置打印左对齐

        esc.addText("流水号：" + hotelCheckDB.getSerial_number() + "\n");
        if (!TextUtils.isEmpty(hotelCheckDB.getName())) {
            esc.addText("客户名称：" + hotelCheckDB.getName() + "\n");
        }
        if (!TextUtils.isEmpty(hotelCheckDB.getPhone())) {
            esc.addText("客户电话：" + hotelCheckDB.getPhone() + "\n");
        }
        if (!TextUtils.isEmpty(hotelCheckDB.getCard())) {
            esc.addText("身份证号：" + hotelCheckDB.getCard() + "\n");
        }
        esc.addText("房间号：" + hotelCheckDB.getRoom() + "\n");
        esc.addText("入住性质：" + hotelCheckDB.getType() + "\n");
        esc.addText("入住时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(hotelCheckDB.getTime()) + "\n");
        esc.addText("最晚退房时间：" + new SimpleDateFormat("yyyy-MM-dd HH:mm").format(hotelCheckDB.getOut()) + "\n");
        esc.addText("入住天数：" + hotelCheckDB.getDay() + "天\n");
        esc.addText("房间单价：" + hotelCheckDB.getPrice() + "元\n");
        esc.addText("房间总费用：" + hotelCheckDB.getPrice() * hotelCheckDB.getDay() + "元\n");
        esc.addText("已交费用：" + hotelCheckDB.getPay() + " " + hotelCheckDB.getCost() + "元\n");
        esc.addText("退房押金：" + hotelCheckDB.getDeposit() + "元\n");
        esc.addText("客户来源：" + hotelCheckDB.getFrom() + "\n");
        esc.addText("宾馆电话：010-84286638\n");
        esc.addText("宾馆地址：北京市朝阳区北三环东路和平西桥西南侧（地铁站A口）\n");
        esc.addPrintAndLineFeed();

//        esc.addPrintAndLineFeed();
//        // 设置条码可识别字符位置在条码下方
//        esc.addSelectPrintingPositionForHRICharacters(EscCommand.HRI_POSITION.BELOW);
//        // 设置条码高度为60点
//        esc.addSetBarcodeHeight((byte) 60);
//        // 设置条码单元宽度为1点
//        esc.addSetBarcodeWidth((byte) 2);
//        // 打印Code128码
//        esc.addCODE128("" + d.getTime());
//        esc.addCODE128(esc.genCodeB("" + d.getTime()));
        esc.addPrintAndFeedLines((byte) 2);

        Vector<Byte> datas = esc.getCommand();
        // 发送数据
        Byte[] Bytes = datas.toArray(new Byte[datas.size()]);
        byte[] bytes = GpUtils.ByteTo_byte(Bytes);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try {
            rel = mGpService.sendEscCommand(0, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
//                Toast.makeText(App.app, GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
