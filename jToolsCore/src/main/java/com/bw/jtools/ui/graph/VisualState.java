package com.bw.jtools.ui.graph;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

/**
 * Stores the state of a grahpical element.
 */
public class VisualState
{

    /**
     * The outline/Box of the element.
     */
    public Rectangle2D.Float boundingBox;

    public Map<Integer, Connector> connectors;

    /**
     * Visible or hidden
     */
    public boolean visible;
    
    /**
     * Get connector at point.
     * @param graphPoint The element relative coorindates.
     * @param snapRadius The additional radius to accept a match
     * @return The found connector or null
     */
    public Connector getConnectorAt(Point2D.Float graphPoint, float snapRadius)
    {
        Connector bestMatch = null;
        float bdx = Float.MAX_VALUE;
        float bdy = Float.MAX_VALUE;
        for ( Connector c : connectors.values() )
        {
            float dx = Math.abs(c.xOffset-graphPoint.x);
            float dy = Math.abs(c.yOffset-graphPoint.y);
            
            if ( dx < snapRadius && dx < bdx && dy < snapRadius && dy < bdy )
            {
                bestMatch = c;
                bdx = dx;
                bdy = dy;
            }
        }
        return bestMatch;
    }
}
