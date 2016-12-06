package net.evlikat.siberian.swing;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import net.evlikat.siberian.model.Cell;
import net.evlikat.siberian.model.Field;
import net.evlikat.siberian.model.Position;
import net.evlikat.siberian.model.Rabbit;
import net.evlikat.siberian.model.RegularRabbit;
import net.evlikat.siberian.model.RegularWolf;
import net.evlikat.siberian.model.Sex;

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
    private static final Config RABBIT_CONF = ConfigFactory.load().getConfig("rabbit");
    private static final Config WOLF_CONF = ConfigFactory.load().getConfig("wolf");

    public static final int WIDTH = CONF.getInt("width");
    public static final int HEIGHT = CONF.getInt("height");

    public static final int RABBIT_MAX_AGE = RABBIT_CONF.getInt("maxAge");
    public static final int WOLF_MAX_AGE = WOLF_CONF.getInt("maxAge");
    //
    private volatile Field field;
    private volatile Timer timer;

    public FieldVisualizationPanel() {
        super(true);
        setPreferredSize(new Dimension(Cell.SIZE * WIDTH, Cell.SIZE * HEIGHT));
    }

    public void init() {
        field = Field.create(WIDTH, HEIGHT);
        IntStream.range(0, CONF.getInt("rabbits")).forEach(i -> {
            int randX = ThreadLocalRandom.current().nextInt(WIDTH);
            int randY = ThreadLocalRandom.current().nextInt(HEIGHT);
            field.addUnit(new RegularRabbit(Position.on(randX, randY), randomAge(RABBIT_MAX_AGE), Sex.random(), field));
        });
        IntStream.range(0, CONF.getInt("wolves")).forEach(i -> {
            int randX = ThreadLocalRandom.current().nextInt(WIDTH);
            int randY = ThreadLocalRandom.current().nextInt(HEIGHT);
            field.addUnit(new RegularWolf(Position.on(randX, randY), randomAge(WOLF_MAX_AGE), Sex.random(), field));
        });
        repaint();
    }

    private int randomAge(int max) {
        return ThreadLocalRandom.current().nextInt(max / 2);
    }

    public void stopOrResume() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        } else {
            start();
        }
    }

    private void start() {
        timer = new Timer(false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGame();
            }
        }, 0, 100);
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
