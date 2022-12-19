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

import com.bw.jtools.graph.LSystemConfig;
import com.bw.jtools.graph.LSystemGraphicCommand;
import com.bw.jtools.io.IOTool;
import com.bw.jtools.properties.*;
import com.bw.jtools.ui.properties.PropertyPanelBase;
import com.bw.jtools.ui.properties.table.PropertyGroupNode;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import javax.swing.*;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class LSystemConfigPanel extends PropertyPanelBase
{
    private LSystemPanel lSystemPanel_;
    private LSystemConfig lSystemConfig_;

    public LSystemConfigPanel(LSystemPanel lpanel)
    {
        this.lSystemPanel_ = lpanel;
        this.lSystemConfig_ = lpanel.getLSystem()
                .getConfig();
        init();
    }

    protected void init()
    {
        updateProperties();

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton exp = new JButton("Export");
        JButton imp = new JButton("Import");

        exp.addActionListener(e -> {
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

    public void updateProperties()
    {
        DefaultTreeModel model = table_.getTreeModel();

        PropertyGroup visualSettings = new PropertyGroup("Visual Settings");

        addProperty(visualSettings, new PropertyBooleanValue("Border", lSystemPanel_.isDrawBorder()), value ->
        {
            Boolean n = value.getValue();
            lSystemPanel_.setDrawBorder(n);
        });

        addProperty(visualSettings, new PropertyNumberValue("Angel (degree)",
                Math.toDegrees(lSystemConfig_.angle_)), value ->
        {
            lSystemPanel_.getLSystem()
                    .getConfig().angle_ = Math.toRadians(value.getValue()
                    .doubleValue());
            lSystemPanel_.updateLSystem();
        });

        addProperty(visualSettings, new PropertyNumberValue("Delta X", lSystemConfig_.deltaX_), value ->
        {
            lSystemPanel_.getLSystem()
                    .getConfig().deltaX_ = value.getValue()
                    .doubleValue();
            lSystemPanel_.updateLSystem();
        });

        addProperty(visualSettings, new PropertyNumberValue("Delta Y", lSystemConfig_.deltaY_), value ->
        {
            lSystemPanel_.getLSystem()
                    .getConfig().deltaY_ = value.getValue()
                    .doubleValue();
            lSystemPanel_.updateLSystem();
        });

        PropertyGroup definition = new PropertyGroup("Definition");
        addProperty(definition, new PropertyStringValue("Axiom", lSystemConfig_.axiom_), value ->
        {
            lSystemPanel_.getLSystem()
                    .getConfig().axiom_ = value.getValue();
            lSystemPanel_.updateLSystem();
        });
        PropertyMapValue<Character, String> pRules = new PropertyMapValue<>("Rules", Character.class, String.class);
        pRules.putAll(lSystemConfig_.rules_);
        addProperty(definition, pRules, value ->
        {
            lSystemConfig_.rules_.clear();
            lSystemConfig_.rules_.putAll(value.getValue());
            lSystemPanel_.updateLSystem();
        });
        PropertyMapValue<Character, List<LSystemGraphicCommand>> pCommands =
                new PropertyMapValue<>("Commands",
                        Character.class, (Class<List<LSystemGraphicCommand>>)(Class<?>)List.class);
        pCommands.putAll(lSystemConfig_.commands_);
        addProperty(definition, pCommands, value ->
        {
            lSystemConfig_.commands_.clear();
            lSystemConfig_.commands_.putAll(value.getValue());
            lSystemPanel_.updateLSystem();
        });

        PropertyGroupNode root = new PropertyGroupNode(null);
        root.add(new PropertyGroupNode(visualSettings));
        root.add(new PropertyGroupNode(definition));

        model.setRoot(root);
        table_.expandAll();
    }


}
