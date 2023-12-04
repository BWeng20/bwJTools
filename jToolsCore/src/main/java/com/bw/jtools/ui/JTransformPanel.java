package com.bw.jtools.ui;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

/**
 * Similar to JXTransform from the java.net swinghelper project, but handles multiple components.
 */
public class JTransformPanel extends JPanel
{

	private Rectangle visibleRect;


	//This is important
	public boolean isOptimizedDrawingEnabled()
	{
		return false;
	}

	public JTransformPanel()
	{
		this(null);
	}

	public JTransformPanel(LayoutManager lm)
	{
		super(lm);
		setOpaque(false);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.MOUSE_WHEEL_EVENT_MASK);
		ToolTipManager.sharedInstance()
					  .registerComponent(this);
	}

	private Rectangle getTransformedSize()
	{
		Dimension viewSize = getSize();
		Rectangle viewRect = new Rectangle(viewSize);
		if (at != null)
		{
			viewRect = at.createTransformedShape(viewRect)
						 .getBounds();
		}
		return viewRect;
	}

	public void paint(Graphics g)
	{
		//repaint the whole transformer in case the view component was repainted
		Rectangle clipBounds = g.getClipBounds();
		if (clipBounds != null && !clipBounds.equals(visibleRect))
		{
			repaint();
		}
		//clear the background
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());

		if (at != null && at.getDeterminant() != 0)
		{
			Graphics2D g2 = (Graphics2D) g.create();
			Insets insets = getInsets();
			Rectangle bounds = getBounds();

			//don't forget about insets
			bounds.x += insets.left;
			bounds.y += insets.top;
			bounds.width -= insets.left + insets.right;
			bounds.height -= insets.top + insets.bottom;
			double centerX1 = bounds.getCenterX();
			double centerY1 = bounds.getCenterY();

			Rectangle tb = getTransformedSize();
			double centerX2 = tb.getCenterX();
			double centerY2 = tb.getCenterY();

			//set antialiasing by default
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			//translate it to the center of the view component again
			double tx = centerX1 - centerX2 - getX();
			double ty = centerY1 - centerY2 - getY();
			g2.translate((int) tx, (int) ty);
			g2.transform(at);
			super.paint(g2);
			g2.dispose();
		}
		//paint the border
		paintBorder(g);
	}

	public AffineTransform at;

	private Component mouseEnteredComponent;
	private Component mouseDraggedComponent;
	private Component mouseCurrentComponent;


	private MouseEvent transformMouseEvent(MouseEvent event)
	{
		if (event == null)
		{
			return null;
		}

		MouseEvent newEvent;
		if (event instanceof MouseWheelEvent)
		{
			MouseWheelEvent mouseWheelEvent = (MouseWheelEvent) event;
			newEvent = new MouseWheelEvent(mouseWheelEvent.getComponent(), mouseWheelEvent.getID(),
					mouseWheelEvent.getWhen(), mouseWheelEvent.getModifiers(),
					mouseWheelEvent.getX(), mouseWheelEvent.getY(),
					mouseWheelEvent.getClickCount(), mouseWheelEvent.isPopupTrigger(),
					mouseWheelEvent.getScrollType(), mouseWheelEvent.getScrollAmount(),
					mouseWheelEvent.getWheelRotation());
		}
		else
		{
			newEvent = new MouseEvent(event.getComponent(), event.getID(),
					event.getWhen(), event.getModifiers(),
					event.getX(), event.getY(),
					event.getClickCount(), event.isPopupTrigger(), event.getButton());
		}

		if (at.getDeterminant() != 0)
		{


			Rectangle viewBounds = getTransformedSize();
			Insets insets = getInsets();
			int xgap = (getWidth() - (viewBounds.width + insets.left + insets.right)) / 2;
			int ygap = (getHeight() - (viewBounds.height + insets.top + insets.bottom)) / 2;

			double x = newEvent.getX() + viewBounds.getX() - insets.left;
			double y = newEvent.getY() + viewBounds.getY() - insets.top;
			Point2D p = new Point2D.Double(x - xgap, y - ygap);

			Point2D tp;
			try
			{
				tp = at.inverseTransform(p, null);
			}
			catch (Exception ex)
			{
				//can't happen, we check it before
				throw new AssertionError("NoninvertibleTransformException");
			}
			//Use transformed coordinates to get the current component
			mouseCurrentComponent =
					SwingUtilities.getDeepestComponentAt(this, (int) tp.getX(), (int) tp.getY());
			if (mouseCurrentComponent == null)
			{
				mouseCurrentComponent = this;
			}
			Component tempComponent = mouseCurrentComponent;
			if (mouseDraggedComponent != null)
			{
				tempComponent = mouseDraggedComponent;
			}

			Point point = SwingUtilities.convertPoint(this, (int) tp.getX(), (int) tp.getY(), tempComponent);
			newEvent.setSource(tempComponent);
			newEvent.translatePoint(point.x - event.getX(), point.y - event.getY());
		}
		return newEvent;
	}

	protected void processMouseEvent(MouseEvent e)
	{
		MouseEvent transformedEvent = transformMouseEvent(e);
		switch (e.getID())
		{
			case MouseEvent.MOUSE_ENTERED:
				if (mouseDraggedComponent == null || mouseCurrentComponent == mouseDraggedComponent)
				{
					dispatchMouseEvent(transformedEvent);
				}
				break;
			case MouseEvent.MOUSE_EXITED:
				if (mouseEnteredComponent != null)
				{
					dispatchMouseEvent(createEnterExitEvent(mouseEnteredComponent, MouseEvent.MOUSE_EXITED, e));
					mouseEnteredComponent = null;
				}
				break;
			case MouseEvent.MOUSE_RELEASED:
				if (mouseDraggedComponent != null && e.getButton() == MouseEvent.BUTTON1)
				{
					transformedEvent.setSource(mouseDraggedComponent);
					mouseDraggedComponent = null;
				}
				dispatchMouseEvent(transformedEvent);
				break;
			default:
				dispatchMouseEvent(transformedEvent);
		}
		super.processMouseEvent(e);
	}

	private void dispatchMouseEvent(MouseEvent event)
	{
		MouseListener[] mouseListeners =
				event.getComponent()
					 .getMouseListeners();
		for (MouseListener listener : mouseListeners)
		{
			//skip all ToolTipManager's related listeners
			if (!listener.getClass()
						 .getName()
						 .startsWith("javax.swing.ToolTipManager"))
			{
				switch (event.getID())
				{
					case MouseEvent.MOUSE_PRESSED:
						listener.mousePressed(event);
						break;
					case MouseEvent.MOUSE_RELEASED:
						listener.mouseReleased(event);
						break;
					case MouseEvent.MOUSE_CLICKED:
						listener.mouseClicked(event);
						break;
					case MouseEvent.MOUSE_EXITED:
						listener.mouseExited(event);
						break;
					case MouseEvent.MOUSE_ENTERED:
						listener.mouseEntered(event);
						break;
					default:
						throw new AssertionError();
				}
			}
		}
	}

	protected void processMouseMotionEvent(MouseEvent e)
	{
		MouseEvent transformedEvent = transformMouseEvent(e);
		if (mouseEnteredComponent == null)
		{
			mouseEnteredComponent = mouseCurrentComponent;
		}
		switch (e.getID())
		{
			case MouseEvent.MOUSE_MOVED:
				if (mouseCurrentComponent != mouseEnteredComponent)
				{
					dispatchMouseEvent(createEnterExitEvent(mouseEnteredComponent, MouseEvent.MOUSE_EXITED, e));
					dispatchMouseEvent(createEnterExitEvent(mouseCurrentComponent, MouseEvent.MOUSE_ENTERED, e));
				}
				break;
			case MouseEvent.MOUSE_DRAGGED:
				if (mouseDraggedComponent == null)
				{
					mouseDraggedComponent = mouseEnteredComponent;
				}
				if (mouseEnteredComponent == mouseDraggedComponent && mouseCurrentComponent != mouseDraggedComponent)
				{
					dispatchMouseEvent(createEnterExitEvent(mouseDraggedComponent, MouseEvent.MOUSE_EXITED, e));
				}
				else if (mouseEnteredComponent != mouseDraggedComponent && mouseCurrentComponent == mouseDraggedComponent)
				{
					dispatchMouseEvent(createEnterExitEvent(mouseDraggedComponent, MouseEvent.MOUSE_ENTERED, e));
				}
				if (mouseDraggedComponent != null)
				{
					transformedEvent.setSource(mouseDraggedComponent);
				}
				break;
		}
		mouseEnteredComponent = mouseCurrentComponent;
		//dispatch MouseMotionEvent
		MouseMotionListener[] mouseMotionListeners =
				transformedEvent.getComponent()
								.getMouseMotionListeners();
		for (MouseMotionListener listener : mouseMotionListeners)
		{
			//skip all ToolTipManager's related listeners
			if (!listener.getClass()
						 .getName()
						 .startsWith("javax.swing.ToolTipManager"))
			{
				switch (transformedEvent.getID())
				{
					case MouseEvent.MOUSE_MOVED:
						listener.mouseMoved(transformedEvent);
						break;
					case MouseEvent.MOUSE_DRAGGED:
						listener.mouseDragged(transformedEvent);
						break;
					default:
						throw new AssertionError();
				}
			}
		}
		super.processMouseMotionEvent(e);
	}

	protected void processMouseWheelEvent(MouseWheelEvent e)
	{
		MouseWheelEvent transformedEvent = (MouseWheelEvent) transformMouseEvent(e);
		MouseWheelListener[] mouseWheelListeners =
				transformedEvent.getComponent()
								.getMouseWheelListeners();
		for (MouseWheelListener listener : mouseWheelListeners)
		{
			listener.mouseWheelMoved(transformedEvent);
		}
		super.processMouseWheelEvent(e);
	}

	public String getToolTipText(MouseEvent event)
	{
		if (mouseEnteredComponent instanceof JComponent)
		{
			return ((JComponent) mouseEnteredComponent).getToolTipText();
		}
		else
			return getToolTipText();
	}

	private MouseEvent createEnterExitEvent(Component c, int eventId, MouseEvent mouseEvent)
	{
		return new MouseEvent(c, eventId, mouseEvent.getWhen(), 0,
				mouseEvent.getX(), mouseEvent.getY(), 0,
				false, MouseEvent.NOBUTTON);
	}

	public void layout()
	{
		try
		{
			if (at != null)
			{
				Dimension os = getSize();
				Rectangle r = new Rectangle(0, 0, os.width, os.height);
				AffineTransform t = new AffineTransform(at);
				t.invert();
				// setSize(t.createTransformedShape(r).getBounds().getSize());
				setSize(new Dimension(os.width / 2, os.height / 2));
				super.layout();
				setSize(os);
			}
		}
		catch (NoninvertibleTransformException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Dimension getPreferredSize()
	{
		Dimension size = getTransformedSize().getSize();
		Insets insets = getInsets();
		size.width += insets.left + insets.right;
		size.height += insets.top + insets.bottom;
		return size;
	}
}
