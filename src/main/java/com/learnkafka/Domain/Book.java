package com.learnkafka.Domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Book {
    @NotNull
    Integer bookId;
    @NotBlank
    String bookName;
    @NotBlank
    String bookAuthor;

    public Book(Integer bookId, String bookName, String bookAuthor) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.bookAuthor = bookAuthor;
    }
}
