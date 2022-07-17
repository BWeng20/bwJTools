/*
 * (c) copyright 2022 Bernd Wengenroth
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
package com.bw.jtools.ui.graph;

import com.bw.jtools.Application;
import com.bw.jtools.graph.Graph;
import com.bw.jtools.graph.GraphUtil;
import com.bw.jtools.graph.Node;
import com.bw.jtools.image.ImageTool;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.io.data.DataInputStream;
import com.bw.jtools.io.data.DataOutputStream;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyNumberValue;
import com.bw.jtools.shape.ShapePane;
import com.bw.jtools.ui.I18N;
import com.bw.jtools.ui.JExceptionDialog;
import com.bw.jtools.ui.JLAFComboBox;
import com.bw.jtools.ui.JPaintViewport;
import com.bw.jtools.ui.SettingsUI;
import com.bw.jtools.ui.UIToolSwing;
import com.bw.jtools.ui.icon.IconTool;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import com.bw.jtools.ui.graph.DecoratorShape.DecoratorShapeDefinition;
import com.bw.jtools.ui.graph.DecoratorShape.DecoratorShapeDefinition.SVGDefinition;
import com.bw.jtools.ui.graph.impl.CloudNodeDecorator;
import com.bw.jtools.ui.graph.impl.DecoratorNodeVisual;
import com.bw.jtools.ui.graph.impl.NodeLabelVisual;
import com.bw.jtools.ui.graph.impl.ShapeEdgeVisual;
import com.bw.jtools.ui.graph.impl.ShapeNodeDecorator;
import com.bw.jtools.ui.graph.impl.TreeLayout;
import com.bw.jtools.ui.graph.impl.TreeRectangleGeometry;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;
import com.bw.jtools.ui.properties.table.PropertyTable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultTreeModel;

public class DecoratorEditor extends JComponent
{

    protected String preference_prefix_ = "DecoratorEditor.";
    protected DecoratorShapeDefinition dsd_ = new DecoratorShapeDefinition();

    protected final class SVGTableModel extends AbstractTableModel
    {
        public List<SVGDefinition> svgs_ = new ArrayList<>();
        
        @Override
        public int getRowCount()
        {
            return svgs_.size();
        }

        @Override
        public int getColumnCount()
        {
            return 1;
        }

        @Override
        public Object getValueAt(int row, int col)
        {
            return svgs_.get(row).name_;
        }
    };
    
    protected final SVGTableModel svgsModel_ = new SVGTableModel();
    
    protected final DefaultTableModel sequenceModel_ = new DefaultTableModel();
    protected final PropertyTable props_ = new PropertyTable();
    protected final GraphPanel preview_ = new GraphPanel();

    public void loadDecoratorDefinition(File decoratorFile)
    {
        DecoratorShapeDefinition dsd = null;
        try
        {
            InputStream s = new BufferedInputStream(new FileInputStream(decoratorFile));
            DataInputStream is = new DataInputStream(s);
            dsd = new DecoratorShapeDefinition();
            dsd.read(is);
            is.close();

        } catch (Exception e)
        {
            e.printStackTrace();
            dsd = null;
        }
        if (dsd != null)
        {
            dsd_ = dsd;
            svgsModel_.svgs_.clear();
            for (DecoratorShapeDefinition.SVGDefinition s : dsd_.svg_)
            {
                svgsModel_.svgs_.add( s );
            }
            svgsModel_.fireTableDataChanged();

            sequenceModel_.setRowCount(0);
            for (int i : dsd_.sequence_)
            {
                sequenceModel_.addRow(new String[]
                {
                    String.valueOf(i)
                });
            }
        }

    }

    public void loadDecoratorFile()
    {
        File selectedDecoratorFile
                = IOTool.selectFile(this, preference_prefix_ + "select.file",
                        I18N.getText("decoratoreditor.dialog.load"),
                        IOTool.OPEN,
                        new FileNameExtensionFilter(I18N.getText("filefilter.decoratorfile"),
                                "deco", "dec"), null);
        if (selectedDecoratorFile != null)
        {
            loadDecoratorDefinition(selectedDecoratorFile);
        }
    }

    public void addSvgFile()
    {
        File selectedSVGFile
                = IOTool.selectFile(this, preference_prefix_ + "select.svg",
                        I18N.getText("decoratoreditor.dialog.loadSvg"),
                        IOTool.OPEN,
                        new FileNameExtensionFilter(I18N.getText("filefilter.svgfile"),
                                "svg"), null);

        if (selectedSVGFile != null)
        {
            try
            {
                byte d[] = Files.readAllBytes(selectedSVGFile.toPath());
                String svgSource = new String(d, StandardCharsets.UTF_8);
                svgsModel_.svgs_.add( new SVGDefinition( selectedSVGFile.getName(), svgSource ));
                svgsModel_.fireTableDataChanged();
                updateExample();
            } catch (Exception ex)
            {
                JExceptionDialog d = new JExceptionDialog(this, ex);
                d.setLocationByPlatform(true);
                d.setVisible(true);
            }
        }
    }

    public void addSequenceEntry()
    {
        sequenceModel_.addRow(new String[]
        {
            String.valueOf(sequenceModel_.getRowCount() + 1)
        });
        updateExample();
    }

    /**
     * Updates the visible example from the data in the editors.
     */
    protected void updateExample()
    {
        final int segN = sequenceModel_.getRowCount();
        dsd_.sequence_ = new int[segN];
        for (int i = 0; i < segN; ++i)
        {
            String v = (String) sequenceModel_.getValueAt(i, 0);
            dsd_.sequence_[i] = v == null ? 0 : Integer.parseInt(v);
        }

        final int svgN = svgsModel_.getRowCount();
        dsd_.svg_ = new DecoratorShapeDefinition.SVGDefinition[svgN];
        for (int i = 0; i < svgN; ++i)
        {
            String name = (String) svgsModel_.getValueAt(i, 0);
            String src = (String) svgsModel_.getValueAt(i, 1);
            dsd_.svg_[i]
                    = new DecoratorShapeDefinition.SVGDefinition(
                            name, src
                    );
        }
        // preview_.setG
        
    }
    
   
    protected void createGraph()
    {
        Graph g = preview_.getGraph();
        g.setRoot(null);

        VisualSettings settings = new VisualSettings();
        Layout layout = new TreeLayout(new TreeRectangleGeometry());

        NodeLabelVisual v = new NodeLabelVisual(layout, settings);
        ShapeEdgeVisual ev = new ShapeEdgeVisual(layout, settings);

        preview_.setNodeVisual(v);
        preview_.setEdgeVisual(ev);
        
        v.getGeometry().beginUpdate();
        
        Node root = GraphUtil.createTextNode( "Grandson ");
        g.setRoot(root);

        Node son = GraphUtil.createTextNode("Son");
        Node father = GraphUtil.createTextNode("Father");
        Node mother = GraphUtil.createTextNode("Mother");

        g.addEdge(root, son);
        g.addEdge(son, father);
        g.addEdge(son, mother);

        v.getGeometry().endUpdate();
    }

    public DecoratorEditor()
    {

        sequenceModel_.setColumnCount(1);

        PropertyGroup p = new PropertyGroup("X");
        p.addProperty(new PropertyNumberValue("D", 12));

        DefaultTreeModel model = props_.getTreeModel();
        model.setRoot(new PropertyGroupNode(p));

        JButton load = UIToolSwing.createI18NTextButton("decoratoreditor.button.load");

        load.addActionListener((evt)
                ->
        {
            loadDecoratorFile();
        });

        JButton save = UIToolSwing.createI18NTextButton("decoratoreditor.button.save");
        save.addActionListener((evt)
                ->
        {
            if (dsd_ != null)
            {
                File selectedDecoratorFile
                        = IOTool.selectFile(this, preference_prefix_ + "select.file",
                                I18N.getText("decoratoreditor.dialog.save"),
                                IOTool.OPEN,
                                new FileNameExtensionFilter(I18N.getText("filefilter.decoratorfile"),
                                        "deco", "dec"), null);
                if (selectedDecoratorFile != null)
                {
                    try
                    {
                        OutputStream s = new BufferedOutputStream(new FileOutputStream(selectedDecoratorFile));
                        DataOutputStream is = new DataOutputStream(s);
                        dsd_.write(is,0);
                        is.close();
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        JSplitPane editPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        editPanel.setPreferredSize(new Dimension(400, 200));

        JPanel configPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.NORTHWEST;
        c.weighty = 1;
        c.weightx = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        JTable svgs = new JTable();

        svgs.setModel(svgsModel_);
        svgs.setTableHeader(null);
        JScrollPane svgsPane = new JScrollPane(svgs);
        configPanel.add(svgsPane, c);
        svgsPane.setPreferredSize(new Dimension(0, 0));

        c.weighty = 0;
        c.weightx = 0;
        c.gridy += c.gridheight;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        JButton addSvg = UIToolSwing.createIconRolloverButton(DecoratorEditor.class, "plus");
        addSvg.setPreferredSize(new Dimension(16, 16));
        addSvg.addActionListener((e) ->
        {
            addSvgFile();
        });
        configPanel.add(addSvg, c);
        JButton removeSvg = UIToolSwing.createIconRolloverButton(DecoratorEditor.class, "remove");
        removeSvg.addActionListener((e) ->
        {
            svgsModel_.svgs_.remove(svgs.getSelectedRow());
            svgsModel_.fireTableDataChanged();
            updateExample();
        });
        removeSvg.setEnabled(false);
        svgs.getSelectionModel().addListSelectionListener((e) ->
        {
            removeSvg.setEnabled(-1 != svgs.getSelectedRow());
        });

        removeSvg.setPreferredSize(new Dimension(16, 16));
        ++c.gridx;
        configPanel.add(removeSvg, c);

        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;
        ++c.gridy;
        c.weightx = 1;
        c.gridx = 0;
        c.gridwidth = 2;

        JTable seqList = new JTable(sequenceModel_);
        seqList.setTableHeader(null);
        JScrollPane seqPane = new JScrollPane(seqList);
        seqPane.setPreferredSize(new Dimension(0, 0));
        configPanel.add(seqPane, c);

        c.weighty = 0;
        c.weightx = 0;
        c.gridy += c.gridheight;
        c.gridwidth = 1;
        c.fill = GridBagConstraints.NONE;
        JButton addSeq = UIToolSwing.createIconRolloverButton(DecoratorEditor.class, "plus");
        addSeq.addActionListener((e) ->
        {
            addSequenceEntry();
        });

        configPanel.add(addSeq, c);
        JButton removeSeg = UIToolSwing.createIconRolloverButton(DecoratorEditor.class, "remove");
        ++c.gridx;
        configPanel.add(removeSeg, c);

        removeSeg.setEnabled(false);
        seqList.getSelectionModel().addListSelectionListener((e) ->
        {
            removeSeg.setEnabled(0 <= seqList.getSelectedRow());
        });
        removeSeg.addActionListener((e) ->
        {
            sequenceModel_.removeRow(seqList.getSelectedRow());
            updateExample();
        });

        c.gridy += c.gridheight;
        c.gridheight = 5;
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 2;
        c.weightx = 1;
        c.gridx = 0;
        c.gridwidth = 2;

        JScrollPane propsPane = new JScrollPane(props_);
        propsPane.setPreferredSize(new Dimension(200, 0));
        configPanel.add(propsPane, c);

        JScrollPane previewPanel = new JScrollPane();
        JPaintViewport vp = new JPaintViewport();
        vp.setBackgroundImage(ImageTool.createCheckerboardImage(Color.WHITE, new Color(230, 230, 230), 10, 10));
        vp.setOpaque(true);
        previewPanel.setViewport(vp);
        previewPanel.setViewportView(preview_);
        preview_.setOpaque(false);

        editPanel.setLeftComponent(configPanel);
        editPanel.setRightComponent(previewPanel);

        setLayout(new BorderLayout());
        add(editPanel, BorderLayout.CENTER);
        
        createGraph();
    }

    public static void main(String[] args)
    {
        // Initialize library.
        Application.initialize(DecoratorEditor.class);
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e)
        {
        }

        JFrame frame = new JFrame("Decorator Editor");

        DecoratorEditor editor = new DecoratorEditor();

        JPanel status = new JPanel(new BorderLayout());
        JLAFComboBox lafCB = new JLAFComboBox();
        status.add(lafCB, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(editor, BorderLayout.CENTER);
        mainPanel.add(status, BorderLayout.SOUTH);
        frame.setContentPane(mainPanel);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setIconImages(IconTool.getAppIconImages());
        frame.pack();

        // Restore window-position and dimension from preferences.
        SettingsUI.loadWindowPosition(frame);
        SettingsUI.storePositionAndFlushOnClose(frame);

        frame.setVisible(true);
    }
}
