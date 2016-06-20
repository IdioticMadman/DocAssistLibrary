package net.ezbim.docassist.txt.bean;

/**
 * 表示一个文件实体
 **/
public class BookInfo {
    public int id;
    public String bookname;
    public int bookmark;//书签

    public BookInfo() {
    }

    public BookInfo(int id, String bookname, int bookmark) {
        this.id = id;
        this.bookname = bookname;
        this.bookmark = bookmark;
    }

}