package dao;

import bean.Type;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import util.DBHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 */
public class TypeDao {
    //构建QueryRunner对象
    QueryRunner runner = new QueryRunner();

    /**
     * 添加图书类型方法
     * 主键id自增列
     * @param name
     * @param parentId
     * @return
     */
    /*对照数据库表，为什么add里面不用传参id呢？因为id被设置为了自动增长列，所以不用传参到add里*/
    public int add(String name, long parentId) throws SQLException {
        Connection conn = DBHelper.getConnection();
        String sql = "insert into type values(null, ?, ?)"; //注意，虽然id是自动增长列，但sql语句这还是要传一个null占位子，不然会出错
        /*增删改都可以用update语句，这里用update语句，是因为update方法没那么多要求，比较方便使用
        * 用insert()方法也行，但是调用这个方法需要传入ResultSetHandler，显然update方法
        * 比result方法更简单调用
        * */
                    //runner.insert();
        int count = runner.update(conn, sql, name, parentId);
        conn.close();   //记得关闭连接
        return count;
    }

    /**
     *获取所有类型
     * @return
     */
    public List<Type> getAll() throws SQLException {  //getAll，一次查询会查询出多个类型
        Connection conn = DBHelper.getConnection();
        String sql = "select id, name, parentId from type";
        /*上述sql语句查出来之后，将得到多个type数据，所以下面用一个集合来保存数据*/
        /*BeanListHandler因为会获取到多个数据，所以用BeanListHandler来存*/
        List<Type> types = runner.query(conn, sql, new BeanListHandler<Type>(Type.class));
        conn.close();
        return types;
    }

    /**
     * 根据类型编号获取类型对象
     * @param typeId
     * @return
     */
    public Type getById(long typeId) throws SQLException {   //在数据表中一行一行地获取对应type
        Connection conn = DBHelper.getConnection();
        String sql = "select id, name, parentId from type where id = ?";
        /*BeanHandler:表示把结果集中的一行数据,封装成一个对象,专门针对结果集中只有一行数据的情况。*/
        Type type = runner.query(conn, sql, new BeanHandler<Type>(Type.class), typeId);
        conn.close();
        return type;
    }

    /**
     * 修改图书类型
     * @param id 代表需要修改的类型编号
     * @param name
     * @param parentId
     * @return
     */
    public int modify(long id, String name, long parentId) throws SQLException {
        //类似add方法里的语句，差不多照搬即可
        Connection conn = DBHelper.getConnection();
        String sql = "update type set name = ?, parentId = ? where id = ?";
        int count = runner.update(conn, sql, name, parentId, id);
        conn.close();   //记得关闭连接
        return count;
    }

    /**
     * 删除图书类型
     * @param id
     * @return
     */
    public int remove(long id) throws SQLException {
        //类似add方法里的语句，差不多照搬即可
        Connection conn = DBHelper.getConnection();
        String sql = "delete from type where id=?";  //物理删除(即数据直接无法恢复)
        int count = runner.update(conn, sql, id);
        conn.close();   //记得关闭连接
        return count;
    }
    /*一下代码是用来的测试用的，用来测试是否前面成功地获取数据*/
    public static void main(String[] args) {
        List<Type> types = null;
        try {
            types = new TypeDao().getAll();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        System.out.println(types);
    }
}