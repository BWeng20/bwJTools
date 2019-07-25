/*
 * (c) copyright 2015-2019 Bernd Wengenroth
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

import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.swing.WindowConstants;

import com.bw.jtools.io.IOTool;
import com.bw.jtools.persistence.Store;

/**
 * Dialog to show exception details.
 *
 */
public class JExceptionDialog extends javax.swing.JDialog
{
   /**
    * Creates new form JExceptionDialog
     * @param parent The parent for the dialog. Normally the main window of the application.
     * @param throwed The Throwable to show.
    */
   public JExceptionDialog(java.awt.Window parent, Throwable throwed)
   {
      super(parent, ModalityType.APPLICATION_MODAL);
      initComponents();
      setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

      setIconImages( IconCache.getAppIconImages());

      String msg = throwed.getMessage();
      if (msg == null || msg.isEmpty()) msg = throwed.getClass().getSimpleName();

      setTitle( UITool.formatI18N("exception.title", msg));

      shortST = IOTool.getRestrictedStackTrace(throwed,"", 10);
      fullST  = IOTool.getRestrictedStackTrace(throwed,"", 2000);

      text_.setText(shortST);

      try
      {
         mail_link_.setUri("mailto:Bernd.Wengenroth@gmx.de?subject="+Store.AppName+"%20Exception&body="+URLEncoder.encode(shortST, "UTF-8"));
      }
      catch (UnsupportedEncodingException ex)
      {
      }
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
    * content of this method is always regenerated by the Form Editor.
    */
    private void initComponents()//GEN-BEGIN:initComponents
    {

        label_title_ = new javax.swing.JLabel();
        mail_link_ = new com.bw.jtools.ui.JLink();
        button_close_ = new javax.swing.JButton();
        scroller_ = new javax.swing.JScrollPane();
        text_ = new javax.swing.JTextArea();
        SwitchTrace = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setModalExclusionType(java.awt.Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setPreferredSize(new java.awt.Dimension(600, 350));

        label_title_.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        label_title_.setText( UITool.getI18NText("exception.info"));
        label_title_.setAlignmentY(0.0F);

        mail_link_.setToolTipText("mailto:Bernd.Wengenroth@gmx.de");
        mail_link_.setAlias("Report Error");
        mail_link_.setUri("mailto:Bernd.Wengenroth@gmx.de");

        button_close_.setText(UITool.getI18NText("button.close") );
        button_close_.setToolTipText("");
        button_close_.setAlignmentY(0.0F);
        button_close_.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                button_close_ActionPerformed(evt);
            }
        });

        text_.setEditable(false);
        text_.setColumns(20);
        text_.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF,java.awt.Font.PLAIN, 12));
        text_.setRows(5);
        scroller_.setViewportView(text_);

        SwitchTrace.setText( UITool.getI18NText("exception.fullTraces"));
        SwitchTrace.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                SwitchTraceActionPerformed(evt);
            }
        });

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
    }//GEN-END:initComponents

    private void button_close_ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_button_close_ActionPerformed
    {//GEN-HEADEREND:event_button_close_ActionPerformed
        dispatchEvent (new WindowEvent (this, WindowEvent.WINDOW_CLOSING));
    }//GEN-LAST:event_button_close_ActionPerformed

    private void SwitchTraceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SwitchTraceActionPerformed
        showFullST = !showFullST;

        SwitchTrace.setText(showFullST ? UITool.getI18NText("exception.shortTraces") : UITool.getI18NText("exception.fullTraces"));
        text_.setText(showFullST ? fullST : shortST );
    }//GEN-LAST:event_SwitchTraceActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton SwitchTrace;
    private javax.swing.JButton button_close_;
    private javax.swing.JLabel label_title_;
    private com.bw.jtools.ui.JLink mail_link_;
    private javax.swing.JScrollPane scroller_;
    private javax.swing.JTextArea text_;
    // End of variables declaration//GEN-END:variables

    private String shortST;
    private String fullST;
    private boolean showFullST = false;

}
