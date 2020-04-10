package orfgui;
import java.awt.*;

/**
 * Rectangle object contains all information needed to draw a rectangle in  a random color
 */
public class Rectangle {

        private int width;
        private int height;
        private int xpos;
        private int ypos;
        private Color color;

        Rectangle() {
            xpos = 1;
            ypos = 1;
            width = 1;
            height = 1;
            color = Color.WHITE;

        }

        Rectangle(int x, int y, int w, int h, Color c) {
            xpos = x;
            ypos = y;
            width = w;
            height = h;
            color = c;
        }

    public int getXpos() {
        return xpos;
    }

    public void setXpos(int x) {
            xpos = x;
    }

    public int getYpos() {
        return ypos;
    }

    public void setYpos(int y) {
        ypos = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color c) {
            color = c;
    }

    public int getHeight() {
            return height;
        }

        public void setHeight(int h) {
            height = h;
        }

        public int getWidth() {
            return width;
        }

        public void setWidth(int w) {
            width = w;
        }

    @Override
    public String toString() {
        return "Rectangle{" +
                "width=" + width +
                ", height=" + height +
                ", xpos=" + xpos +
                ", ypos=" + ypos +
                ", color=" + color +
                '}';
    }
}
