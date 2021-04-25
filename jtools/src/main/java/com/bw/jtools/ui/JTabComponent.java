/*
 * (c) copyright Bernd Wengenroth
 *
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

import com.bw.jtools.ui.icon.IconTool;

import javax.swing.*;
import java.awt.*;

/**
 * Panel to work as Tab-Component in a JTabbedPane.<br>
 * Contains a text and a "close"-icons with roll-over effect.<br>
 * The icons can be specified by c'tor-call or use the defaults.<br>
 * In the second case the application have to provide two icons in the icons-folder:<br>
 * <i>"icons/tab_action.png"</i> and <i>"icons/tab_action_ro.png"</i>.<br>
 * <br>
 * <i><u>Note about UI behavior:</u></i><br>
 * The Swing-UI place the tab-component fixed at the
 * center of the available tab-area.<br>
 * So the button will not at the right border if the area gets too big.<br>
 * This can't be configured or bypasses in some reliable way. <br>
 * <br>
 * See example com.bw.jtools.examples.tabcomponent.JTabComponentDemo.
 *
 * @see IconTool#getIcon(java.lang.String)
 */
public class JTabComponent extends JPanel
{

	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = -7716113934005257056L;

	/**
	 * Creates a new Tab-Component.
	 *
	 * @param name           The name to show.
	 * @param icon           The tab-icon or null.
	 * @param action         Runnable to call if the close button is pressed.
	 *                       If null, the button will select the tab if clicked.
	 * @param action_icon    Icon for the button.
	 * @param action_icon_ro Rollover-Icon for the button or null.
	 */
	public JTabComponent(String name, Icon icon, Runnable action,
						 Icon action_icon, Icon action_icon_ro)
	{
		super(new GridBagLayout());

		// Create some gap to upper edge.
		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));

		if (action_icon == null)
		{
			if (action_tab_icon_ == null)
			{
				action_tab_icon_ = IconTool.getIcon("tab_action.png");
				action_tab_icon_ro_ = IconTool.getIcon("tab_action_ro.png");
			}
			action_icon = action_tab_icon_;
			action_icon_ro = action_tab_icon_ro_;
		}

		setOpaque(false);

		JLabel label = new JLabel(name);
		if (icon != null)
		{
			label.setIcon(icon);
		}

		GridBagConstraints gc = new GridBagConstraints();
		gc.anchor = GridBagConstraints.LINE_START;
		gc.weightx = 1;
		gc.gridy = 0;
		gc.gridx = 0;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		add(label, gc);

		JButton actionButton = new JButton(action_icon);
		actionButton.setContentAreaFilled(false);
		if (action_icon_ro != null)
			actionButton.setRolloverIcon(action_icon_ro);

		actionButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 0));
		actionButton.setBorderPainted(false);

		if (action != null)
		{
			actionButton.addActionListener((actionEvent) -> action.run());
		}
		else
		{
			actionButton.addActionListener((actionEvent) ->
			{
				Container c = JTabComponent.this.getParent();
				while (c != null && !(c instanceof JTabbedPane))
				{
					c = c.getParent();
				}
				if (c != null)
				{
					JTabbedPane tp = (JTabbedPane) c;
					int tabIdx = tp.indexOfTabComponent(JTabComponent.this);
					if (tabIdx != -1)
						tp.setSelectedIndex(tabIdx);
				}
			});
		}
		gc.anchor = GridBagConstraints.LINE_END;
		gc.weightx = 0;
		gc.gridx = 1;
		gc.fill = GridBagConstraints.NONE;
		add(actionButton, gc);
	}

	@Override
	public void setBounds(int x, int y, int w, int h)
	{
		super.setBounds(x, y, w, h);
	}

	/**
	 * Creates a new Tab-Component.
	 *
	 * @param name   The name to show.
	 * @param icon   The tab-icon or null.
	 * @param action Runnable to call if the close button is pressed.
	 */
	public JTabComponent(String name, Icon icon, Runnable action)
	{
		this(name, icon, action, null, null);
	}

	static Icon action_tab_icon_;
	static Icon action_tab_icon_ro_;

}
