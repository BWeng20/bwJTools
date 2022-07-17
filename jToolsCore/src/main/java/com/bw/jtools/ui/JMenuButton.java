/*
 * (c) copyright Bernd Wengenroth
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.bw.jtools.ui;

import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.ActionEvent;

/**
 * A Button that shows a popup-menu directly below.
 */
public class JMenuButton extends JToggleButton
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 8971737844940749760L;

	/**
	 * The menu to show.
	 */
	public JPopupMenu menu_;

	/**
	 * Creates a new Menu Button with label and optional an icon.
	 *
	 * @param label The label for the button.
	 * @param icon  The optional icon. Can be null.
	 * @param menu  The pop-up menu to show.
	 */
	public JMenuButton(String label, Icon icon, JPopupMenu menu)
	{
		super(label, icon, false);
		this.menu_ = menu;

		addActionListener(
				(ActionEvent ev) ->
				{
					if (isSelected())
					{
						menu_.show(JMenuButton.this, 0, getBounds().height);
					}
					else
					{
						menu_.setVisible(false);
					}
				});

		menu.addPopupMenuListener(new PopupMenuListener()
		{
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e)
			{
				JMenuButton.this.setSelected(false);
			}

			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e)
			{
			}

			@Override
			public void popupMenuCanceled(PopupMenuEvent e)
			{
			}
		});
	}
}
