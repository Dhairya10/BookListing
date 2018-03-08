package com.example.dhairyakumar.booklisting;

public class Book
{
    private   String  author;
    private   String title;
    private String publisher;
    public Book(String  author, String title, String publisher)
    {
        this.author = author;
        this.title = title;
        this.publisher = publisher;
    }

    public String  getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getPublisher() {
        return publisher;
    }

}
