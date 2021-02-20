package com.example.e_library.common;

import java.sql.Timestamp;

public class DTO {
    String memberId;
    String rentId;
    String bookId;
    String bookName;
    String rentDate;
    String dueDate;
    String returnDate;
    String bookStatus;
    int lateDate;

    @Override
    public String toString() {
        return "DTO{" +
                "memberId='" + memberId + '\'' +
                ", rentId='" + rentId + '\'' +
                ", bookId='" + bookId + '\'' +
                ", bookName='" + bookName + '\'' +
                ", rentDate='" + rentDate + '\'' +
                ", dueDate='" + dueDate + '\'' +
                ", returnDate='" + returnDate + '\'' +
                ", bookStatus='" + bookStatus + '\'' +
                ", lateDate=" + lateDate +
                '}';
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getRentId() {
        return rentId;
    }

    public void setRentId(String rentId) {
        this.rentId = rentId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getRentDate() {
        return rentDate;
    }

    public void setRentDate(String rentDate) {
        this.rentDate = rentDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getBookStatus() {
        return bookStatus;
    }

    public void setBookStatus(String bookStatus) {
        this.bookStatus = bookStatus;
    }

    public int getLateDate() {
        return lateDate;
    }

    public void setLateDate(int lateDate) {
        this.lateDate = lateDate;
    }
}
