package action;

import bean.User;
import biz.UserBiz;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/user.let")
public class UserServlet extends HttpServlet {
    //构建UserBiz的对象
    UserBiz userBiz = new UserBiz();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    /**
     * /user.let?type = login  ←登录地址会以这种格式进来，所以到时候判断后面是不是login即可
     * /user.let?type = exit 安全退出
     * /user.let?type = modifyPwd 修改密码
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    /*补充：上述方法注释中的“/”，表示的是项目根目录，与其它项目不同的是，在web项目中，根目录代表着前端资源*/
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();   //获取session
        /*设置字符编码格式，可以防止请求和响应中的中文乱码*/
        req.setCharacterEncoding("utf-8");  //设置字符编码格式
        resp.setContentType("text/html;charset=utf-8"); //设置字符编码格式
        PrintWriter out = resp.getWriter();  //out对象是响应当中的一个输出流的对象，可以帮助我们打印信息

        //1、判读用户请求的类型为login
        String method = req.getParameter("type");
switch (method){
    case "login":
        //2、从login.html中获取用户名，密码和验证码
        String name = req.getParameter("name");
        String pwd = req.getParameter("pwd");
        String userCode = req.getParameter("valcode");
        // 2.1 提取session中的验证码进行判断
        String code = session.getAttribute("code").toString();  //它是一个对象类型，要toString转成String类型，才能用String类型的code将其保存起来
        /*IgnoreCase,忽略大小写*/
        if(!code.equalsIgnoreCase(userCode)){//如果用户填写的验证码与所生成验证码不相同
            out.println("<script>alert('验证码错误'); location.href = 'login.html';</script>");
            return;  //错误，直接返回
        }

        //3、调用UserBiz的getUser方法，根据用户名和密码获取对应的用户对象
        User user = userBiz.getUser(name, pwd);

        //4、判断用户对象是否为null
        if(user == null){
        // 4.1 如果是null，就表示用户名或密码不正确，提示错误，回到登录界面
            /*用js代码实现提示和跳转↓*/
            out.println("<script>alert('用户名或密码不存在'); location.href = 'login.html';</script>");
        }
        else{
        // 4.2 非空，表示登陆成功，将用户对象保存在session中，提示登陆成功后，界面跳转到index.jsp
            session.setAttribute("user", user);  //如果用户登录成功，保存下用户的数据(user->object)
            out.println("<script>alert('登录成功'); location.href = 'index.jsp';</script>");
        }

        break;

    case "exit":
        // 1、清除session
        session.invalidate();   //使得当前存储的用户信息被全清理掉了

        // 2、跳转到login.html界面(框架中需要回去(意思就是不是单纯的页面跳转))
        //跳转过程：top.jsp->parent->index.jsp->login.html
        //parent，表示top.jsp的parent是index.jsp
        out.println("<script>parent.window.location.href='login.html';</script>");
        break;

    case "modifyPwd":   //修改密码
        // 1、获取用户输入的新密码
        String newPwd = req.getParameter("newpwd");  //newPwd就是jsp里定义的name
        String newPwd2 = req.getParameter("newpwd2");  //判断两次输入密码是否一致

        // 2、从session中获取用户的编号
        /*下面一句的意思：首先通过session.getAttribute("user")试图获取session中存有的user对象
        但之前user是以object类型保存在session里的，即“session.setAttribute("user", user);  //如果用户登录成功，保存下用户的数据(user->object)”这句
        所以要把这个object类型对象 向下转型 成User类型，就变成我们要获得的user对象了
        进而可以调用getId方法了
         */
        long id = ((User)session.getAttribute("user")).getId();  //拿到用户编号

        // 3、调用biz层方法
        int count = userBiz.modifyPwd(id, newPwd);  //方法会返回一个count

        // 4、响应-参考exit
        if(count > 0){  //count大于0说明修改成功
            out.println("<script>alert('密码修改成功');parent.window.location.href='login.html';</script>");
        }
        else{
            out.println("<script>alert('密码修改失败');</script>");
        }
        break;
}


    }
}
