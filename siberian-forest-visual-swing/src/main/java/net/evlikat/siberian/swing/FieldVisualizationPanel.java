package net.evlikat.siberian.swing;

import com.typesafe.config.Config;
import net.evlikat.siberian.config.Configuration;
import net.evlikat.siberian.geo.Position;
import net.evlikat.siberian.model.Field;
import net.evlikat.siberian.model.UpdateResult;
import net.evlikat.siberian.model.draw.CellDrawer;
import net.evlikat.siberian.model.draw.DrawableField;
import net.evlikat.siberian.model.draw.factory.CellFactory;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FieldVisualizationPanel extends JPanel {

    private static final Config CONF = Configuration.ROOT.getConfig("field");

    public static final int WIDTH = CONF.getInt("width");
    public static final int HEIGHT = CONF.getInt("height");
    //
    private volatile DrawableField field;
    private volatile Timer timer;
    //
    private Consumer<String> infoConsumer;

    public FieldVisualizationPanel() {
        super(true);
        setPreferredSize(new Dimension(CellDrawer.SIZE * WIDTH, CellDrawer.SIZE * HEIGHT));
    }

    public void init() {
        field = DrawableField.create(WIDTH, HEIGHT, new CellFactory(), new DrawableZooFactoryImpl());
        IntStream.range(0, CONF.getInt("rabbits")).forEach(i -> {
            int randX = ThreadLocalRandom.current().nextInt(WIDTH);
            int randY = ThreadLocalRandom.current().nextInt(HEIGHT);
            field.addRabbitOn(Position.on(randX, randY));
        });
        IntStream.range(0, CONF.getInt("wolves")).forEach(i -> {
            int randX = ThreadLocalRandom.current().nextInt(WIDTH);
            int randY = ThreadLocalRandom.current().nextInt(HEIGHT);
            field.addWolfOn(Position.on(randX, randY));
        });
        repaint();
    }

    public void setInfoConsumer(Consumer<String> infoConsumer) {
        this.infoConsumer = infoConsumer;
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
    protected void paintComponent(Graphics g) {
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
        UpdateResult result = field.update();
        repaint();
        if (infoConsumer != null) {
            infoConsumer.accept(
                    "Rabbits: " + result.getRabbitsTotal()
                            + ", Wolves: " + result.getWolvesTotal()
                            + ", Elapsed: "
                            + result.getElapsed() + "ms");
        }
    }

    public void putWolfOn(Point point) {
        if (timer == null) {
            field.addRabbitOn(positionBy(point));
            repaint();
        }
    }

    public void putRabbitOn(Point point) {
        if (timer == null) {
            field.addWolfOn(positionBy(point));
            repaint();
        }
    }

    public void showInfoAbout(Point point) {
        Position position = positionBy(point);
        JOptionPane.showMessageDialog(null, field.unitsOn(position)
                .map(Object::toString)
                .collect(Collectors.joining(",\n")));
    }

    private Position positionBy(Point point) {
        int x = (int) (point.getX() / CellDrawer.SIZE);
        int y = (int) (point.getY() / CellDrawer.SIZE);
        return Position.on(x, y);
    }
}
