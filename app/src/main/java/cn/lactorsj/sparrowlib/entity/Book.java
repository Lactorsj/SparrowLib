package cn.lactorsj.sparrowlib.entity;

import java.util.ArrayList;

import cn.lactorsj.sparrowlib.R;

public class Book {
    public int id;
    // 名称
    public String name;
    // 描述
    public String author;
    // 价格
    public int isAvailable;
    // 大图的保存路径

    // 大图的资源编号
    public int pic;
    public String borrowBy;

    // 声明一个手机商品的名称数组



    private static String[] mNameArray = {
            "C Primer Plus (Developer's Library) 6th Edition",
            "The Software Engineer's Guidebook",
            "Fluent Python 2nd Edition",
            "R Markdown Cookbook",
            "Introduction to Algorithms 4th Edition",
            "Game Programming Patterns"
    };
    // 声明一个手机商品的描述数组
    private static String[] mAuthorArray = {
            "Stephen Prata",
            "Gergely Orosz",
            "Luciano Ramalho",
            "Yihui Xie, Christophe Dervieux, Emily Riederer",
            "Thomas H. Cormen, Charles E. Leiserson, …(2 more)",
            "Robert Nystrom"
    };

    private static int[] mPicArray = {
            R.drawable.c_primer_plus,
            R.drawable.the_software_engineers_guidebook,
            R.drawable.fluent_python,
            R.drawable.r_markdown_cookbook,
            R.drawable.introduction_to_algorithm,
            R.drawable.game_programming_patterns
    };

    // 获取默认的信息列表
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
