package net.evlikat.siberian;

import net.evlikat.siberian.swing.FieldVisualizationPanel;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {

    private FieldVisualizationPanel mainPanel;

    public MainWindow() throws HeadlessException {
        super("Siberian Forest");
    }

    public void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 800);

        mainPanel = new FieldVisualizationPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.init();

        setContentPane(mainPanel);
        setVisible(true);
    }
}
