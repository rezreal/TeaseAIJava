package me.goddragon.teaseai.api.statistics;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CustomSerializer implements JsonSerializer<ArrayList<StatisticsBase>> {

    protected static Map<String, Class> map = new TreeMap<String, Class>();

    static {
        map.put("Edge", JavaEdge.class);
        map.put("Response", JavaResponse.class);
        map.put("Module", JavaModule.class);
        map.put("Stroke", JavaStroke.class);
        map.put("StatisticsBase", StatisticsBase.class);
        map.put("EdgeHold", JavaEdgeHold.class);
    }

    @Override
    public JsonElement serialize(ArrayList<StatisticsBase> src, Type typeOfSrc,
            JsonSerializationContext context) {
        if (src == null)
            return null;
        else {
            JsonArray ja = new JsonArray();
            for (int i = 0; i < src.size(); i++) {
                if (!(src.get(i) instanceof StatisticsBase))
                {
                    if ((Object)src.get(i) instanceof String)
                    {
                        ja.add(context.serialize(src.get(i), String.class));
                    }
                    else if ((Object)src.get(i) instanceof Integer)
                    {
                        ja.add(context.serialize(src.get(i), Integer.class));
                    }
                    else if ((Object)src.get(i) instanceof Double)
                    {
                        ja.add(context.serialize(src.get(i), Double.class));
                    }
                    else if ((Object)src.get(i) instanceof Boolean)
                    {
                        ja.add(context.serialize(src.get(i), Boolean.class));
                    }
                    else if ((Object)src.get(i) instanceof Object)
                    {
                        ja.add(context.serialize(src.get(i), Object.class));
                    }
                    continue;
                }
                StatisticsBase bc = src.get(i);
                
                Class c = map.get(bc.isA);
                if (c == null)
                    throw new RuntimeException("Unknown class: " + bc.isA);
                ja.add(context.serialize(bc, c));

            }
            return ja;
        }
    }
}