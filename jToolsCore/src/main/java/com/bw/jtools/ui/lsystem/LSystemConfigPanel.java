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
package com.bw.jtools.ui.lsystem;

import com.bw.jtools.io.IOTool;
import com.bw.jtools.lsystem.LSystem;
import com.bw.jtools.lsystem.LSystemConfig;
import com.bw.jtools.lsystem.LSystemGraphicCommand;
import com.bw.jtools.properties.PropertyBooleanValue;
import com.bw.jtools.properties.PropertyGroup;
import com.bw.jtools.properties.PropertyListValue;
import com.bw.jtools.properties.PropertyMapValue;
import com.bw.jtools.properties.PropertyNumberValue;
import com.bw.jtools.properties.PropertyStringValue;
import com.bw.jtools.properties.PropertyValue;
import com.bw.jtools.ui.properties.PropertyPanelBase;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.tree.DefaultTreeModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class LSystemConfigPanel extends PropertyPanelBase
{
    private LSystemPanel lSystemPanel_;

    public LSystemConfigPanel(LSystemPanel lpanel)
    {
        this.lSystemPanel_ = lpanel;
        init();
    }

    private LSystemConfig getConfig()
    {
        return lSystemPanel_.getLSystem()
                            .getConfig();
    }

    protected void init()
    {
        updateProperties();

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton exp = new JButton("Export");
        JButton imp = new JButton("Import");

        exp.addActionListener(e ->
        {
            File file = IOTool.selectFile(this, "lsystem.export.lastDir", "Import L-System Config",
                    IOTool.SAVE, IOTool.getFileFilterJson());
            if (file != null)
            {

                try (OutputStream os = new FileOutputStream(file))
                {
                    OutputStreamWriter w = new OutputStreamWriter(os, StandardCharsets.UTF_8);
                    JsonWriter jsonWriter = Json.createWriter(w);
                    JsonObject jo = lSystemPanel_.getLSystem().getConfig().toJSON();
                    jsonWriter.write(jo);
                    w.flush();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });


        imp.addActionListener(e -> {
            File file = IOTool.selectFile(this, "lsystem.export.lastDir", "Export L-System Config",
                    IOTool.OPEN, IOTool.getFileFilterJson());
            if (file != null)
            {
                try (InputStream is = new FileInputStream(file))
                {
                    InputStreamReader r = new InputStreamReader(is, StandardCharsets.UTF_8);
                    BufferedReader nr = new BufferedReader(r);
                    lSystemPanel_.getLSystem().getConfig().fromJSON(nr);
                    updateProperties();
                    lSystemPanel_.updateLSystem();
                } catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }
        });


        buttons.add(imp);
        buttons.add(exp);

        add(buttons, BorderLayout.SOUTH);
    }

    /**
     * Updates the property tree from the L-System-Configuration.
     */
    public void updateProperties()
    {
        DefaultTreeModel model = table_.getTreeModel();
        LSystemConfig cfg = getConfig();

        PropertyGroup visualSettings = new PropertyGroup("Visual Settings");

        addProperty(visualSettings, new PropertyBooleanValue("Border", lSystemPanel_.isDrawBorder()), value ->
        {
            Boolean n = value.getValue();
            lSystemPanel_.setDrawBorder(n);
        });

        addProperty(visualSettings, new PropertyNumberValue("Angel (degree)",
                Math.toDegrees(getConfig().angle_)), value ->
        {
            getConfig().angle_ = Math.toRadians(value.getValue()
                                                     .doubleValue());
            updateLSystem();
        });

        addProperty(visualSettings, new PropertyNumberValue("Delta X", cfg.deltaX_), value ->
        {
            getConfig().deltaX_ = value.getValue()
                                       .doubleValue();
            updateLSystem();
        });

        addProperty(visualSettings, new PropertyNumberValue("Delta Y", cfg.deltaY_), value ->
        {
            getConfig().deltaY_ = value.getValue()
                                       .doubleValue();
            updateLSystem();
        });

        PropertyGroup definition = new PropertyGroup("Definition");
        addProperty(definition, new PropertyStringValue("Axiom", cfg.axiom_), value ->
        {
            getConfig().axiom_ = value.getValue();
            updateLSystem();
        });
        PropertyMapValue<Character, String> pRules = new PropertyMapValue<>("Rules", Character.class, String.class);
        pRules.putAll(getConfig().rules_);
        addProperty(definition, pRules, value ->
        {
            LSystemConfig lcfg = getConfig();
            lcfg.rules_.clear();
            value.getValue()
                 .forEach((c, pv) -> lcfg.rules_.put(c, pv.getValue()));
            updateLSystem();
        });
        PropertyMapValue<Character, List<PropertyValue<LSystemGraphicCommand>>> pCommands =
                new PropertyMapValue<>(
                        "Commands",
                        Character.class,
                        PropertyListValue.class);
        getConfig().commands_.forEach((c, cmdList) ->
        {
            PropertyListValue<LSystemGraphicCommand> l =
                    new PropertyListValue<>(c.toString(), LSystemGraphicCommand.class);
            cmdList.forEach(l::add);
            pCommands.putProperty(c, l);
        });
        addProperty(definition, pCommands, value ->
        {
            LSystemConfig lcfg = getConfig();
            lcfg.commands_.clear();
            value.getValue()
                 .forEach((c, cmdList) ->
                 {
                     List<PropertyValue<LSystemGraphicCommand>> pl = cmdList.getValue();
                     List<LSystemGraphicCommand> l = new ArrayList<>(pl.size());
                     pl.forEach(prop -> l.add(prop.getValue()));
                     lcfg.commands_.put(c, l);
                 });
            updateLSystem();
        });

        PropertyGroupNode root = new PropertyGroupNode(null);
        root.add(new PropertyGroupNode(visualSettings));
        root.add(new PropertyGroupNode(definition));

        model.setRoot(root);
        table_.expandAll();
    }

    private void updateLSystem()
    {
        LSystem ls = lSystemPanel_.getLSystem();
        int gc = ls.getGenerations();
        ls.reset();
        while (gc > 0)
        {
            ls.generation();
            --gc;
        }
        lSystemPanel_.updateLSystem();
    }


}
