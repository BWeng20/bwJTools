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
import java.util.HashMap;
import java.util.Map;

public class LSystemConfig
{
    public String axiom_;
    public double angle_;
    public double deltaX_ = 5;
    public double deltaY_ = 5;
    public final Map<Character, String> rules_;
    public final Map<Character, LSystemGraphicCommand> commands_ = new HashMap<>();

    public LSystemConfig(String axiom, double angle, Map<Character, String> rules ) {
        angle_ = angle;
        rules_ = new HashMap<>(rules);
        axiom_ = axiom;
        commands_.put('F',LSystemGraphicCommand.DRAW_FORWARD);
        commands_.put('f',LSystemGraphicCommand.MOVE_FORWARD);
        commands_.put('-',LSystemGraphicCommand.TURN_COUNTERCLOCKWISE);
        commands_.put('+',LSystemGraphicCommand.TURN_CLOCKWISE);
        commands_.put('[',LSystemGraphicCommand.PUSH_ON_STACK);
        commands_.put(']',LSystemGraphicCommand.POP_FROM_STACK);
    }

    public void fromJSON( JsonObject o)  {
        axiom_ = o.getString("axiom");
        angle_ = o.getJsonNumber("angle").doubleValue();
        deltaX_ = o.getJsonNumber("deltaX").doubleValue();
        deltaY_ = o.getJsonNumber("deltaY").doubleValue();


        JsonArray ruleAr = o.getJsonArray("rules");
        rules_.clear();
        for (int i=0 ; i<ruleAr.size(); ++i) {
            JsonObject r = ruleAr.getJsonObject(i);
            rules_.put( (char)r.getInt("char"), r.getString("rule") );
        }

        JsonArray cmdAr = o.getJsonArray("commands");
        commands_.clear();
        for (int i=0 ; i<cmdAr.size(); ++i) {
            JsonObject r = cmdAr.getJsonObject(i);
            commands_.put( (char)r.getInt("char"),
                    LSystemGraphicCommand.valueOf(r.getString("command")) );
        }
    }

    public JsonObject toJSON()  {
        final JsonBuilderFactory bf = JsonTool.getJsonBuilderFactory();
        final JsonObjectBuilder builder = bf.createObjectBuilder();

        builder.add("axiom", axiom_);
        builder.add("angle", angle_);
        builder.add("deltaX", deltaX_);
        builder.add("deltaY", deltaY_);

        JsonArrayBuilder rulesAB = bf.createArrayBuilder();
        for ( Map.Entry<Character,String> r : rules_.entrySet() )
        {
            final JsonObjectBuilder ob = bf.createObjectBuilder();
            ob.add("char", (int)r.getKey() );
            ob.add("rule", r.getValue() );
            rulesAB.add( ob );
        }
        builder.add("rules", rulesAB );
        JsonArrayBuilder commandAB = bf.createArrayBuilder();
        for ( Map.Entry<Character,LSystemGraphicCommand> r : commands_.entrySet() )
        {
            final JsonObjectBuilder ob = bf.createObjectBuilder();
            ob.add("char", r.getKey() );
            ob.add("command", r.getValue().name() );
            commandAB.add( ob );
        }
        builder.add("commands", commandAB );

        return builder.build();
    }
}
