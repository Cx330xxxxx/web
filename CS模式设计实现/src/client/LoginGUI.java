package client;

import common.Message;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class LoginGUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton resetButton;
    private JLabel statusLabel;
    private JLabel connectionLabel;
    private Client client;
    
    // 数据显示窗口
    private JFrame dataFrame;
    private JTable dataTable;
    private DefaultTableModel tableModel;
    
    public LoginGUI(Client client) {
        this.client = client;
        initLoginComponents();
        setupListeners();
    }
    
    private void initLoginComponents() {
        setTitle("客户端登录系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 连接状态面板
        connectionLabel = new JLabel("等待连接...", JLabel.CENTER);
        connectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        mainPanel.add(connectionLabel, BorderLayout.NORTH);
        
        // 登录面板
        JPanel loginPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        loginPanel.setBorder(BorderFactory.createTitledBorder("用户登录"));
        
        loginPanel.add(new JLabel("用户名:"));
        usernameField = new JTextField(20);
        loginPanel.add(usernameField);
        
        loginPanel.add(new JLabel("密码:"));
        passwordField = new JPasswordField(20);
        loginPanel.add(passwordField);
        
        loginButton = new JButton("登录");
        resetButton = new JButton("重置");
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(resetButton);
        buttonPanel.add(loginButton);
        loginPanel.add(new JLabel(""));
        loginPanel.add(buttonPanel);
        
        // 状态面板
        statusLabel = new JLabel("请登录系统", JLabel.CENTER);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // 添加到主面板
        mainPanel.add(loginPanel, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    // 初始化数据显示界面（登录成功后调用）
    private void initDataFrame() {
        dataFrame = new JFrame("数据库内容");
        dataFrame.setSize(700, 500);
        dataFrame.setLocationRelativeTo(null);
        dataFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // 标题标签
        JLabel titleLabel = new JLabel("用户/127.0.0.1连接主机。数据库连接成功！", JLabel.CENTER);
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // 数据表格
        String[] columnNames = {"ID", "名称", "描述", "创建日期"};
        tableModel = new DefaultTableModel(columnNames, 0);
        dataTable = new JTable(tableModel);
        dataTable.setRowHeight(25);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        // 设置表格的滚动面板
        JScrollPane scrollPane = new JScrollPane(dataTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("数据库内容"));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // 退出按钮
        JButton logoutButton = new JButton("退出登录");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(logoutButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        // 退出按钮的监听器
        logoutButton.addActionListener(e -> {
            dataFrame.dispose();
            setVisible(true); // 重新显示登录窗口
            usernameField.setText("");
            passwordField.setText("");
        });
        
        // 窗口关闭的监听器
        dataFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(true); // 重新显示登录窗口
            }
        });
        
        dataFrame.add(panel);
    }
    
    public void setConnected() {
        connectionLabel.setText("用户/127.0.0.1连接主机。数据库连接成功！");
    }
    
    private void setupListeners() {
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginGUI.this, "用户名和密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                client.login(username, password);
            }
        });
        
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                usernameField.setText("");
                passwordField.setText("");
            }
        });
    }
    
    public void updateLoginStatus(boolean success, String message) {
        if (success) {
            // 登录成功 - 显示提示、隐藏登录窗口、请求数据库数据
            statusLabel.setText("登录状态: " + message);
            JOptionPane.showMessageDialog(this, message, "成功", JOptionPane.INFORMATION_MESSAGE);
            
            // 隐藏登录窗口，显示数据窗口
            setVisible(false);
            
            // 初始化并显示数据窗口
            if (dataFrame == null) {
                initDataFrame();
            }
            dataFrame.setVisible(true);
            
            // 请求数据库数据
            client.requestData();
        } else {
            // 登录失败 - 显示提示信息
            statusLabel.setText("登录状态: " + message);
            JOptionPane.showMessageDialog(this, message, "失败", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void updateDataTable(ArrayList<HashMap<String, Object>> data) {
        // 确保dataFrame已初始化
        if (dataFrame == null || !dataFrame.isVisible()) {
            return;
        }
        
        // 清除表格数据
        tableModel.setRowCount(0);
        
        // 添加新数据
        if (data == null || data.isEmpty()) {
            System.out.println("警告: 收到空数据或null数据");
            return;
        }
        
        System.out.println("收到数据条数: " + data.size());
        
        for (HashMap<String, Object> row : data) {
            try {
                Object[] rowData = {
                    row.get("id"),
                    row.get("name"),
                    row.get("description"),
                    row.get("creation_date")
                };
                tableModel.addRow(rowData);
            } catch (Exception e) {
                System.err.println("处理数据行时出错: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 