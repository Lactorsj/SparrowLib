package cn.lactorsj.sparrowlib.entity;

import java.util.ArrayList;

import cn.lactorsj.sparrowlib.R;

public class Book {
    public int id;
    public String name;
    public String author;
    public int isAvailable; // is the book in the library(1) or borrowed(0)
    public int pic; // the index in the mPicArray
    public String borrowBy; // the username whom borrow the book

    private static final String[] mNameArray = {
            "C Primer Plus (Developer's Library) 6th Edition",
            "The Software Engineer's Guidebook",
            "Fluent Python 2nd Edition",
            "R Markdown Cookbook",
            "Introduction to Algorithms 4th Edition",
            "Game Programming Patterns"
    };
    private static final String[] mAuthorArray = {
            "Stephen Prata",
            "Gergely Orosz",
            "Luciano Ramalho",
            "Yihui Xie, Christophe Dervieux, Emily Riederer",
            "Thomas H. Cormen, Charles E. Leiserson, …(2 more)",
            "Robert Nystrom"
    };

    private static final int[] mPicArray = {
            R.drawable.c_primer_plus,
            R.drawable.the_software_engineers_guidebook,
            R.drawable.fluent_python,
            R.drawable.r_markdown_cookbook,
            R.drawable.introduction_to_algorithm,
            R.drawable.game_programming_patterns
    };

    public static ArrayList<Book> getDefaultList() {
        ArrayList<Book> list = new ArrayList<>();
        for (int i = 0; i < mNameArray.length; i++) {
            Book book = new Book();
            book.id = i;
            book.name = mNameArray[i];
            book.author = mAuthorArray[i];
            book.isAvailable = 1;
            book.pic = mPicArray[i];
            list.add(book);
        }
        return list;
    }
}
