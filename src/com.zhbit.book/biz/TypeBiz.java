package biz;

import bean.Book;
import bean.Type;
import dao.BookDao;
import dao.TypeDao;

import java.sql.SQLException;
import java.util.List;

public class TypeBiz {
    TypeDao typeDao = new TypeDao();
    public List<Type> getAll(){
        try {
            return typeDao.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int add(String name, long parentId){
        int count = 0;
        try {
            count = typeDao.add(name, parentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    public int modify(long id, String name, long parentId){
        int count = 0;
        try {
            count = typeDao.modify(id, name, parentId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return count;
    }

    /**
     * 删除
     * 表与表之间的关系是逻辑关系
     * type是book的一个外键
     * @param id
     * @return 0 删除失败 Exception: 提示用户的信息,把异常丢到servlet中
     */
    public int remove(long id) throws Exception {
        // 如果存在子项(也就是外键)是不能remove的
        BookDao bookDao = new BookDao();
        int count = 0;
        try {
            List<Book> books = bookDao.getBooksByTypeId(id);
            if(books.size() > 0){
                //不能删除
                throw new Exception("删除的类型有子信息，删除失败");
            }
            count = typeDao.remove(id);
            return count;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Type getById(long id){
        try {
            return typeDao.getById(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}