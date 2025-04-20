package com.bw.jtools.ui.graph;

import com.bw.jtools.geometry.PointOn;
import com.bw.jtools.ui.UITool;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.function.Supplier;

/**
 * A generic leaf shape.
 */
public class Leaf {
    Color color;
    Color fillColor;

    Point2D.Double base;

    Path2D.Double strokePath;
    Path2D.Double bodyOutline;

    AffineTransform transform = new AffineTransform();

    /**
     * Get the color of the outline and veins.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Set the color of the outline and veins.
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Get the color used for the leaf background.
     */
    public Color getFillColor() {
        return fillColor;
    }

    /**
     * Set the color used for the leaf background.
     */
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    /**
     * Create a new Leaf. The petiole start will be at position 0,0.
     * The leaf extends vertically in positive direction and horizontal in both directions.
     *
     * @param w     Width of the leaf.
     * @param h     Height of the leaf
     * @param veins Number of veins to generate
     */
    public Leaf(double w, double h, int veins) {
        this(w, h * (9.0 / 10.0), h / 10.0, veins, null, UITool.multiplyColor(Color.GREEN, 0.7f), Color.GREEN);
    }

    /**
     * Create a new Leaf. The petiole start will be at position 0,0.
     * The leaf extends vertically in positive direction and horizontal in both directions.
     *
     * @param w             Width of the leaf.
     * @param h             Height of the leaf body
     * @param petioleLength Length of the petiole. Can be small, but must not be zero.
     * @param veins         Number of veins to generate
     * @param rand          Random number source. If null, {@link Math#random()} is used.
     * @param color         Color used for outline and veins.
     * @param fillColor     Color used for leaf background.
     */
    public Leaf(double w, double h, double petioleLength, int veins, Supplier<Double> rand, Color color, Color fillColor) {
        this.fillColor = fillColor;
        this.color = color;
        this.base = new Point2D.Double(0, 0);

        if (rand == null)
            rand = Math::random;

        CubicCurve2D.Double left, right, midrib;

        double cp1x = -w / 2 - rand.get() * (w / 5);

        Point2D.Double tipPoint = new Point2D.Double(0, petioleLength + h);

        left = new CubicCurve2D.Double(
                0, petioleLength,
                cp1x, petioleLength - rand.get() * (h / 5),
                cp1x, petioleLength + h / 2 - rand.get() * (h / 10),
                tipPoint.x, tipPoint.y
        );

        right = new CubicCurve2D.Double(
                0, petioleLength,
                -cp1x, petioleLength - rand.get() * (h / 5),
                -cp1x, petioleLength + h / 2 - rand.get() * (h / 10),
                tipPoint.x, tipPoint.y);

        midrib = new CubicCurve2D.Double(
                0, petioleLength,
                rand.get() * (w / 20), h / 3 + rand.get() * (h / 20),
                rand.get() * (w / 20), h * (2 / 3) + rand.get() * (h / 20),
                tipPoint.x, tipPoint.y);

        bodyOutline = new Path2D.Double();
        bodyOutline.moveTo(0, petioleLength);

        bodyOutline.append(left, true);

        bodyOutline.curveTo(
                right.ctrlx2, right.ctrly2,
                right.ctrlx1, right.ctrly1,
                right.x1, right.y1
        );
        bodyOutline.closePath();

        strokePath = new Path2D.Double();
        strokePath.moveTo(0, 0);
        strokePath.lineTo(0, petioleLength);
        strokePath.append(bodyOutline, true);
        strokePath.append(midrib, false);

        for (int i = 0; i < veins; i++) {
            Point2D.Double p1 = PointOn.offset(left, 0.5 + i * (0.5 / veins));
            Point2D.Double p2 = PointOn.offset(midrib, i * (0.9 / veins));

            strokePath.moveTo(p1.x, p1.y);
            strokePath.lineTo(p2.x, p2.y);
            p2 = PointOn.offset(right, 0.5 + i * (0.5 / veins));
            strokePath.lineTo(p2.x, p2.y);
        }
    }

    /**
     * Draws the leaf with the current stroke.
     *
     * @param g2 The graphics-context.
     */
    public void draw(Graphics2D g2) {
        AffineTransform aft = g2.getTransform();
        g2.transform(transform);
        g2.setColor(fillColor);
        g2.fill(bodyOutline);
        g2.setColor(color);
        g2.draw(strokePath);
        g2.setTransform(aft);
    }

    public void rotate(double radians) {
        transform.rotate(radians, base.x, base.y);
    }

}
