package org.zells.cortex.zells;

import org.zells.dish.Dish;
import org.zells.dish.Zell;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.CompositeMessage;
import org.zells.dish.delivery.messages.IntegerMessage;
import org.zells.dish.delivery.messages.StringMessage;

import java.util.*;

public class TurtleZell implements Zell {

    private Dish dish;
    private Set<Address> canvases = new HashSet<Address>();
    private int x = 500;
    private int y = 500;
    private int a = 90;
    private List<List<Integer>> lines = new ArrayList<List<Integer>>();

    public TurtleZell(Dish dish) {
        this.dish = dish;
    }

    @Override
    public void receive(Message message) {
        if (message.read(0).asString().equals("canvas")
                && message.read("add") instanceof AddressMessage) {

            canvases.add(message.read("add").asAddress());
            redraw();
        } else if (message.read(0).asString().equals("reset")) {
            reset();
            redraw();
        } else if (message.read(0).asString().equals("go")) {
            if (!message.read("forward").isNull()) {
                go(message.read("forward").asInteger());
                redraw();
            } else if (!message.read("backwards").isNull()) {
                go(-message.read("backwards").asInteger());
                redraw();
            }
        } else if (message.read(0).asString().equals("turn")) {
            if (!message.read("left").isNull()) {
                turn(message.read("left").asInteger());
                redraw();
            } else if (!message.read("right").isNull()) {
                turn(-message.read("right").asInteger());
                redraw();
            }
        } else if (message.read(0).asString().equals("key")) {
            String key = message.read(1).asString();
            if (key.equals("left")) {
                turn(10);
            } else if (key.equals("right")) {
                turn(-10);
            } else if (key.equals("up")) {
                go(100);
            } else if (key.equals("down")) {
                go(-100);
            }
            redraw();
        }
    }

    private int dx(int d) {
        return x + (int) (d * Math.cos(Math.toRadians(a)));
    }

    private int dy(int d) {
        return y + (int) (d * Math.sin(Math.toRadians(a)));
    }

    private void reset() {
        x = 50;
        y = 50;
        a = 90;
        lines.clear();
    }

    private void go(int distance) {
        lines.add(Arrays.asList(x, y, dx(distance), dy(distance)));
        x = dx(distance);
        y = dy(distance);
    }

    private void turn(int angle) {
        a += angle;
    }

    private void redraw() {
        clear();
        drawLines();
        drawMyself();
    }

    private void drawLines() {
        for (List<Integer> line : lines) {
            drawLine(line.get(0), line.get(1), line.get(2), line.get(3));
        }
    }

    private void drawMyself() {
        drawCircle(x, y, 50);
        drawLine(dx(40), dy(40), dx(50), dy(50));
    }

    private void drawLine(int startX, int startY, int endX, int endY) {
        draw(new CompositeMessage()
                .put(1, new StringMessage("line"))
                .put("startX", new IntegerMessage(startX))
                .put("startY", new IntegerMessage(startY))
                .put("endX", new IntegerMessage(endX))
                .put("endY", new IntegerMessage(endY)));
    }

    private void drawCircle(int x, int y, int radius) {
        draw(new CompositeMessage()
                .put(1, new StringMessage("circle"))
                .put("centerX", new IntegerMessage(x))
                .put("centerY", new IntegerMessage(y))
                .put("radius", new IntegerMessage(radius)));
    }

    private void clear() {
        send(new CompositeMessage(new StringMessage("clear")));
    }

    private void draw(CompositeMessage message) {
        send(message.put(0, new StringMessage("draw")));
    }

    private void send(Message message) {
        for (Address canvas : canvases) {
            dish.send(canvas, message);
        }
    }
}
