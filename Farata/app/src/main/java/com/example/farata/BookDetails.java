package com.example.farata;

public class BookDetails {
    public String bookname, url;

    public BookDetails(String bookname, String url) {
        this.bookname = bookname;
        this.url = url;
    }

    public BookDetails() {}


    public String getBookname() {
        return this.bookname;
    }

    public String getUrl() {
        return this.url;
    }

}
