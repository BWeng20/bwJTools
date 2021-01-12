package com.bw.jtools.ui.graph;

import java.awt.*;

public interface Decorator
{
	public void decorate(Graphics g, Node node);
	public Rectangle getBounds( Node node );

	public void install(Geometry g,Node node);
	public void uninstall(Geometry g,Node node);

}
