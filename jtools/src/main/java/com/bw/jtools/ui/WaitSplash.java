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

import java.awt.Graphics;


/**
 * A application modal "please wait" splash screen.
 */
final public class WaitSplash extends javax.swing.JFrame
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 707461739107938372L;

	private static WaitSplash instance_;

	public static void showWait(boolean show)
	{
		showWait(show, null);
	}

	public static void showWait(boolean show, String text)
	{
		if (instance_ == null && show)
		{
			instance_ = new WaitSplash();
		}
		if (instance_ != null)
		{
			if (show)
			{
				if (text == null) text = I18N.getText("wait.message");
				instance_.text_.setText(text);
				instance_.setLocationRelativeTo(null);
				instance_.toFront();
			}
			instance_.setVisible(show);
		}
		if (show)
		{
			Graphics g = instance_.getGraphics();
			if (g != null)
				instance_.paint(g);
		}
	}

	protected WaitSplash()
	{
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	private void initComponents()
	{

		background_panel_ = new javax.swing.JPanel();
		text_ = new javax.swing.JLabel();

		setAutoRequestFocus(false);
		setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		setEnabled(false);
		setFocusable(false);
		setIconImages(IconTool.getAppIconImages());
		setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		setName("WaitSplash"); // NOI18N
		setUndecorated(true);
		setResizable(false);
		setType(java.awt.Window.Type.POPUP);

		background_panel_.setBackground(new java.awt.Color(255, 255, 255));
		background_panel_.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
		background_panel_.setDoubleBuffered(false);

		text_.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
		text_.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		text_.setText(I18N.getText("wait.message"));

		javax.swing.GroupLayout background_panel_Layout = new javax.swing.GroupLayout(background_panel_);
		background_panel_.setLayout(background_panel_Layout);
		background_panel_Layout.setHorizontalGroup(
				background_panel_Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
									   .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, background_panel_Layout.createSequentialGroup()
																													.addContainerGap()
																													.addComponent(text_, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
																													.addContainerGap())
		);
		background_panel_Layout.setVerticalGroup(
				background_panel_Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
									   .addGroup(background_panel_Layout.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(text_)
																		.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					  .addComponent(background_panel_, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					  .addComponent(background_panel_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
		);

		pack();
	}

	private javax.swing.JPanel background_panel_;
	private javax.swing.JLabel text_;
}
