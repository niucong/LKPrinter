package com.niucong.lkprinter.db;

import android.os.Parcel;
import android.os.Parcelable;

import org.litepal.crud.DataSupport;

/**
 * Created by think on 2018/3/26.
 */

public class HotelCheckDB extends DataSupport implements Parcelable {
    private int id;
    private long serial_number;// 流水号
    private String name;// 客户名称
    private String phone;// 客户电话
    private String card;// 身份证号
    private String room;// 房间号
    private int price;// 房间单价
    private String type;// 入住性质：全天、钟点、续住
    private long time;// 入住时间
    private long out;// 最晚退房时间
    private int day;// 入住天数
    private int cost;// 已交费用
    private int deposit;// 已收押金
    private String pay;// 收款方式：支付宝、微信、美团、现金（默认）、刷卡
    private String from;// 电话、美团、携程、去哪儿、其它（默认）

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getOut() {
        return out;
    }

    public void setOut(long out) {
        this.out = out;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(long serial_number) {
        this.serial_number = serial_number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public String getPay() {
        return pay;
    }

    public void setPay(String pay) {
        this.pay = pay;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeLong(this.serial_number);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.card);
        dest.writeString(this.room);
        dest.writeString(this.type);
        dest.writeLong(this.time);
        dest.writeLong(this.out);
        dest.writeInt(this.day);
        dest.writeInt(this.price);
        dest.writeInt(this.cost);
        dest.writeInt(this.deposit);
        dest.writeString(this.pay);
        dest.writeString(this.from);
    }

    public HotelCheckDB() {
    }

    protected HotelCheckDB(Parcel in) {
        this.id = in.readInt();
        this.serial_number = in.readLong();
        this.name = in.readString();
        this.phone = in.readString();
        this.card = in.readString();
        this.room = in.readString();
        this.type = in.readString();
        this.time = in.readLong();
        this.out = in.readLong();
        this.day = in.readInt();
        this.price = in.readInt();
        this.cost = in.readInt();
        this.deposit = in.readInt();
        this.pay = in.readString();
        this.from = in.readString();
    }

    public static final Creator<HotelCheckDB> CREATOR = new Creator<HotelCheckDB>() {
        @Override
        public HotelCheckDB createFromParcel(Parcel source) {
            return new HotelCheckDB(source);
        }

        @Override
        public HotelCheckDB[] newArray(int size) {
            return new HotelCheckDB[size];
        }
    };
}
