package ru.kfu.itis;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mg on 02.10.14.
 */
public class Main {

    public static BasicForm frame;

    public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame = new BasicForm();
            }
        });

    }
}
