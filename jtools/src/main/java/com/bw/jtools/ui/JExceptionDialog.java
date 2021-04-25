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

import com.bw.jtools.Log;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.ui.icon.IconTool;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Dialog to show exception details.
 */
public class JExceptionDialog extends javax.swing.JDialog
{
	/**
	 * Generated Serial Version
	 */
	private static final long serialVersionUID = 754679126955976546L;

	/**
	 * Creates new exception dialog with default title and message.<br>
	 * Content of I18N property <i>exception.reportToUrl</i> is used to create a link to
	 * send the report,<br>
	 * e.g. a mailto-url 'mailto:info@company.com?subject=Exception&amp;body=' can be used
	 * to use the system e-mail application to send the report.
	 *
	 * @param parent  The parent for the dialog. Normally the main window of the application.
	 * @param throwed The Throwable to show.
	 */
	public JExceptionDialog(Component parent, Throwable throwed)
	{
		this(parent, null, null, throwed);
	}

	/**
	 * Creates new exception dialog.<br>
	 * Content of I18N property <i>exception.reportToUrl</i> is used to create a link to
	 * send the report,<br>
	 * e.g. a mailto-url 'mailto:info@company.com?subject=Exception&amp;body=' can be used
	 * to use the system e-mail application to send the report.
	 *
	 * @param parent  The parent for the dialog. Normally the main window of the application.
	 * @param title   Title of the dialog.
	 *                If null, the value of i18n key 'exception.title' will be used.
	 * @param message Additional message that will be shown above the exception.
	 *                If null, the value of i18n key 'exception.info' will be used.
	 * @param throwed The Throwable to show.
	 */
	public JExceptionDialog(Component parent, String title, String message, Throwable throwed)
	{
		super(SwingUtilities.getWindowAncestor(parent), ModalityType.APPLICATION_MODAL);
		initComponents();
		if (message != null)
		{
			label_title_.setText(message);
		}
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		setIconImages(IconTool.getAppIconImages());

		String msg = throwed.getMessage();
		if (msg == null || msg.isEmpty()) msg = throwed.getClass()
													   .getSimpleName();

		setTitle(title == null ? I18N.format("exception.title", msg) : title);

		shortST = IOTool.getRestrictedStackTrace(throwed, "", 10);
		fullST = IOTool.getRestrictedStackTrace(throwed, "", 2000);

		text_.setText(shortST);

		try
		{
			// URLEncoder doesn't conform to mailto-specific encoding.
			// so we have to replace all "+" with "space".
			// @TODO: Possibly also other chars are not correct.
			String body = URLEncoder.encode(shortST, "UTF-8")
									.replace("+", "%20");
			mail_link_.setUri(I18N.getText("exception.reportToUrl") + body);
		}
		catch (UnsupportedEncodingException ex)
		{
			Log.error("Failed to encode exception e-mail", ex);
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 */
	private void initComponents()
	{
		label_title_ = new javax.swing.JLabel();
		mail_link_ = new JLink();
		button_close_ = new javax.swing.JButton();
		scroller_ = new javax.swing.JScrollPane();
		text_ = new javax.swing.JTextArea();
		SwitchTrace = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
		setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		setPreferredSize(new java.awt.Dimension(600, 350));

		label_title_.setFont(new java.awt.Font("Dialog", Font.BOLD, 14)); // NOI18N
		label_title_.setText(I18N.getText("exception.info"));
		label_title_.setAlignmentY(0.0F);

		mail_link_.setToolTipText("mailto:Bernd.Wengenroth@gmx.de");
		mail_link_.setAlias(I18N.getText("exception.link.alias"));
		mail_link_.setUri("");

		button_close_.setText(I18N.getText("button.close"));
		button_close_.setToolTipText("");
		button_close_.setAlignmentY(0.0F);
		button_close_.addActionListener(this::button_close_ActionPerformed);

		text_.setEditable(false);
		text_.setColumns(20);
		text_.setFont(new java.awt.Font(Font.SANS_SERIF, Font.PLAIN, 12));
		text_.setRows(5);
		scroller_.setViewportView(text_);

		SwitchTrace.setText(I18N.getText("exception.fullTraces"));
		SwitchTrace.addActionListener(this::SwitchTraceActionPerformed);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					  .addGroup(layout.createSequentialGroup()
									  .addContainerGap()
									  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
													  .addGroup(layout.createSequentialGroup()
																	  .addComponent(SwitchTrace)
																	  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
																	  .addComponent(button_close_)
																	  .addGap(18, 18, 18)
																	  .addComponent(mail_link_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
													  .addGroup(layout.createSequentialGroup()
																	  .addComponent(label_title_, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE)
																	  .addGap(0, 255, Short.MAX_VALUE))
													  .addComponent(scroller_))
									  .addContainerGap())
		);
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
					  .addGroup(layout.createSequentialGroup()
									  .addContainerGap()
									  .addComponent(label_title_)
									  .addGap(1, 1, 1)
									  .addComponent(scroller_, javax.swing.GroupLayout.DEFAULT_SIZE, 234, Short.MAX_VALUE)
									  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
									  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
													  .addComponent(button_close_)
													  .addComponent(mail_link_, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
													  .addComponent(SwitchTrace))
									  .addContainerGap())
		);

		pack();
	}

	private void button_close_ActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt)//GEN-FIRST:event_button_close_ActionPerformed
	{
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	private void SwitchTraceActionPerformed(@SuppressWarnings("unused") java.awt.event.ActionEvent evt)
	{//GEN-FIRST:event_SwitchTraceActionPerformed
		showFullST = !showFullST;

		SwitchTrace.setText(showFullST ? I18N.getText("exception.shortTraces") : I18N.getText("exception.fullTraces"));
		text_.setText(showFullST ? fullST : shortST);
	}

	private javax.swing.JButton SwitchTrace;
	private javax.swing.JButton button_close_;
	private javax.swing.JLabel label_title_;
	private JLink mail_link_;
	private javax.swing.JScrollPane scroller_;
	private javax.swing.JTextArea text_;

	private String shortST;
	private String fullST;
	private boolean showFullST = false;

}
