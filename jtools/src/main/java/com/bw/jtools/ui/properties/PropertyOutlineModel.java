/*
 * (c) copyright 2015-2019 Bernd Wengenroth
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
package com.bw.jtools.ui.properties;

import javax.swing.tree.DefaultTreeModel;
import org.netbeans.swing.outline.DefaultOutlineModel;

public class PropertyOutlineModel extends DefaultOutlineModel
{
      /** For unknown reasons this member is package-protected in DefaultOutlineModel. */
      protected DefaultTreeModel treeModel_;

      /** For unknown reasons this member is package-protected in DefaultOutlineModel. */
      protected PropertyTableModel tableModel_;

      protected PropertyOutlineModel(DefaultTreeModel treeModel, PropertyTableModel tableModel)
      {
          super(treeModel, tableModel, true, "Properties");
          this.tableModel_ = tableModel;
          this.treeModel_ = treeModel;
          tableModel.setOutlineModel(this);
      }

      /**
       * Getter for Tree Model.<br>
       * For unknown reasons getTreeModel member function and the member is package-protected
       * in DefaultOutlineModel.
       * @return The current tree model.
       */
      public DefaultTreeModel getDefaultTreeModel()
      {
          return treeModel_;
      }

      /**
       * Getter for Tree Model.<br>
       * For unknown reasons getTableModel member function and the member is package-protected
       * in DefaultOutlineModel.
       * @return The current table model.
       */
      public PropertyTableModel getDefaultTableModel()
      {
          return tableModel_;
      }

      public static PropertyOutlineModel createOutlineModel()
      {
            DefaultTreeModel treeModel = new DefaultTreeModel(null);
            PropertyTableModel tableModel = new PropertyTableModel();
            return new PropertyOutlineModel(treeModel, tableModel );
      }
}
