package cn.lactorsj.sparrowlib.entity;


public class User {
    public int id;
    public String username;
    public String password;


    public User(){

    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
