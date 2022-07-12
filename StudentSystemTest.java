package GUI.experienment;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.regex.Pattern;

/*
学生信息管理系统基本介绍：
    1、学生有学号、姓名、专业、性别和出生日期信息，界面中包含“确定”和“重置”按钮。
    2、功能菜单里有对学生信息的增删改查功能，单击“确定”按钮将学生信息提交到数据库中，
点击“重置”按钮清空界面中用户输入的信息。
点击关闭按钮退出程序并断开数据库的连接。
 */

/*
建表（studenttable）语句：

create table studentTable(
        sId varchar(20) primary key,
        name varchar(50) not null,
        major varchar(50) not null,
        sex varchar(10) not null,
        birthday varchar(30) not null
)ENGINE = InnoDB DEFAULT CHARSET=utf8;

 */

public class StudentSystemTest {
    public static void main(String[] args) {
        new StuMangerSys();
    }
}
class StuMangerSys extends JFrame {
    private JPanel jPanel;
    private JMenu menu;
    private JMenuBar jMenuBar;
    private JMenuItem item1,item2,item3,item4;
    private JButton jButton1,jButton2;
    private JLabel jLabel1,jLabel2,jLabel3,jLabel4,jLabel5;
    private JTextField text1,text2,text3,text4,text5;
    private String func; //功能菜单中对应功能的指令语句。
    private static Connection connection = null;
    private static PreparedStatement preparedStatement = null;
    private static ResultSet resultSet = null, resultSet2 = null;

    //数据库连接
    static {
        try {
            //注册驱动
            String driveName = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/javadbtest";
            String user = "root";
            String password = "1234";
            Class.forName(driveName);
            //驱动建立连接
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("MySQL数据库连接成功");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public StuMangerSys(){
        super("学生信息管理系统");
        jPanel = new JPanel();
        jPanel.setLayout(null);
        jMenuBar = new JMenuBar();
        jMenuBar.setBounds(5,10,70,20);
        menu = new JMenu("功能菜单");
        item1=new JMenuItem("添加学生");
        item2=new JMenuItem("删除学生");
        item3=new JMenuItem("修改学生");
        item4=new JMenuItem("查询学生");
        menu.add(item1);
        menu.add(item2);
        menu.add(item3);
        menu.add(item4);
        jMenuBar.add(menu);
        jPanel.add(jMenuBar);

        jLabel1 = new JLabel("学号:");
        jLabel1.setBounds(150,40,60,30);
        jPanel.add(jLabel1);
        text1 = new JTextField("",30);
        text1.setBounds(210,40,100,30);
        jPanel.add(text1);

        jLabel2 = new JLabel("姓名:");
        jLabel2.setBounds(150,80,60,30);
        jPanel.add(jLabel2);
        text2 = new JTextField("",30);
        text2.setBounds(210,80,100,30);
        jPanel.add(text2);

        jLabel3 = new JLabel("专业:");
        jLabel3.setBounds(150,120,60,30);
        jPanel.add(jLabel3);
        text3 = new JTextField("",30);
        text3.setBounds(210,120,100,30);
        jPanel.add(text3);

        jLabel4 = new JLabel("性别:");
        jLabel4.setBounds(150,160,60,30);
        jPanel.add(jLabel4);
        text4 = new JTextField("男",30);
        text4.setBounds(210,160,100,30);
        jPanel.add(text4);

        jLabel5 = new JLabel("出生日期:");
        jLabel5.setBounds(150,200,60,30);
        jPanel.add(jLabel5);
        text5 = new JTextField("2001-03-04",30);
        text5.setBounds(210,200,100,30);
        jPanel.add(text5);

        jButton1 = new JButton("确定");
        jButton1.setBounds(110,300,80,35);
        jPanel.add(jButton1);

        jButton2 = new JButton("重置");
        jButton2.setBounds(280,300,80,35);
        jPanel.add(jButton2);

        //Listener
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"请输入要添加的学生学号，姓名，专业，性别，出生日期！以确定键提交信息","提示",JOptionPane.INFORMATION_MESSAGE);
                setFunc("insert");
            }
        });
        //
        item2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"请输入要删除的学生信息，只需输入学号，以确定键提交请求","提示",JOptionPane.INFORMATION_MESSAGE);
                setFunc("delete");
            }
        });

        item3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"请输入要修改的学生的学号，只能修改姓名，专业，性别，出生日期！以确定键提交修改信息","提示",JOptionPane.INFORMATION_MESSAGE);
                setFunc("modify");
            }
        });
//
        item4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,"请输入要查询的学生的学号，仅需输入学号！以确定键提交查询请求","提示",JOptionPane.INFORMATION_MESSAGE);
                setFunc("query");
            }
        });
//      确认提交按钮设置以及处理对应的功能
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = getFunc();
                if (s == null){
                    JOptionPane.showMessageDialog(null,"请先选择左侧功能菜单的选项来提交您需要的功能","提示信息",JOptionPane.INFORMATION_MESSAGE);
                }
                else if (s.equals("insert")){
                    insertfunc();
                } else if (s.equals("delete")){
                    deletefunc();
                } else if (s.equals("modify")){
                    modifyfunc();
                } else if (s.equals("query")){
                    queryfunc();
                }
            }
        });

//        重置按钮
        jButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                text1.setText("");
                text2.setText("");
                text3.setText("");
                text4.setText("");
                text5.setText("");
                JOptionPane.showMessageDialog(null,"重置完成！","结果信息",JOptionPane.INFORMATION_MESSAGE);
            }
        });

        //窗口关闭操作以及关闭流的操作
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (resultSet!=null) resultSet.close();
                    if (preparedStatement!=null) preparedStatement.close();
                    if (connection!=null) connection.close();
                    System.exit(0);
                }catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
//            系统启动程序时弹出欢迎界面
            @Override
            public void windowOpened(WindowEvent e) {
                JOptionPane.showMessageDialog(null,"欢迎来到学生信息管理系统！" +
                        "请在功能菜单选择操作后再进行提交确认按钮","提示信息",JOptionPane.INFORMATION_MESSAGE);

            }
        });
        //设置窗体属性
        this.add(jPanel);
        this.setSize(500,600);
        this.setLocation(220,140);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }
    //插入功能
    public void insertfunc(){
        String sID = text1.getText();
        String name = text2.getText();
        String major = text3.getText();
        String sex = text4.getText();
        String birthday = text5.getText();

        try {
            // 先判断数据库中是否已经存在这名学生
            preparedStatement = connection.prepareStatement("select sId from studenttable");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                if (resultSet.getString(1).equals(sID)){
                    JOptionPane.showMessageDialog(null,"该学生已存在于数据库中，无需重复输入"
                            ,"查询结果",JOptionPane.INFORMATION_MESSAGE);
                }
            }

//            判断输入的数据是否合法，符合逻辑
            if (islegal() && iscorsex()){
//                如果符合，则执行插入操作
            String sql = "insert into studenttable values (?,?,?,?,?)";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1,sID);
            preparedStatement.setString(2,name);
            preparedStatement.setString(3,major);
            preparedStatement.setString(4,sex);
            preparedStatement.setString(5,birthday);
            preparedStatement.executeUpdate();
            JOptionPane.showMessageDialog(null,"您已经成功录入该学生！","结果信息",JOptionPane.INFORMATION_MESSAGE);
            }
        }catch (SQLException es) {
            es.printStackTrace();
        }
    }
//修改学生操作
    public boolean modifyfunc(){
        try {
            String id = text1.getText();
            String name = text2.getText();
            String major = text3.getText();
            String sex = text4.getText();
            String birthday = text5.getText();

            // 先判断数据库中是否已经存在这名学生
            preparedStatement = connection.prepareStatement("select sId from studenttable");
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                // 如果存在这名学生（根据他的学号来判断）
                if (resultSet.getString(1).equals(id)){

                    // 如果管理员重新输入这名学生的姓名，则进行姓名的修改，如果没有输入（输入为空），则不修改该学生的姓名信息。
                    if (!name.isEmpty()){
                        preparedStatement = connection.prepareStatement("update studenttable set name = ? where sId = ?");
                        preparedStatement.setString(1,name);
                        preparedStatement.setString(2,id);
                        preparedStatement.executeUpdate();
                    }

                    // 如果管理员重新输入这名学生的专业，则进行专业的修改，如果没有输入（输入为空），则不修改该学生的专业信息。
                    if (!major.isEmpty()){
                        preparedStatement = connection.prepareStatement("update studenttable set major = ? where sId = ?");
                        preparedStatement.setString(1,major);
                        preparedStatement.setString(2,id);
                        preparedStatement.executeUpdate();
                    }

                    // 如果管理员重新输入这名学生的性别，则进行性别的修改，如果没有输入（输入为空），则不修改该学生的性别信息。
                    if (!sex.isEmpty()){
                        preparedStatement = connection.prepareStatement("update studenttable set sex = ? where sId = ?");
                        preparedStatement.setString(1,sex);
                        preparedStatement.setString(2,id);
                        preparedStatement.executeUpdate();
                    }

                    // 如果管理员重新输入这名学生的出生日期，则进行出生日期的修改，如果没有输入（输入为空），则不修改该学生的出生日期信息。
                    if (!birthday.isEmpty()){
                        preparedStatement = connection.prepareStatement("update studenttable set birthday = ? where sId = ?");
                        preparedStatement.setString(1,birthday);
                        preparedStatement.setString(2,id);
                        preparedStatement.executeUpdate();
                    }
                    // 统一修改完成，弹出提示信息
                    JOptionPane.showMessageDialog(null,"修改成功！","提示信息",JOptionPane.INFORMATION_MESSAGE);
                    return true;
                }
            }
            // 要修改的学生不存在时，输出提示
            JOptionPane.showMessageDialog(null,"您要修改的学生不存在！请重新输入","提示信息",JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException troubles) {
            troubles.printStackTrace();
        }
        return false;
    }
//删除学生功能
    public boolean deletefunc(){
        try {
        String id = text1.getText();
            // 先判断数据库中是否已经存在这名学生
        preparedStatement = connection.prepareStatement("select sId from studenttable");
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            if (resultSet.getString(1).equals(id)){
                preparedStatement = connection.prepareStatement("delete from studenttable where sId = ?");
                preparedStatement.setString(1,id);
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null,"删除成功！","提示信息",JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }
        JOptionPane.showMessageDialog(null,"您要删除的学生不存在！，请重新输入","提示信息",JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }

    public boolean queryfunc(){
        try {
        String id = text1.getText();
            // 先判断数据库中是否已经存在这名学生
        preparedStatement = connection.prepareStatement("select * from studenttable");
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            if (resultSet.getString(1).equals(id)){
                JOptionPane.showMessageDialog(null,"查询成功！该学生的信息如下：\n" +
                                "学号 ： " + resultSet.getString(1) + '\n' +
                                " 姓名 ：" + resultSet.getString(2)+ '\n' +
                                "专业 ： " + resultSet.getString(3) + '\n' +
                                "性别 ： " + resultSet.getString(4) + '\n' +
                                "出生日期 ： " + resultSet.getString(5) + '\n'
                        ,"查询结果",JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }
        JOptionPane.showMessageDialog(null,"您要查询的学生不存在！请重新输入","提示信息",JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
    // 判断数据是否符合逻辑的方法
    public boolean islegal(){
        if (!isNumeric(text1.getText())){
            JOptionPane.showMessageDialog(null,"输入不是数字，请重新输入学号！","提示信息",JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
//        判断输入框是否为空或者有不完整的信息，没有完整输入
        if (text1.getText().isEmpty()||text2.getText().isEmpty()||text3.getText().isEmpty()||text4.getText().isEmpty()||text5.getText().isEmpty())
        {
            JOptionPane.showMessageDialog(null,"输入信息不完整！","提示信息",JOptionPane.INFORMATION_MESSAGE);
            return false;
        }

        //判断出生日期是否在正常的日期规范标准，并限制输入格式
        if (!Pattern.compile("^[1,2]\\d{3}-(0[1-9]||1[0-2])" +
                "-(0[1-9]||[1,2][0-9]||3[0,1])$").matcher(text5.getText()).matches()
                || text5.getText().isEmpty()){
            JOptionPane.showMessageDialog(null,"输入的日期格式不合法，如XXXX-YY-DD，请重新输入！","提示信息",JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
        return true;
    }
    //判断输入的是否是数字
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public boolean iscorsex(){
        if ( text4.getText().equals("男") ) {
            return true;
        }
        if (text4.getText().equals("女")){
            return true;
        }
        else
        {
            JOptionPane.showMessageDialog(null,"只能输入“男、女”，请重新输入！","提示信息",JOptionPane.INFORMATION_MESSAGE);
            return false;
        }
    }

    //系统获取功能命令的方法
    public String getFunc() {
        return func;
    }

    //为系统设置功能命令的方法
    public void setFunc(String func) {
        this.func = func;
    }
}
