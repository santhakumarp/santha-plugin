import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonMethods {
    public static String getStringValueFromJsonObject(JsonObject jsonData, String paramName) {
        String value = "";
        if(jsonData.get(paramName) instanceof JsonNull) {

        } else {
            JsonPrimitive jsonPrimitive = jsonData.getAsJsonPrimitive(paramName);
            value = jsonPrimitive.getAsString();
        }
        return value;
    }

    public static int getStatusValueFromString(String status) {
        int statusValue = 0;
        switch (status) {
            case "Open":
                statusValue = 1;
                break;
            case "Accepted":
                statusValue = 1;
                break;
            case "Resolved":
                statusValue = 12;
                break;
            case "Developer Testing Started":
                statusValue = 3;
                break;
            case "Code Review in Progress":
                statusValue = 5;
                break;
            case "Ready for Code Review":
                statusValue = 4;
                break;
            case "Done":
                statusValue = 12;
                break;
            case "Backlog":
                statusValue = 0;
                break;
            case "Closed":
                statusValue = 12;
                break;
            case "To do":
                statusValue = 1;
                break;
            case "Investigation under way":
                statusValue = 2;
                break;
            case "Shipped":
                statusValue = 12;
                break;
            case "SQA Required":
                statusValue = 7;
                break;
            case "QA Started":
                statusValue = 8;
                break;
            case "Ready for QA":
                statusValue = 7;
                break;
            case "Code Review Passed!":
                statusValue = 6;
                break;
            case "Pending Server Side Work":
                statusValue = 2;
                break;
            case "Available for client (Live)":
                statusValue = 11;
                break;
            case "Reopened":
                statusValue = -1;
                break;
            case "In Progress":
                statusValue = 2;
                break;
            case "Work in progress":
                statusValue = 2;
                break;
            case "Code Review Required":
                statusValue = 4;
                break;
            case "Code review":
                statusValue = 4;
                break;
            case "Waiting for build":
                statusValue = 5;
                break;
            case "Completed QA":
                statusValue = 9;
                break;
            case "Ready for release":
                statusValue = 10;
                break;
            case "Ready to Release":
                statusValue = 10;
                break;
            case "Resolved In Trunk":
                statusValue = 9;
                break;
            case "Ready for Sign Off":
                statusValue = 10;
                break;
            default:
                statusValue = 0;
                break;

        }
        return statusValue;
    }

    public static int getIntegerValueFromJsonObject(JsonObject jsonData, String paramName) {
        JsonPrimitive jsonPrimitive = jsonData.getAsJsonPrimitive(paramName);
        int value = jsonPrimitive.getAsInt();
        return value;
    }

    public static Date getDateValueFromString (String str_date) {
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd\'T\'HH:mm:ss.SSSZ");
        Date date = null;
        try {
            date = (Date)formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static long getDateTimeFromJsonObject(JsonObject jsonData, String paramName) {
        String  dateTimeString = getStringValueFromJsonObject(jsonData, paramName);
        Date date = getDateValueFromString(dateTimeString);
        long timestamp = date.getTime();
        return timestamp;
    }
}
