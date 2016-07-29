import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ChangeLog {
    private String field = "";
    private String fieldType = "";
    private String from = "";
    private String fromString = "";
    private String to = "";
    private String toString = "";
    private JsonElement changeLog;

    public ChangeLog(JsonElement element) {
        if(element != null && element.getAsJsonObject().has("field")) {
            changeLog = element;
            this.setValues();
        }
    }

    private void setValues() {
        JsonObject changeLogObject = changeLog.getAsJsonObject();
        field = CommonMethods.getStringValueFromJsonObject(changeLogObject, "field");
        fieldType = CommonMethods.getStringValueFromJsonObject(changeLogObject, "fieldtype");
        from = CommonMethods.getStringValueFromJsonObject(changeLogObject, "from");
        to = CommonMethods.getStringValueFromJsonObject(changeLogObject, "to");
        fromString = CommonMethods.getStringValueFromJsonObject(changeLogObject, "fromString");
        toString = CommonMethods.getStringValueFromJsonObject(changeLogObject, "toString");
    }

    public String getToString() {
        return toString;
    }

    public String getField() {
        return field;
    }

    public String getFieldType() {
        return fieldType;
    }

    public String getFrom() {
        return from;
    }

    public String getFromString() {
        return fromString;
    }

    public String getTo() {
        return to;
    }
}
