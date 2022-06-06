package action;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;


/**
 * 生成验证码
 */
@WebServlet(urlPatterns = "/code.let",loadOnStartup = 1) //loadOnStartup = 1意思是，希望在服务器启动的时候，验证码就已经配置好了
public class ValCodeServlet extends HttpServlet {
        Random random =new Random();
    /**
     * 获取随机字符串
     * @return
     */
    private String getRandomStr(){
        String str="23456789ABCDEFGHJKMNPQRSTUVWXYZabcdefghgkmnopqrstuvwxyz";//1,0,l o容易让用户搞混，所以不加进去
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < 4; i++){   //生成随机四个字符
            int index = random.nextInt(str.length());  //随机数点去获取字符串str的下标
            char letter = str.charAt(index);  //charAt获取字符的下标，str.charAt(index)相当于C++的str[index]
            sb.append(letter);  //将拿到的新的字符追加到字符串sb末尾
        }
        return sb.toString();
    }

    /**
     * 获取背景颜色 0~ 255
     * @return
     */
    private Color getBackColor(){
        /*通过随机数点来获取RGB数值*/
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        return new Color(red,green,blue);

    }

    /**
     * 获取前景色
     * @param bgColor
     * @return
     */
    private Color getForeColor(Color bgColor){
        /*为什么要是相减的呢？因为前景色要和背景色有反差才能看的见*/
        int red = 255 - bgColor.getRed();   //这里的get方法来自java内置的Color类
        int green = 255 - bgColor.getGreen();
        int blue = 255 - bgColor.getBlue();
        return new Color(red,green,blue);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

       //1.设置响应格式为图片:jpg
        resp.setContentType("image/jpeg");
        //2.创建图片对象
        /*构建的是缓冲区图片，也就是在内存中的图片*/
        BufferedImage bufferedImage = new BufferedImage(80,30,BufferedImage.TYPE_INT_RGB);//TYPE_INT_RGB就是rgb三原色

        //3.获取画布对象
        /*东西得在画布对象上才能被画出来*/
        Graphics g  = bufferedImage.getGraphics();

        //4.设置背景颜色
        Color bgColor = getBackColor();  //拿到背景颜色
        g.setColor(bgColor);  //把bgColor给画布对象

        //5.画背景
        g.fillRect(0,0,80,30);   //从0,0开始填充起，用给的bgColor来填充80的宽度和30的高度

        //6.设置前景色
        Color foreColor = getForeColor(bgColor);
        g.setColor(foreColor);

        //设置字体
        g.setFont(new Font("黑体",Font.BOLD,26));

        //7.将随机字符串存到session*
        String randomStr = getRandomStr();   //拿到随机数
        HttpSession session = req.getSession();  //拿到Session
        session.setAttribute("code",randomStr);  //保存到code里面
        g.drawString(randomStr,10,28);//x就是左边间隔，y就是与最下面的间隔


        //8.噪点(30个白色正方形)
        /*验证码画噪点，原因是为了防止机器人直接扫描图片提取字符，所以要添加噪点来进行干扰*/
        for(int i = 0; i < 30; i++){
            g.setColor(Color.white);  //用白色画
            int x = random.nextInt(80); //噪点要画在宽80长30的长方形区域内
            int y = random.nextInt(30);
            g.fillRect(x,y,1,1);  //相当于画一些白色的小正方形
        }

        //9.将这个张内存的图片输出到响应流
        /*也就是将图片输出到客户端*/
        ServletOutputStream sos = resp.getOutputStream();  //获取响应流
        ImageIO.write(bufferedImage,"jpeg",sos); //把图片写到响应流里去
        //把bufferedImage以jpeg的格式写到响应流里面去
    }
}
