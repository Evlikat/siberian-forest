package net.evlikat.siberian.swing;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.evlikat.siberian.model.Cell;
import net.evlikat.siberian.model.Field;
import net.evlikat.siberian.model.Position;
import net.evlikat.siberian.model.Rabbit;
import net.evlikat.siberian.model.RegularWolf;
import net.evlikat.siberian.model.Sex;
import net.evlikat.siberian.model.Wolf;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class FieldVisualizationPanel extends JPanel {

    private static final Config CONF = ConfigFactory.load().getConfig("field");

    public static final int WIDTH = CONF.getInt("width");
    public static final int HEIGHT = CONF.getInt("height");
    //
    private Field field;

    public FieldVisualizationPanel() {
        super(true);
        setPreferredSize(new Dimension(Cell.SIZE * WIDTH, Cell.SIZE * HEIGHT));
    }

    public void init() {
        field = Field.create(WIDTH, HEIGHT);
        new Timer(false).scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGame();
            }
        }, 0, 100);

        IntStream.range(0, CONF.getInt("rabbits")).forEach(i -> {
            int randX = ThreadLocalRandom.current().nextInt(WIDTH);
            int randY = ThreadLocalRandom.current().nextInt(HEIGHT);
            field.addUnit(new Rabbit(Position.on(randX, randY)));
        });
        IntStream.range(0, CONF.getInt("wolves")).forEach(i -> {
            int randX = ThreadLocalRandom.current().nextInt(WIDTH);
            int randY = ThreadLocalRandom.current().nextInt(HEIGHT);
            field.addUnit(new RegularWolf(Position.on(randX, randY), Sex.random()));
        });
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        paintField(g2d);
    }

    private void paintField(Graphics2D g) {
        field.draw(g);
    }

    public void updateGame() {
        field.update();
        repaint();
    }
}
