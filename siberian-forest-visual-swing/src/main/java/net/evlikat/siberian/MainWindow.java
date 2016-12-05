package net.evlikat.siberian;

import net.evlikat.siberian.swing.FieldVisualizationPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class MainWindow extends JFrame {

    private JPanel rootPanel;
    private JPanel managementPanel;
    private JButton restartButton;
    private JButton stopStartButton;
    private JButton updateButton;
    private FieldVisualizationPanel fieldPanel;

    public MainWindow() throws HeadlessException {
        super("Siberian Forest");
    }

    public void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        rootPanel = new JPanel(new FlowLayout());
        rootPanel.setBackground(Color.WHITE);

        managementPanel = new JPanel();
        managementPanel.setLayout(new GridBagLayout());

        stopStartButton = styled(new JButton("Stop/Start"));
        stopStartButton.addActionListener(e -> fieldPanel.stopOrResume());
        updateButton = styled(new JButton("Turn"));
        updateButton.addActionListener(e -> fieldPanel.updateGame());
        restartButton = styled(new JButton("Restart"));
        restartButton.addActionListener(e -> fieldPanel.init());

        managementPanel.add(stopStartButton, gbc(0, 1));
        managementPanel.add(updateButton, gbc(0, 2));
        managementPanel.add(restartButton, gbc(0, 3));

        fieldPanel = new FieldVisualizationPanel();
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.init();

        rootPanel.add(managementPanel);
        rootPanel.add(fieldPanel);

        setContentPane(rootPanel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private GridBagConstraints gbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 2, 2, 2);
        return gbc;
    }

    private JButton styled(JButton button) {
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        Border line = new LineBorder(Color.BLACK);
        Border margin = new EmptyBorder(5, 15, 5, 15);
        Border compound = new CompoundBorder(line, margin);
        button.setBorder(compound);
        button.setMinimumSize(new Dimension(100, 30));
        return button;
    }
}
