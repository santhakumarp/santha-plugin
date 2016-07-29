import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class UserData {
    private String name = "";
    private String displayName = "";
    private String emailAddress = "";
    private String key = "";
    private Boolean active = false;
    private JsonElement userData;

    public UserData(JsonElement element) {
        if(element != null && element.getAsJsonObject().has("name")) {
            userData = element;
            this.setValues();
        }
    }

    private void setValues() {
        JsonObject userObject = userData.getAsJsonObject();
        name = CommonMethods.getStringValueFromJsonObject(userObject, "name");
        displayName = CommonMethods.getStringValueFromJsonObject(userObject, "displayName");
        emailAddress = CommonMethods.getStringValueFromJsonObject(userObject, "emailAddress");
        key = CommonMethods.getStringValueFromJsonObject(userObject, "key");
        JsonPrimitive isActive = userObject.getAsJsonPrimitive("active");
        active = isActive.getAsBoolean();
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getKey() {
        return key;
    }

    public Boolean getActive() {
        return active;
    }
}
