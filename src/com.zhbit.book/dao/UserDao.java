package dao;

/*用户表的数据操作对象*/

import bean.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import util.DBHelper;

import java.sql.Connection;
import java.sql.SQLException;

public class UserDao {
    //创建QueryRunner对象(JDBC-->DBUtils)
    QueryRunner runner = new QueryRunner();

    public User getUser(String name, String pwd) throws SQLException {

            // 1.调用DBHelper获取连接对象
            Connection conn = DBHelper.getConnection();

            // 2.准备执行的sql语句
            String sql="select * from user where name = ? and pwd = ? and state = 1";

            // 3.调用查询方法,将查询的数据封装成User对象
        /*BeanHandler<User>(User.class)，
         就是把数据(比如一个用户的id,name,pwd,state四列)封装成一个user对象并返回
         BeanHandler代表着一个javabean对象*/
            User user = runner.query(conn,sql,new BeanHandler<User>(User.class),name,pwd);

            // 4.关闭连接对象
            conn.close();

            // 5.返回user
            return user;
    }

    /**
     * 修改密码
     * @param id 需要修改密码的用户编号
     * @param pwd 新的密码
     * @return 修改的数据行
     */
    public int modifyPwd(long id, String pwd) throws SQLException {
        String sql = "update  user set pwd = ? where id =?";
        Connection conn = DBHelper.getConnection();
        int count = runner.update(conn, sql, pwd, id);   //参数根据"?"顺序来给
        DBHelper.close(conn);
        return count;
    }


    /**
     * 这个方法的目的是用来测试是否能正场输出user的数据
     * @param args
     */
    public static void main(String[] args) {
        User user = null;
        try {
            user = new UserDao().getUser("super", "123");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(user);
    }

}
