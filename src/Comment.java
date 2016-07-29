import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class Comment {
    private UserData author = new UserData(new JsonObject());
    private UserData updaterAuthor = new UserData(new JsonObject());
    private long created = 0;
    private long updated = 0;
    private String body = "";
    private JsonElement comment;

    public Comment(JsonElement element) {
        if(element != null && element.getAsJsonObject().has("created")) {
            comment = element;
            this.setValues();
        }
    }

    private void setValues() {
        JsonObject commentObject = comment.getAsJsonObject();
        created = CommonMethods.getDateTimeFromJsonObject(commentObject, "created");
        updated = CommonMethods.getDateTimeFromJsonObject(commentObject, "updated");
        body = CommonMethods.getStringValueFromJsonObject(commentObject, "body");
        author = new UserData(commentObject.get("author"));
        updaterAuthor = new UserData(commentObject.get("updateAuthor"));
    }

    public UserData getAuthor() {
        return author;
    }

    public UserData getUpdaterAuthor() {
        return updaterAuthor;
    }

    public long getCreated() {
        return created;
    }

    public long getUpdated() {
        return updated;
    }

    public String getBody() {
        return body;
    }
}
