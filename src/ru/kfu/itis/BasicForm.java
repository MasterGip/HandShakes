package ru.kfu.itis;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by mg on 02.10.14.
 */
public class BasicForm extends JFrame {

    private JTextField tf_friend_1;
    private JTextField tf_friend_2;
    private JTextField tf_exception;
    private JButton btn_go;



    BasicForm(){
        super();
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setSize(500, 500);

        JPanel panel = new JPanel(new GridLayout(1,3));
        tf_friend_1 = new JTextField("id1");
        tf_friend_2 = new JTextField("id2");
        tf_exception = new JTextField();
        tf_exception.setToolTipText("Найти пути, исключая данного пользователя");
        btn_go = new JButton("GO!");
        btn_go.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user_1 = tf_friend_1.getText();
                String user_2 = tf_friend_2.getText();
                String exceptionally = tf_exception.getText();
                String answer = Tools.getAnswer(user_1, user_2, exceptionally);
                JOptionPane.showMessageDialog(Main.frame, answer);

            }
        });


        panel.add(tf_friend_1);
        panel.add(tf_friend_2);
        panel.add(tf_exception);
        this.add(panel, BorderLayout.CENTER);
        this.add(btn_go, BorderLayout.SOUTH);
        this.setVisible(true);



    }

}
