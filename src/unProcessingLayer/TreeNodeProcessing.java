package unProcessingLayer;

import java.io.File;
import java.util.function.Function;

import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;
import unCore.TreeNodeObject;




public final class TreeNodeProcessing {
    private TreeNodeProcessing() {}

    // interface for nodes that support JSON serialization
    public interface JSONSerializable {
        JSONObject dataToJSON(JSONObject json);
        void dataFromJSON(JSONObject data);
        String getJSONType();
    }

    // SERIALIZATION /////////////////////////////////////////////////

    public static <N extends TreeNodeObject<N> & Iterable<N> & JSONSerializable>
    JSONObject toJSON(N node) {
        JSONObject o = new JSONObject();
        o.setString("type", node.getJSONType());
        o.setJSONObject("data", node.dataToJSON(new JSONObject()));
        JSONArray kids = new JSONArray();
        for (N c : node.getChildren())
            kids.append(toJSON(c));
        o.setJSONArray("children", kids);
        return o;
    }

    public static <N extends TreeNodeObject<N> & Iterable<N> & JSONSerializable>
    N fromJSON(JSONObject o, Function<String, N> factoryFn) {
        String type = o.getString("type", null);
        if (type == null) throw new RuntimeException("Missing 'type' string at JSON node root.");

        JSONObject data = o.getJSONObject("data");
        if (data == null) data = new JSONObject();

        N n = factoryFn.apply(type);
        if (n == null) throw new RuntimeException("Factory returned null for type: " + type);

        n.dataFromJSON(data);

        JSONArray kids = o.getJSONArray("children");
        if (kids != null)
            for (int i = 0; i < kids.size(); i++)
                n.addChild(fromJSON(kids.getJSONObject(i), factoryFn));

        return n;
    }

    // FILE IO ///////////////////////////////////////////////////////

    public static <N extends TreeNodeObject<N> & Iterable<N> & JSONSerializable>
    void saveTreeJSON(N node, String filename, PApplet pa) {
        pa.saveJSONObject(toJSON(node), filename);
    }

    public static <N extends TreeNodeObject<N> & Iterable<N> & JSONSerializable>
    N loadTreeJSON(String filename, Function<String, N> factoryFn, PApplet pa) {
        return fromJSON(pa.loadJSONObject(filename), factoryFn);
    }
}

