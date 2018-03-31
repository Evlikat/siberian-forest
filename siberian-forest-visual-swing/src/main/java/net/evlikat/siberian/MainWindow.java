package net.evlikat.siberian;

import net.evlikat.siberian.model.Rabbit;
import net.evlikat.siberian.model.RabbitExample;
import net.evlikat.siberian.model.Sex;
import net.evlikat.siberian.model.Wolf;
import net.evlikat.siberian.model.WolfExample;
import net.evlikat.siberian.model.draw.RabbitDrawer;
import net.evlikat.siberian.model.draw.WolfDrawer;
import net.evlikat.siberian.swing.FieldVisualizationPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainWindow extends JFrame {

    private JLabel infoLabel;
    private JPanel rootPanel;
    private JPanel managementPanel;
    private JPanel creaturePanel;
    private JButton restartButton;
    private JButton stopStartButton;
    private JButton updateButton;
    private ButtonGroup bGroup;
    private JToggleButton femaleRabbitLabel;
    private JToggleButton maleRabbitLabel;
    private JToggleButton femaleWolfLabel;
    private JToggleButton maleWolfLabel;
    private FieldVisualizationPanel fieldPanel;

    public MainWindow() throws HeadlessException {
        super("Siberian Forest");
    }

    public void init() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));
        rootPanel.setBackground(Color.WHITE);

        managementPanel = new JPanel();
        managementPanel.setBackground(Color.WHITE);
        managementPanel.setLayout(new GridBagLayout());

        stopStartButton = styled(new JButton("Stop/Start"));
        stopStartButton.addActionListener(e -> fieldPanel.stopOrResume());
        updateButton = styled(new JButton("Turn"));
        updateButton.addActionListener(e -> fieldPanel.updateGame());
        restartButton = styled(new JButton("Restart"));
        restartButton.addActionListener(e -> fieldPanel.init());

        managementPanel.add(stopStartButton, gbc(0, 0));
        managementPanel.add(updateButton, gbc(1, 0));
        managementPanel.add(restartButton, gbc(2, 0));

        fieldPanel = new FieldVisualizationPanel();
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setInfoConsumer(info -> infoLabel.setText(info));

        fieldPanel.init();
        initMouseListener(fieldPanel);

        creaturePanel = new JPanel();
        creaturePanel.setLayout(new GridBagLayout());
        femaleRabbitLabel = styled(new JToggleButton("F", new ImageIcon(RabbitDrawer.IMG)));
        maleRabbitLabel = styled(new JToggleButton("M", new ImageIcon(RabbitDrawer.IMG)));
        femaleWolfLabel = styled(new JToggleButton("F", new ImageIcon(WolfDrawer.IMG)));
        maleWolfLabel = styled(new JToggleButton("M", new ImageIcon(WolfDrawer.IMG)));
        bGroup = new ButtonGroup();
        bGroup.add(femaleRabbitLabel);
        bGroup.add(maleRabbitLabel);
        bGroup.add(femaleWolfLabel);
        bGroup.add(maleWolfLabel);
        creaturePanel.add(femaleRabbitLabel, gbc(0, 0));
        creaturePanel.add(maleRabbitLabel, gbc(1, 0));
        creaturePanel.add(femaleWolfLabel, gbc(2, 0));
        creaturePanel.add(maleWolfLabel, gbc(3, 0));

        infoLabel = new JLabel("Info");
        infoLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rootPanel.add(managementPanel, BorderLayout.CENTER);
        rootPanel.add(fieldPanel, BorderLayout.CENTER);
        rootPanel.add(creaturePanel, BorderLayout.CENTER);
        rootPanel.add(infoLabel, BorderLayout.EAST);

        setContentPane(rootPanel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    private void initMouseListener(FieldVisualizationPanel panel) {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (femaleRabbitLabel.isSelected()) {
                        panel.putRabbitOn(e.getPoint(), new RabbitExample(Rabbit.ADULT, Sex.FEMALE));
                    } else if (maleRabbitLabel.isSelected()) {
                        panel.putRabbitOn(e.getPoint(), new RabbitExample(Rabbit.ADULT, Sex.MALE));
                    } else if (femaleWolfLabel.isSelected()) {
                        panel.putWolfOn(e.getPoint(), new WolfExample(Wolf.ADULT, Sex.MALE));
                    } else if (maleWolfLabel.isSelected()) {
                        panel.putWolfOn(e.getPoint(), new WolfExample(Wolf.ADULT, Sex.MALE));
                    }
                    if (!e.isShiftDown()) {
                        bGroup.clearSelection();
                    }
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    panel.showInfoAbout(e.getPoint());
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                fieldPanel.highlightAim(e.getPoint());
            }
        };
        panel.addMouseListener(mouseAdapter);
        panel.addMouseMotionListener(mouseAdapter);
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

    private <T extends AbstractButton> T styled(T button) {
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
