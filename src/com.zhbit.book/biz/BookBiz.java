package biz;

import bean.Book;
import bean.Type;
import dao.BookDao;
import dao.TypeDao;

import java.sql.SQLException;
import java.util.List;

public class BookBiz {
    //需要一个BookDao对象
    BookDao bookDao = new BookDao();

    public List<Book> getBooksByTypeId(long typeId){  //根据编号获取书籍
        try {
            return bookDao.getBooksByTypeId(typeId);
        } catch (SQLException throwables) {
            throwables.printStackTrace();//throw new RuntimeException(e);
            return null;
        }
    }

    public int add(long typeId, String name, double price, String desc, String pic, String publish, String author, long stock, String address){
        int count = 0;
        try {
            count = bookDao.add(typeId, name, price, desc, pic, publish, author, stock, address);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    public int modify(long id, long typeId, String name, double price, String desc, String pic, String publish, String author, long stock, String address){
        int count = 0;
        try {
            count = bookDao.modify(id, typeId, name, price, desc, pic, publish, author, stock, address);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return count;
}

    public int remove(long id){
        int count = 0;
        try {
            count = bookDao.remove(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

//    public List<Book> getByPage(int pageIndex, int pageSize){
//        TypeDao typeDao = new TypeDao();   //建立一个typeDao对象，方便调用里面的一些方法
//        List<Book> books = null;
//        try {
//            books = bookDao.getByPage(pageIndex, pageSize);
//            //处理type对象的数据问题
//            for(Book book : books){
//                long typeId = book.getTypeId();
//                book.getTypeId();     //此时这里还是个null
//                //根据typeId找到对应的type对象
//                Type type = typeDao.getById(typeId);
//                //设置给book.setType()
//                book.setType(type);  //完成每一个book对象的外键的设置
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return books;
//    }

//    public Book getById(long id){
//        try {
//            return bookDao.getById(id);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    /**
     * 由行数算页数
     * @return
     */
//    public int getPageCount(int pageSize) {   //注意参数pageSize，有了pageSize才能知道能得到多少页
//        int pageCount = 0;
//        try {
//            //1、获取行数
//            int rowCount = bookDao.getCount(); //先得到所有的行数
//            //2、根据行数获取页数，每页多少条
//            pageCount = (rowCount - 1) / pageSize + 1;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return pageCount;
//    }
}
