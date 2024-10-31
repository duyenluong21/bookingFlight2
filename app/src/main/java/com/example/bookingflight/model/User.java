package com.example.bookingflight.model;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class User implements Parcelable {
    private String email ;
    private String password;
    private String salt; // Thêm trường salt vào lớp User
    private String fullname;
    private String ngaySinh;
    private String maTK;
    private String maKH;
    private String gioiTinh;
    private String soCCCD;
    private String diaChi;
    private String soDT;
    private String loaiHanhKhach;
    private String ngayDangKy;

    public String getNgayDangKy() {
        return ngayDangKy;
    }

    public void setNgayDangKy(String ngayDangKy) {
        this.ngayDangKy = ngayDangKy;
    }

    public User(Parcel in) {
        maKH = in.readString();
        email = in.readString();
        password = in.readString();
        fullname = in.readString();
        ngaySinh = in.readString();
        maTK = in.readString();
        maKH = in.readString();
        gioiTinh = in.readString();
        soCCCD = in.readString();
        diaChi = in.readString();
        soDT = in.readString();
        loaiHanhKhach = in.readString();
        ngayDangKy = in.readString();
    }

    public User(String maKH, String fullname, String email, String ngaySinh, String gioiTinh, String diaChi, String soDT) {
        this.maKH = maKH;
        this.fullname = fullname;
        this.email = email;
        this.gioiTinh = gioiTinh;
        this.ngaySinh = ngaySinh;
        this.diaChi = diaChi;
        this.soDT = soDT;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(String ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getNgaysinh() {
        return ngaySinh;
    }

    public void setNgaysinh(String ngaysinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getMaTK() {
        return maTK;
    }

    public void setMaTK(String maTK) {
        this.maTK = maTK;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSoCCCD() {
        return soCCCD;
    }

    public void setSoCCCD(String soCCCD) {
        this.soCCCD = soCCCD;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDT() {
        return soDT;
    }

    public void setSoDT(String soDT) {
        this.soDT = soDT;
    }

    public String getLoaiHanhKhach() {
        return loaiHanhKhach;
    }

    public void setLoaiHanhKhach(String loaiHanhKhach) {
        this.loaiHanhKhach = loaiHanhKhach;
    }

    @Override
    public String toString() {
        return "User{" +
                "maKH='" + maKH + '\'' +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", salt='" + salt + '\'' +
                ", fullname='" + fullname + '\'' +
                ", ngaySinh='" + ngaySinh + '\'' +
                ", maTK='" + maTK + '\'' +
                ", maKH='" + maKH + '\'' +
                ", gioiTinh='" + gioiTinh + '\'' +
                ", soCCCD='" + soCCCD + '\'' +
                ", diaChi='" + diaChi + '\'' +
                ", soDT='" + soDT + '\'' +
                ", loaiHanhKhach='" + loaiHanhKhach + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(maKH);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(fullname);
        dest.writeString(ngaySinh);
        dest.writeString(maTK);
        dest.writeString(maKH);
        dest.writeString(gioiTinh);
        dest.writeString(soCCCD);
        dest.writeString(diaChi);
        dest.writeString(soDT);
        dest.writeString(loaiHanhKhach);
        dest.writeString(ngayDangKy);
    }

    public String getSalt() {
        // Nếu salt đã được thiết lập, trả về nó
        if (salt != null) {
            return salt;
        }

        // Nếu salt chưa được thiết lập, tạo một salt mới
        SecureRandom random = new SecureRandom();
        byte[] newSalt = new byte[32];
        random.nextBytes(newSalt);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Base64.getEncoder().encodeToString(newSalt);
        }
        return null;
    }


    // Phương thức băm mật khẩu với salt
    private String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest((password + salt).getBytes());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return Base64.getEncoder().encodeToString(hash);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Rest of the class

}
