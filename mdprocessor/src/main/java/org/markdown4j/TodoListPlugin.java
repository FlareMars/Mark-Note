package org.markdown4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FlareMars on 2016/11/9.
 */
public class TodoListPlugin extends Plugin {

    private int globalId = 0;

    private static final Integer MARKER_FINISHED_ONE = 0;
    private static final Integer MARKER_NOT_FINISHED_ONE = 1;
    private static final Integer MARKER_FINISHED_TWO = 2;
    private static final Integer MARKER_NOT_FINISHED_TWO = 3;
    private static final Map<String, Integer> TODO_MARKERS = new HashMap<>(4);
    static {
        TODO_MARKERS.put("*[x]", MARKER_FINISHED_ONE);
        TODO_MARKERS.put("*[]", MARKER_NOT_FINISHED_ONE);
        TODO_MARKERS.put("-", MARKER_FINISHED_TWO);
        TODO_MARKERS.put("+", MARKER_NOT_FINISHED_TWO);
    }

    private static final String OUTPUT_FORMATOR = "<div class=\"md-checkbox\"><input id=\"%s\" type=\"checkbox\" %s /><label for=\"%s\"></label><p id=\"%s\" class=\"cb-label\">%s</p></div>\n";

    public TodoListPlugin() {
        super("todoList");
    }

    @Override
    public void emit(StringBuilder out, List<String> lines, Map<String, String> params) {
        for (int i = 0, size = lines.size();i < size;i++) {
            String line = lines.get(i);
            int firstSpaceIndex = line.indexOf(" ");
            Integer markerId = null;
            if (firstSpaceIndex > 0 && (markerId = TODO_MARKERS.get(line.substring(0, firstSpaceIndex))) != null) {
                if (line.length() - firstSpaceIndex > 1) { // at least one char
                    String id = "checkbox_" + (globalId++);
                    if (markerId.equals(MARKER_FINISHED_ONE) || markerId.equals(MARKER_FINISHED_TWO)) {
                        out.append(String.format(OUTPUT_FORMATOR, id, "checked", id, id + "_label", line.substring(firstSpaceIndex + 1)));
                    } else if (markerId.equals(MARKER_NOT_FINISHED_ONE) || markerId.equals(MARKER_NOT_FINISHED_TWO)) {
                        out.append(String.format(OUTPUT_FORMATOR, id, "", id, id + "_label", line.substring(firstSpaceIndex + 1)));
                    }
                }
            } else { // wrong format
                out.append("wrong format or invalid content: \"");
                out.append(line);
                out.append("\"\n");
            }
        }
    }
}
