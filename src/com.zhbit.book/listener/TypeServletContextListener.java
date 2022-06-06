package listener;

import bean.Type;
import biz.TypeBiz;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.List;

@WebListener
public class TypeServletContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {//创建application
        //1、获取数据库当中所有的类型信息
        TypeBiz biz = new TypeBiz();
        List<Type> types = biz.getAll();

        //2、获取appliction对象
        ServletContext application = sce.getServletContext();//拿到ServletContext的接口的对象，名字叫applicatuon

        //3、将信息存在application对象中
        application.setAttribute("types", types); //将信息存到这个application后，当前的所有页面就可以直接用这个信息

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {//销毁application
        ServletContextListener.super.contextDestroyed(sce);
    }
}
