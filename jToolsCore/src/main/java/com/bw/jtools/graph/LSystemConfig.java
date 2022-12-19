/*
 *  (c) copyright 2022 Bernd Wengenroth
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 *
 */
package com.bw.jtools.graph;

import com.bw.jtools.io.JsonTool;

import javax.json.*;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;

public class LSystemConfig
{
    public String axiom_;
    public double angle_;
    public double deltaX_ = 5;
    public double deltaY_ = 5;
    public final Map<Character, String> rules_;
    public final Map<Character, List<LSystemGraphicCommand>> commands_ = new HashMap<>();

    public LSystemConfig(String axiom, double angle, Map<Character, String> rules)
    {
        angle_ = angle;
        rules_ = new HashMap<>(rules);
        axiom_ = axiom;
    }

    public void addDefaultCommands()
    {
        commands_.put('F', Collections.singletonList(LSystemGraphicCommand.DRAW_FORWARD));
        commands_.put('f', Collections.singletonList(LSystemGraphicCommand.MOVE_FORWARD));
        commands_.put('-', Collections.singletonList(LSystemGraphicCommand.TURN_COUNTERCLOCKWISE));
        commands_.put('+', Collections.singletonList(LSystemGraphicCommand.TURN_CLOCKWISE));
        commands_.put('[', Collections.singletonList(LSystemGraphicCommand.PUSH_ON_STACK));
        commands_.put(']', Collections.singletonList(LSystemGraphicCommand.POP_FROM_STACK));
    }

    public void fromJSONString(String json)
    {
        fromJSON(new StringReader(json));
    }

    public void fromJSON(Reader r)
    {
        JsonReader jsonReader = Json.createReader(r);
        JsonObject jo = jsonReader.readObject();
        fromJSON(jo);
    }

    public void fromJSON(JsonObject o)
    {
        axiom_ = o.getString("axiom");
        angle_ = Math.toRadians(o.getJsonNumber("angle").doubleValue());
        deltaX_ = o.getJsonNumber("deltaX").doubleValue();
        deltaY_ = o.getJsonNumber("deltaY").doubleValue();


        JsonArray ruleAr = o.getJsonArray("rules");
        rules_.clear();
        for (int i = 0; i < ruleAr.size(); ++i)
        {
            JsonObject r = ruleAr.getJsonObject(i);
            rules_.put(r.getString("char").charAt(0), r.getString("rule"));
        }

        JsonArray cmdAr = o.getJsonArray("commands");
        commands_.clear();
        for (int i = 0; i < cmdAr.size(); ++i)
        {
            JsonObject r = cmdAr.getJsonObject(i);
            char chr = r.getString("char").charAt(0);
            JsonArray ca = r.getJsonArray("commands");
            List<LSystemGraphicCommand> cmds = new ArrayList<>();
            for (int ci = 0; ci < ca.size(); ++ci)
            {
                cmds.add(LSystemGraphicCommand.valueOf(ca.getString(ci)));
            }
            commands_.put(chr, cmds);
        }
    }

    public JsonObject toJSON()
    {
        final JsonBuilderFactory bf = JsonTool.getJsonBuilderFactory();
        final JsonObjectBuilder builder = bf.createObjectBuilder();

        builder.add("axiom", axiom_);
        builder.add("angle", Math.toDegrees(angle_));
        builder.add("deltaX", deltaX_);
        builder.add("deltaY", deltaY_);

        JsonArrayBuilder rulesAB = bf.createArrayBuilder();
        for (Map.Entry<Character, String> r : rules_.entrySet())
        {
            final JsonObjectBuilder ob = bf.createObjectBuilder();
            ob.add("char", String.valueOf(r.getKey()));
            ob.add("rule", r.getValue());
            rulesAB.add(ob);
        }
        builder.add("rules", rulesAB);
        JsonArrayBuilder commandAB = bf.createArrayBuilder();
        for (Map.Entry<Character, List<LSystemGraphicCommand>> r : commands_.entrySet())
        {
            final JsonObjectBuilder ob = bf.createObjectBuilder();
            ob.add("char", String.valueOf(r.getKey()));
            List<LSystemGraphicCommand> cmds = r.getValue();
            JsonArrayBuilder arrayBuilder = bf.createArrayBuilder();
            cmds.forEach(cmd -> arrayBuilder.add(cmd.name()));
            ob.add("commands", arrayBuilder.build());
            commandAB.add(ob);
        }
        builder.add("commands", commandAB);

        return builder.build();
    }
}
