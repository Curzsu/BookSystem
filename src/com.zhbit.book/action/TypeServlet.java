package action;

import bean.Type;
import biz.TypeBiz;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/type.let")
public class TypeServlet extends HttpServlet {
    TypeBiz typeBiz = new TypeBiz();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * /type.let?type=add:添加(表单)
     * /type.let?type=modifypre&id=xx:(修改的预备界面(不含表单，数据保存到request当中->request转发到type_modify.jsp))
     * /type.let?type=modify:修改(表单)
     * /type.let?type=remove&id=xx:删除(从地址栏上获取删除的类型编号)
     *
     * /type_list.jsp:查询(所有的类型数据:当项目运行时(涉及监听器)，数据读放到application对象中)
     * (上面不用/type.let?type=query，直接查jsp就可以了)
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    /*补充知识点：
    * 关于一个servlet，能够存信息的作用域如下：(以下从小到大排序)
    * request:只能在同一个请求对象里面传输信息(如果只是当前有个功能/业务要完成，那就放到request里)
    * session:能在同一个会话(用户)里传输信息
    * application:能在同一个运行的项目里传输信息，因此它保存数据的范围会更大些，开发中，
    * 把经常用到，且不怎么发生变化的数据放入application中
    * */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1、设置编码
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        // 2、获取各种对象
        PrintWriter out= resp.getWriter();   //打印对象必须要在设置编码之后
        ServletContext application = req.getServletContext(); //获取application对象
        // 3、根据用户的需求完成业务
        String type = req.getParameter("type");  //先拿到用户的type
        switch (type){
            case "add":
                add(req, resp, out, application);
                break;

            case "modifypre":
                modifyPre(req, resp, out, application);
                break;

            case "modify":
                modify(req, resp, out, application);
                break;

            case "remove":
                remove(req, resp, out, application);
                break;
        }

    }
    /**
     * '
     * @param req
     * @param resp
     * @param out
     * @param application
     */
    private void remove(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) {
        // 1、获取需要删除的id
            long id = Long.parseLong(req.getParameter("id"));

        // 2、调用方法，biz异常的处理
        /*如果异常被捕获了，说明删了不该删的类型*/
        try {
            int count = typeBiz.remove(id);
            if(count > 0){
                List<Type> types = typeBiz.getAll();
                application.setAttribute("types",types);
                out.println("<script>alert('删除成功');location.href='type_list.jsp';</script>");
            }
            else{
                out.println("<script>alert('删除失败');location.href='type_list.jsp';</script>");
            }
        } catch (Exception e) {
           out.println("<script>alert('"+e.getMessage()+"');location.href='type_list.jsp';</script>");   //如果删了不该被删的
        }
        // 3、更新application

        // 4.提示结果


    }

    /**
     * type_modify.jsp --> type.let?type=modify --> type_list.jsp
     * @param req
     * @param resp
     * @param out
     * @param application
     */
    private void modify(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) {
        // 1、获取表单中的数据(id:hidden,name,parentId)
        long id = Long.parseLong(req.getParameter("typeId"));
        String name = req.getParameter("typeName");
        long parentId = Long.parseLong(req.getParameter("parentType"));

        // 2、调用biz的修改方法
        int count = typeBiz.modify(id, name, parentId);

        // 3、更新application
        if(count > 0){   //大于0说明添加成功
            List<Type> types = typeBiz.getAll(); //得到一个最新的数据
            application.setAttribute("types", types);  //把以前的相同名字的type用现在的最新的type换掉
            out.println("<script>alert('修改成功');location.href = 'type_list.jsp';</script>");
        }
        else{
            out.println("<script>alert('修改失败');location.href = 'type_list.jsp';</script>");
        }

        // 4、提示信息
    }

    /* 这里的jsp跨页面有两种方式，一种叫重定向，一种叫转发。
    * 重定向会带来新的请求
    * 所以这里用转发方式的话，就不会带来新的请求
    * 因为转发就是把信息转到请求对象里面
    * 要在跳转入的jsp页面中再把请求中的信息拿出来
    * 因此要保证type.let和Typemodify.jsp这两个是同一个请求
    * */
    /**
     * type_list.jsp ---页面点击修改按钮后---> type.let?type=modifypre&id=xx ---转发---> type_modify.jsp
     * @param req
     * @param resp
     * @param out
     * @param application
     */
    private void modifyPre(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) throws ServletException, IOException {
        //1、获取需要修改type对象的id
        long id = Long.parseLong(req.getParameter("id"));  //拿到传过来的id对象
        //2、根据id获取type对象
        Type type = typeBiz.getById(id);
        /*为什么这边是存到req中呢?因为这里是在同一个功能完成的，不需要很大的作用域(如application，session)，所以req是首选*/
        //3、把type存到req中
        req.setAttribute("type", type); //先把信息存到request里头
        req.getRequestDispatcher("type_modify.jsp").forward(req, resp);//页面的转发过程,通过forward把请求和响应对象一起带过去
    }

    /**
     * 用户首先进入到type_add.jsp
     * 当他点击添加后，页面就会跳转到type.let?type=add，通过这个页面就回到大servlet
     * 如果跳转成功，那么就会进入到type_list.jsp界面
     * 如果跳转失败，就会再次回到type_add.jsp页面
     *
     * @param out
     * @param application
     */
    private void add(HttpServletRequest req, HttpServletResponse resp, PrintWriter out, ServletContext application) {
        //1、从表单中获取名字和父类型
        //获取表单元素
        String typeName = req.getParameter("typeName");
        long parentId = Long.parseLong(req.getParameter("parentType"));//因为获取的参数是一个字符串，所以这里要转成long类型
        //2、调用biz添加方法
        int count = typeBiz.add(typeName, parentId);

        //3、更新application中的types
        if(count > 0){   //大于0说明添加成功
            List<Type> types = typeBiz.getAll(); //得到一个最新的数据
            application.setAttribute("types", types);  //把以前的相同名字的type用现在的最新的type换掉
            out.println("<script>alert('添加成功');location.href = 'type_list.jsp';</script>");
        }
        else{
            out.println("<script>alert('添加失败');location.href = 'type_add.jsp';</script>");
        }
        //4、提示结果

    }
}