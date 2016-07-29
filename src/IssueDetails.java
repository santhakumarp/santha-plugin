import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class IssueDetails {
    private String project = "";
    private String projectKey = "";
    private String ticketNumber = "";
    private String type = "";
    private String status = "";
    private String priority = "";
    private long created = 0;
    private long updated = 0;
    private String summary = "";
    private String description = "";
    private int statusId = 0;
    private int statusValue = 0;
    private UserData assignee = new UserData(new JsonObject());
    private UserData reporter = new UserData(new JsonObject());
    private JsonElement issueDetails;
    private JsonObject fields;
    public float timeToComplete = 0;

    public IssueDetails(JsonElement element) {
        if(element != null && element.getAsJsonObject().has("key")) {
            issueDetails = element;
            this.setValues();
        }
    }

    private void setValues() {
        JsonObject issueObject = issueDetails.getAsJsonObject();
        ticketNumber = CommonMethods.getStringValueFromJsonObject(issueObject, "key");

        fields = issueObject.getAsJsonObject("fields");

        reporter = new UserData(fields.get("reporter"));
        assignee = new UserData(fields.get("assignee"));
        created = CommonMethods.getDateTimeFromJsonObject(fields, "created");
        updated = CommonMethods.getDateTimeFromJsonObject(fields, "updated");
        description = CommonMethods.getStringValueFromJsonObject(fields, "description");
        summary = CommonMethods.getStringValueFromJsonObject(fields, "summary");

        JsonObject issueType = fields.getAsJsonObject("issuetype");
        type = CommonMethods.getStringValueFromJsonObject(issueType, "name");

        JsonObject priorityObject = fields.getAsJsonObject("priority");
        priority = CommonMethods.getStringValueFromJsonObject(priorityObject, "name");

        JsonObject statusObject = fields.getAsJsonObject("status");
        status = CommonMethods.getStringValueFromJsonObject(statusObject, "name");
        statusValue = CommonMethods.getStatusValueFromString(status);
        statusId = CommonMethods.getIntegerValueFromJsonObject(statusObject, "id");

        JsonObject projectObject = fields.getAsJsonObject("project");
        project = CommonMethods.getStringValueFromJsonObject(projectObject, "name");
        projectKey = CommonMethods.getStringValueFromJsonObject(projectObject, "key");
    }

    public void setStatus(String statusString) {
        status = statusString;
        statusValue = CommonMethods.getStatusValueFromString(status);
    }

    public int getStatusValue() {
        return statusValue;
    }

    public int getStatusId() {
        return statusId;
    }

    public JsonObject getFields() {
        return fields;
    }

    public String getProject() {
        return project;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public long getCreated() {
        return created;
    }

    public long getUpdated() {
        return updated;
    }

    public String getSummary() {
        return summary;
    }

    public String getDescription() {
        return description;
    }

    public UserData getAssignee() {
        return assignee;
    }

    public UserData getReporter() {
        return reporter;
    }
}
