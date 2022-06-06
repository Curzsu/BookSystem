package dao;

import bean.Book;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import util.DBHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class BookDao{
    QueryRunner runner = new QueryRunner();

    /**
     * 根据类型返回对应的书籍信息
     * @param typeId
     * @return
     * @throws SQLException
     */
    public List<Book> getBooksByTypeId(long typeId) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from book where typeId = ?";
        List<Book> books = runner.query(conn, sql, new BeanListHandler<Book>(Book.class), typeId);
        conn.close();
        return books;
    }

    /**
     * main测试方法
     * @param args
     */
    public static void main(String[] args) {
        try {
            List<Book> books = new BookDao().getBooksByTypeId(14);  //查找数据表中不存在的类型14
            System.out.println(books);    //返回的是一个空串"[]",代表着books对象是有的，但是没有返回数据
            //因为表中是没有14这个类型的，所以数据传不进去
            books = new BookDao().getBooksByTypeId(2);
            System.out.println();
            System.out.println(books);    //此时就会返回对应的数据了，因为2在表中有对应的typeId
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    public int add(long typeId, String name, double price, String desc, String pic, String publish, String author, long stock, String address) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "insert into book (typeId, `name`, price, `desc`, pic, publish, author, stock, address) " +
                "values(?, ?, ?, ?, ?,?,?,?,?);";
        int count = runner.update(conn, sql, typeId, name, price, desc, pic, publish, author, stock, address);
        conn.close();
        return count;
    }

    public int modify(long id, long typeId, String name, double price, String desc, String pic, String publish, String author, long stock, String address) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "update book set typeId = ?, `name` = ?, price = ?, `desc`= ?,pic = ?,publish = ?,author=?, stock = ?,address = ? " +
                "where id = 12;";
        int count = runner.update(conn, sql, typeId, name, price, desc, pic, publish, author, stock, address);
        conn.close();
        return count;
    }

    public int remove(long id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "delete from book where id = ?;";
        int count = runner.update(conn, sql, id);
        conn.close();
        return count;


    }

    /*这是一个根据分页做的查询*/
    public List<Book> getByPage(int pageIndex, int pageSize) throws SQLException { //前者是当前在第几页，后者是每一页有多少条
        Connection conn = DBHelper.getConnection();
        String sql = "select * from book limit ?,?";
        int offset = (pageIndex - 1) * pageSize;  //计算偏移量（详情见数据库的查询信息search_book）
        List<Book> books = runner.query(conn, sql, new BeanListHandler<Book>(Book.class), offset, pageSize);
        conn.close();
        return books;
    }

    /*一个一个地查询*/
    public Book getById(long id) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select * from book where id = ?";
        Book book = runner.query(conn, sql, new BeanHandler<Book>(Book.class), id);
        conn.close();
        return book;
    }

    /*sql语句返回一行一列*/
    public int getCount() throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "select count(id) from book";
        Number data = runner.query(conn, sql, new ScalarHandler<>());  //java中，7中基本类型除了bool，都属于Number类型
        int count = data.intValue(); //通过data.intValue()把数据拿过来。数据最终需要转成int类型
        conn.close();
        return count;
    }

}