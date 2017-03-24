package com.axa.jira.worklog;

import com.axa.jira.worklog.model.*;
import com.axa.jira.worklog.repositories.UserRepository;
import com.axa.jira.worklog.repositories.WorkedTicketRepository;
import com.axa.jira.worklog.repositories.WorklogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.RequestWrapper;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by A.Solianyk on 14.02.2017.
 */
@Controller
public class MainDashboardController {

    @Autowired
    private WorklogRepository worklogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkedTicketRepository workedTicketRepository;

//    private static final User[] users = new User[]{
//            new User("Швец Руслан (Ruslan Shvets)", "r.shvets"),
//            new User("Цубин Виталий (Vitaliy Tsubin)", "v.tsubin"),
//            new User("Скобля Александр (Alexander Skoblya)	", "a.skoblya"),
//            new User("Соляник Антон (Anton Solianyk)", "a.solianyk"),
//            new User("Коршун Александр (Alexander Korshun)", "a.korshun")
//    };
    private Map<String, Row> rows = new HashMap<>();

    @RequestMapping("/month")

    public String getMainDashboardView(@RequestParam(value="user", required = false) String user,
                                       @RequestParam(value="dateFr", required = false)    @DateTimeFormat(pattern="yyyy-MM-dd") Date dateFr,
                                       @RequestParam(value="dateTo", required = false)    @DateTimeFormat(pattern="yyyy-MM-dd") Date dateTo,
                                       Map<String, Object> model) {
        model.put("message", "Hi there!:)");

        if (dateFr == null && dateTo == null) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            c.set(year, month, c.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
            dateTo = c.getTime();
            c.set(year, month, 1, 0, 0, 0);
            dateFr = c.getTime();
        }
        model.put("dateFr", dateFr);
        model.put("dateTo", dateTo);
        List<Row> rows = worklogRepository.findAll(user, dateFr, dateTo);
        model.put("rows", rows);

        List<Day> header = new ArrayList<Day>();
        Calendar c = Calendar.getInstance();
        c.setTime(dateFr);
        int maxDays = (int)((dateTo.getTime() - dateFr.getTime())/ (24 * 60 * 60 * 1000));
        for (int i = 1; i <= maxDays; i++) {
            c.add(Calendar.DAY_OF_MONTH, 1);
            header.add(new Day(c.getTime()));
        }
        model.put("header", header);
        model.put("days", maxDays);
        return "main_dashboard";
    }

    @RequestMapping("/")
    public String getMonthCalendarView(@RequestParam(value="user", required = false) String user,
                                       @RequestParam(value="dateFr", required = false)    @DateTimeFormat(pattern="yyyy-MM-dd") Date dateFr,
                                       @RequestParam(value="dateTo", required = false)    @DateTimeFormat(pattern="yyyy-MM-dd") Date dateTo,
                                       Map<String, Object> model) {
        model.put("name", "Jira Worklog");
        if (dateFr == null && dateTo == null) {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            c.set(year, month, c.get(Calendar.DAY_OF_MONTH) + 1, 0, 0, 0);
            dateTo = c.getTime();
            c.set(year, month, 1, 0, 0, 0);
            dateFr = c.getTime();
        }



        Calendar c = Calendar.getInstance();
        c.setTime(dateFr);
        //	c.set(Calendar.DAY_OF_MONTH, 1);
        // Moves start date to start of the it week
        c.add(Calendar.DAY_OF_MONTH, -1 * (c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek()));
        dateFr = c.getTime();

        List<Row> rows = user != null ? worklogRepository.findAll(user, dateFr, dateTo) : new ArrayList<>();
        Row row = rows.size() > 0 ? rows.get(0) : null;
        model.put("userName", row != null ? row.getUser().getUserName() : null);
        model.put("worklog", rows.size() > 0 ? rows.get(0) : null);
        model.put("users", userRepository.findDevelopers());
        model.put("user", row != null ? row.getUser() : null);
        model.put("userId", user);
        model.put("dateFr", dateFr);
        model.put("dateTo", dateTo);

//        Map<String, List<Ticket>> workedTickets = user != null ? workedTicketRepository.findByUserDates(user, dateFr, dateTo) : new HashMap<String, List<Ticket>>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        int weeks = (int) ((dateTo.getTime() - dateFr.getTime()) / 1000 / 60 / 60 / 24 / 7) + 1;
        Day[][] days = new Day[weeks][7];
        for (int i = 0; i < weeks; i++) {
            for (int j = 0; j < 7; j++) {
                days[i][j] = new Day(c.getTime());
                days[i][j].setWorkLogs(row != null ? row.getWorkLogs().get(sdf.format(c.getTime())) : null);
//                System.out.print(new SimpleDateFormat("dd.MM.yyy").format(c.getTime()) + " ");
                days[i][j].setTotalMinutes(row != null ? row.getTotalMinutesPerDate(sdf.format(c.getTime())) : 0);
//                days[i][j].setWorkedTickets(workedTickets.get(sdf.format(c.getTime())));
                c.add(Calendar.DAY_OF_MONTH, 1);
            }
            //System.out.println("");
        }
        model.put("days", days);
        return "user_month";
    }

    /*@RequestMapping("/jira/**")
    @ResponseBody
    public String mirrorRest(@RequestBody String body, HttpMethod method, HttpServletRequest request,
                             HttpServletResponse response) throws URISyntaxException

    {
        URI uri = new URI("http", null, "jira", 80, request.getRequestURI(), request.getQueryString(), null);
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<?> request = new HttpEntity<>()y<>(request.getHeaders());

        ResponseEntity<?> responseEntity =
                restTemplate.exchange("http:\\\\jira", method, new HttpEntity<String>(body), null);
        return responseEntity.getBody();
    }*/
}
