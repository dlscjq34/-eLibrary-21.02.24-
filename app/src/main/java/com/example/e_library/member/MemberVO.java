package com.example.e_library.member;

import java.io.Serializable;
import java.sql.Timestamp;

public class MemberVO implements Serializable {
    private String memberId;
    private String memberName;
    private String memberRole;
    private String pswd;
    private String addr;
    private String tel;
    private String regDate;
    int favorBook;
    int howManyLate;


    public MemberVO() {
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getMemberRole() {
        return memberRole;
    }

    public void setMemberRole(String memberRole) {
        this.memberRole = memberRole;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate;
    }

    public int getFavorBook() {
        return favorBook;
    }

    public void setFavorBook(int favorBook) {
        this.favorBook = favorBook;
    }

    public int getHowManyLate() {
        return howManyLate;
    }

    public void setHowManyLate(int howManyLate) {
        this.howManyLate = howManyLate;
    }
}
