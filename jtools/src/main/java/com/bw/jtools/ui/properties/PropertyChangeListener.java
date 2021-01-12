package com.bw.jtools.ui.properties;

/**
 * Interface to inform client that the user changed some property.<br>
 * For more general notification the event of the table model can be used.
 */
public interface PropertyChangeListener
{
	public void propertyChanged( PropertyValue value );

}
