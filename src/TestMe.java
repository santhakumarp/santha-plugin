import com.google.gson.JsonElement;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

// Extend HttpServlet class
public class TestMe extends HttpServlet {

    private String message;

    public void init() throws ServletException
    {
        message = "";
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        InputStream postData = request.getInputStream();

        JiraImport importObject = new JiraImport();
        JsonElement postValue = importObject.getJsonFromInputStream(postData);

        importObject.insertPostDataFromWebHook(postValue);

        response.setStatus(HttpServletResponse.SC_OK);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException
    {
        response.setIntHeader("Refresh", 30);

        // Set response content type
        response.setContentType("text/html");

        // Get current time
        Calendar calendar = new GregorianCalendar();
        String am_pm;
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        if(calendar.get(Calendar.AM_PM) == 0) {
            am_pm = "AM";
        } else {
            am_pm = "PM";
        }

        String CT = hour+":"+ minute +":"+ second +" "+ am_pm;

        PrintWriter out = response.getWriter();
        String title = "Auto Refresh Header Setting";
        String docType = "<!doctype html public \"-//w3c//dtd html 4.0 " +
                        "transitional//en\">\n";

        HttpSession session = request.getSession(true);

        Integer visitCount = new Integer(0);
        String visitCountKey = new String("visitCount");

        if (!session.isNew()){
            visitCount = (Integer)session.getAttribute(visitCountKey);
            visitCount = visitCount + 1;
        }

        session.setAttribute(visitCountKey,  visitCount);

        int page = 0;
        int limit = 0;
        int startsAt = 0;
        page = visitCount - 1;
        limit = 50;
        startsAt = page * limit;

        if(visitCount > 0 && startsAt < 1000) {
            JiraImport importObject = new JiraImport();
            importObject.importRecordsFromJira(startsAt, limit);
            importObject.importIssuesToInfluxDB();
        } else {
            startsAt = 100000;
        }

        /*JiraImport importObject = new JiraImport();
        String outputToShow = importObject.importRecordsFromJira();
        importObject.importIssuesToInfluxDB();*/



        out.println(docType +
                "<html>\n" +
                "<head><title>" + title + "</title></head>\n"+
                "<body bgcolor=\"#f0f0f0\">\n" +
                "<h1 align=\"center\">" + title + "</h1>\n" +
                "<h1 align=\"center\">Visit:" + visitCount + "</h1>\n" +
                "<h1 align=\"center\">Importing records:" + startsAt + "-" + (startsAt+limit) + "</h1>\n" +
                "<p>Current Time is: " + CT +"</p>\n");




    }

    public void destroy()
    {
        message = "";
        // do nothing.
    }
}