package com.bw.jtools.ui;

import com.bw.jtools.Log;
import com.bw.jtools.ui.icon.IconTool;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Helper to show context-relative Pop-Up-Menus.
 */
public class JContextMenu extends JPopupMenu
{

	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = -4608782575452517082L;

	Runnable[] actions_;

	private ActionListener createActionListener(final int i)
	{
		return (ActionEvent e) -> actions_[i].run();
	}

	public JContextMenu(String menu_name, JComponent parent, Object[][] entries, Runnable[] actions)
	{
		super(menu_name);

		final int N = entries.length;

		if (N != actions.length)
		{
			throw new IllegalArgumentException("Size of 'entries' and 'actions' is not equal");
		}

		actions_ = actions;

		for (int i = 0; i < N; ++i)
		{
			final String itemName = entries[i] != null ? String.valueOf(entries[i][0]) : null;

			if (itemName != null)
			{
				if (itemName.equals("---"))
				{
					addSeparator();
				}
				else
				{
					if (entries[i].length != 2)
					{
						throw new IllegalArgumentException("Size of array 'entries[" + i + "]' is not two");
					}

					Icon icon = null;

					Object iconObj = entries[i][1];
					if (iconObj != null)
					{
						if (iconObj instanceof Icon)
						{
							icon = (Icon) iconObj;
						}
						else if (iconObj instanceof String)
						{
							String iconName = (String) iconObj;
							if (!iconName.isEmpty())
							{
								icon = IconTool.getIcon(iconName);
							}
						}
						else
						{
							Log.warn("JContextMenu: entries[" + i + "][1] have to be a String of Icon.");
						}
					}

					JMenuItem item = new JMenuItem(itemName, icon);

					item.addActionListener(createActionListener(i));
					add(item);
				}
			}
		}

		parent.addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					show(e.getComponent(), e.getX(), e.getY());
				}
			}

			public void mouseReleased(MouseEvent e)
			{
				if (e.isPopupTrigger())
				{
					show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

	}

}
