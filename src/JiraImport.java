import java.io.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.google.gson.*;
import java.lang.String;

public class JiraImport extends HttpServlet {
    private JsonElement recordsFromJira;

    public void importIssuesToInfluxDB() {
        JsonObject getJsonObj;
        getJsonObj = recordsFromJira.getAsJsonObject();
        JsonArray jsonArray = getJsonObj.getAsJsonArray("issues");
        InfluxDataBase influxDataBase = new InfluxDataBase("stardb");
        Comment commentObject = new Comment(new JsonObject());
        for (int i = 0, size = jsonArray.size(); i < size; i++)
        {
            JsonElement issue = jsonArray.get(i);
            IssueDetails issueDetails = new IssueDetails(issue);
            JsonObject issueObject = issue.getAsJsonObject();
            JsonObject changeLogObject = issueObject.getAsJsonObject("changelog");

            JsonArray histories;

            if(changeLogObject == null) {
                histories = new JsonArray();
                histories.add(new JsonObject());
            } else {
                histories = changeLogObject.getAsJsonArray("histories");
            }

            ChangeLog changeLog = new ChangeLog(new JsonObject());
            JsonObject fields = issueDetails.getFields();
            UserData userData = new UserData(fields.get("assignee"));
            String dateTime = CommonMethods.getStringValueFromJsonObject(fields, "created");
            Date createdDateTime = CommonMethods.getDateValueFromString(dateTime);
            long timestamp = createdDateTime.getTime();
            issueDetails.setStatus("Open");
            influxDataBase.createInfluxRecords(timestamp, userData, issueDetails, changeLog, commentObject);

            if(histories != null) {
                for (int historyCount = 0, historySize = histories.size(); historyCount < historySize; historyCount++) {
                    JsonElement historyElement = histories.get(historyCount);
                    JsonObject historyObj = historyElement.getAsJsonObject();
                    JsonObject user = historyObj.getAsJsonObject("author");
                    UserData userData1 = new UserData(user);
                    JsonArray items = historyObj.getAsJsonArray("items");
                    String dateTimeVal = CommonMethods.getStringValueFromJsonObject(historyObj, "created");
                    Date createdTime = CommonMethods.getDateValueFromString(dateTimeVal);
                    long timestamp1 = createdTime.getTime();

                    if(items != null) {
                        for (int itemCount = 0, itemSize = items.size(); itemCount < itemSize; itemCount++) {
                            JsonElement itemElement = items.get(itemCount);
                            ChangeLog changeLogData = new ChangeLog(itemElement);
                            String changeLogField = changeLogData.getField();
                            String fieldToCheck = "status";
                            if(fieldToCheck.equals(changeLogField)) {
                                issueDetails.setStatus(changeLogData.getToString());
                                String toStringValue = changeLogData.getToString();
                                if(toStringValue.equals("Closed")) {
                                    issueDetails.timeToComplete = (timestamp1 - timestamp) / 3600000;//To convert to hour
                                }
                            }
                            influxDataBase.createInfluxRecords(timestamp1, userData1, issueDetails, changeLogData, commentObject);
                        }
                    }
                }
            }
            influxDataBase.writeAllData();
        }

    }

    public void insertPostDataFromWebHook(JsonElement jsonData) {
        JsonObject dataObject = jsonData.getAsJsonObject();

        JsonObject issue = dataObject.getAsJsonObject("issue");
        IssueDetails issueDetails = new IssueDetails(issue);

        JsonObject changelog = dataObject.getAsJsonObject("changelog");

        JsonObject user = dataObject.getAsJsonObject("user");
        UserData userData = new UserData(user);

        JsonObject comment = dataObject.getAsJsonObject("comment");
        Comment commentObject = new Comment(comment);

        JsonArray items;

        if(changelog == null) {
            items = new JsonArray();
            items.add(new JsonObject());
        } else {
            items = changelog.getAsJsonArray("items");
        }

        if(items == null) {
            items = new JsonArray();
            items.add(new JsonObject());
        }

        JsonPrimitive timeStampVal = dataObject.getAsJsonPrimitive("timestamp");
        long timestamp = timeStampVal.getAsLong();

        InfluxDataBase influxDataBase = new InfluxDataBase("team_activities");

        for (int i = 0, size = items.size(); i < size; i++) {
            JsonElement itemElement = items.get(i);
            ChangeLog changeLog = new ChangeLog(itemElement);
            influxDataBase.createInfluxRecords(timestamp, userData, issueDetails, changeLog, commentObject);
        }
        influxDataBase.writeAllData();
    }

    public JsonElement getJsonFromInputStream(InputStream postData) throws IOException {
        char c;
        int i;
        String jsonString = "";

        while((i=postData.read())!=-1)
        {
            c=(char)i;
            jsonString += Character.toString(c);
        }
        JsonParser parser = new JsonParser();
        JsonElement postValue = parser.parse(jsonString);
        return postValue;
    }

    public String importRecordsFromJira(int startsAt, int maxResults) {
        String outputToShow = "";
        String output;
        try {
            //updatedDate>=startOfWeek()
            URL url = new URL("https://commusoft.atlassian.net/rest/api/2/search?expand=changelog&jql=project=\"Online+Application+v4\"+order+by+created+DESC&maxResults="+maxResults+"&startAt="+startsAt);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Cookie", " studio.crowd.tokenkey=SIUxpvVsM02ZMUdDZhIekw00; Domain=.commusoft.atlassian.net; Path=/; Secure; HttpOnly");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            while ((output = br.readLine()) != null) {
                outputToShow += output;
            }


            JsonParser parser = new JsonParser();
            recordsFromJira = parser.parse(outputToShow);

            conn.disconnect();

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return outputToShow;
    }
}
