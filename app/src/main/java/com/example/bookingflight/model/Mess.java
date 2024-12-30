package com.example.bookingflight.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Mess implements Parcelable {
    private String maTN;
    private String noiDung1;  // Tin nhắn đã nhận (có thể là đã mã hóa)
    private String noiDung2;  // Tin nhắn gửi đi (có thể là đã mã hóa)
    private String thoiGianGui;
    private String maKH, maNV;
    private boolean isAutoReply;
    private boolean fromCustomer;
    private String fullname;
    private String encryptedAESKey;  // Khóa AES đã mã hóa
    private String encryptedMessage; // Tin nhắn đã mã hóa (để gửi đi)
    private String decryptedMessage; // Tin nhắn đã giải mã (để hiển thị)

    public Mess() {
    }

    // Constructor cho tin nhắn mã hóa
    public Mess(String maKH, String maNV, String fullname, String encryptedAESKey, String thoiGianGui, String encryptedMessage, Boolean fromCustomer) {
        this.maKH = maKH;
        this.maNV = maNV;
        this.fullname = fullname;
        this.encryptedAESKey = encryptedAESKey;  // Lưu trữ khóa AES đã mã hóa
        this.thoiGianGui = thoiGianGui;
        this.encryptedMessage = encryptedMessage;
        this.fromCustomer = fromCustomer;
    }

    // Constructor cho tin nhắn chưa mã hóa
    public Mess(String maKH, String noiDung2, String noiDung1, String thoiGianGui) {
        this.maKH = maKH;
        this.noiDung2 = noiDung2;
        this.thoiGianGui = thoiGianGui;
        this.noiDung1 = noiDung1; // Tin nhắn đã nhận
    }

    // Getter và Setter cho các trường mới
    public String getEncryptedMessage() {
        return encryptedMessage;
    }

    public void setEncryptedMessage(String encryptedMessage) {
        this.encryptedMessage = encryptedMessage;
    }

    public String getDecryptedMessage() {
        return decryptedMessage;
    }

    public void setDecryptedMessage(String decryptedMessage) {
        this.decryptedMessage = decryptedMessage;
    }

    // Getter và Setter cho khóa AES đã mã hóa
    public String getEncryptedAESKey() {
        return encryptedAESKey;
    }

    public void setEncryptedAESKey(String encryptedAESKey) {
        this.encryptedAESKey = encryptedAESKey;
    }

    // Các phương thức khác
    public boolean isFromCustomer() {
        return fromCustomer;
    }

    public void setFromCustomer(boolean fromCustomer) {
        this.fromCustomer = fromCustomer;
    }

    public boolean isAutoReply() {
        return isAutoReply;
    }

    public void setAutoReply(boolean autoReply) {
        isAutoReply = autoReply;
    }

    public String getMaTN() {
        return maTN;
    }

    public void setMaTN(String maTN) {
        this.maTN = maTN;
    }

    public String getThoiGianGui() {
        return thoiGianGui;
    }

    public void setThoiGianGui(String thoiGianGui) {
        this.thoiGianGui = thoiGianGui;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getNoiDung2() {
        return noiDung2;
    }

    public void setNoiDung2(String noiDung2) {
        this.noiDung2 = noiDung2;
    }

    public String getNoiDung1() {
        return noiDung1;
    }

    public void setNoiDung1(String noiDung1) {
        this.noiDung1 = noiDung1;
    }

    @Override
    public String toString() {
        return "Mess{" +
                "maTN='" + maTN + '\'' +
                ", noiDung1='" + noiDung1 + '\'' +
                ", thoiGianGui='" + thoiGianGui + '\'' +
                ", maKH='" + maKH + '\'' +
                ", noiDung2='" + noiDung2 + '\'' +
                ", encryptedAESKey='" + encryptedAESKey + '\'' +
                ", encryptedMessage='" + encryptedMessage + '\'' +
                '}';
    }

    // Phương thức đọc đối tượng từ Parcel
    protected Mess(Parcel in) {
        maTN = in.readString();
        noiDung1 = in.readString();
        thoiGianGui = in.readString();
        maKH = in.readString();
        noiDung2 = in.readString();
        encryptedAESKey = in.readString();
        encryptedMessage = in.readString();
        decryptedMessage = in.readString(); // Đọc tin nhắn đã giải mã từ Parcel
    }

    public static final Creator<Mess> CREATOR = new Creator<Mess>() {
        @Override
        public Mess createFromParcel(Parcel in) {
            return new Mess(in);
        }

        @Override
        public Mess[] newArray(int size) {
            return new Mess[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(maTN);
        dest.writeString(noiDung1);
        dest.writeString(thoiGianGui);
        dest.writeString(maKH);
        dest.writeString(noiDung2);
        dest.writeString(encryptedAESKey);  // Lưu trữ khóa AES đã mã hóa vào Parcel
        dest.writeString(encryptedMessage); // Lưu trữ tin nhắn đã mã hóa
        dest.writeString(decryptedMessage); // Lưu trữ tin nhắn đã giải mã
    }
}
