package net.evlikat.siberian;

import java.awt.*;

public class Application {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            MainWindow mainWindow = new MainWindow();
            mainWindow.init();
        });
    }
}
