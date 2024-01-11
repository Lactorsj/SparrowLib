package cn.lactorsj.sparrowlib.entity;


public class User {
    public int id;
    public String username;
    public String password;
    public int status; // can borrow book(1) / already borrow a book(0)
    public int book; // the book that this user borrow

    public User() {

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.status = 1; //Can borrow book
        this.book = 0;
    }
}
