package com.example.e_library.book;

import java.io.Serializable;
import java.sql.Timestamp;

public class BookVO implements Serializable {
    String bookId;
    String bookName;
    String writer;
    String publisher;
    String publiDate;
    String status;

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

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPubliDate() {
        return publiDate;
    }

    public void setPubliDate(String publiDate) {
        this.publiDate = publiDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
