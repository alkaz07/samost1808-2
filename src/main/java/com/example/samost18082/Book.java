package com.example.samost18082;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Book {
    private SimpleIntegerProperty isbn = new SimpleIntegerProperty();
    private SimpleStringProperty title = new SimpleStringProperty();
    private SimpleIntegerProperty year = new SimpleIntegerProperty();

    public Book(int isbn, String title, int year) {
        this.isbn.set(isbn);
        this.title.set(title);
        this.year.set(year);
    }

    public int getIsbn() {
        return isbn.get();
    }

    public SimpleIntegerProperty isbnProperty() {
        return isbn;
    }

    public void setIsbn(int isbn) {
        this.isbn.set(isbn);
    }

    public String getTitle() {
        return title.get();
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public int getYear() {
        return year.get();
    }

    public SimpleIntegerProperty yearProperty() {
        return year;
    }

    public void setYear(int year) {
        this.year.set(year);
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn=" + isbn.get() +
                ", title=" + title.get() +
                ", year=" + year.get() +
                '}';
    }
}
