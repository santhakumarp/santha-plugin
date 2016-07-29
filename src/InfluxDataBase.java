import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.concurrent.TimeUnit;

public class InfluxDataBase {
    private BatchPoints batchPoints;
    private String dbName;
    private InfluxDB influxDB = InfluxDBFactory.connect("http://localhost:8086", "santha", "santha123");

    public InfluxDataBase(String dbNameParam) {
        dbName = dbNameParam;
        influxDB.createDatabase(dbName);
        batchPoints = BatchPoints
                .database(dbName)
                .retentionPolicy("default")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
    }

    public void createInfluxRecords(long timestamp, UserData userData, IssueDetails issueDetails, ChangeLog changeLog, Comment commentObject) {
        Point dataToInsert = Point.measurement("issues")
                .time(timestamp, TimeUnit.MILLISECONDS)
                .tag("project", issueDetails.getProject())
                .tag("projectKey", issueDetails.getProjectKey())
                .tag("ticket", issueDetails.getTicketNumber())
                .tag("type", issueDetails.getType())
                .tag("status", issueDetails.getStatus())
                .tag("priority", issueDetails.getPriority())
                .tag("assignee", issueDetails.getAssignee().getName())
                .tag("reporter", issueDetails.getReporter().getName())
                .tag("userName", userData.getName())
                .addField("timeToComplete", issueDetails.timeToComplete)
                .addField("statusValue", issueDetails.getStatusValue())
                .addField("fromString", changeLog.getFromString())
                .addField("toString", changeLog.getToString())
                .addField("fieldName", changeLog.getField())
                .addField("statusId", issueDetails.getStatusId())
                //.addField("summary", issueDetails.getSummary())
                .addField("created", issueDetails.getCreated())
                .addField("updated", issueDetails.getUpdated())
                //.addField("description", issueDetails.getDescription())
                .addField("assigneeDisplayName", issueDetails.getAssignee().getDisplayName())
                .addField("assigneeKey", issueDetails.getAssignee().getKey())
                .addField("reporterDisplayName", issueDetails.getReporter().getDisplayName())
                .addField("reporterKey", issueDetails.getReporter().getKey())
                .addField("userDisplayName", userData.getDisplayName())
                .addField("userKey", userData.getKey())
                .addField("fromVal", changeLog.getFrom())
                .addField("toVal", changeLog.getTo())
                .addField("fieldType", changeLog.getFieldType())
                /*.addField("commentBody", commentObject.getBody())
                .addField("commentCreated", commentObject.getCreated())
                .addField("commentUpdated", commentObject.getUpdated())
                .addField("authorName", commentObject.getAuthor().getName())
                .addField("authorNameDisplayName", commentObject.getAuthor().getDisplayName())
                .addField("authorKey", commentObject.getAuthor().getKey())
                .addField("updatedAuthorName", commentObject.getAuthor().getName())
                .addField("updatedAuthorDisplayName", commentObject.getAuthor().getDisplayName())
                .addField("updatedAuthorKey", commentObject.getAuthor().getKey())*/
                .build();

        if(issueDetails.timeToComplete > 0) {
            Point completedData = Point.measurement("issue_completed")
                    .time(timestamp, TimeUnit.MILLISECONDS)
                    .tag("project", issueDetails.getProject())
                    .tag("projectKey", issueDetails.getProjectKey())
                    .tag("type", issueDetails.getType())
                    .tag("status", issueDetails.getStatus())
                    .tag("priority", issueDetails.getPriority())
                    .tag("assignee", issueDetails.getAssignee().getName())
                    .tag("reporter", issueDetails.getReporter().getName())
                    .tag("userName", userData.getName())
                    .addField("ticket", issueDetails.getTicketNumber())
                    .addField("timeToComplete", issueDetails.timeToComplete)
                    .build();
            this.batchPoints.point(completedData);
        }
        this.batchPoints.point(dataToInsert);
    }

    public void writeAllData() {
        this.influxDB.write(this.batchPoints);
        batchPoints = BatchPoints
                .database(dbName)
                .retentionPolicy("default")
                .consistency(InfluxDB.ConsistencyLevel.ALL)
                .build();
    }
}
