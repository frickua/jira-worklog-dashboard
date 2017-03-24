package com.axa.jira.worklog.repositories;

import com.axa.jira.worklog.model.Row;
import com.axa.jira.worklog.model.Ticket;
import com.axa.jira.worklog.model.User;
import com.axa.jira.worklog.model.WorkLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static java.sql.Types.DATE;
import static java.sql.Types.VARCHAR;

/**
 * Created by A.Solianyk on 17.02.2017.
 */
@Repository
public class WorklogRepository {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Find all customers, thanks Java 8, you can create a custom RowMapper like this :
    public List<Row> findAll(String user, Date dateFr, Date dateTo) {

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "SELECT " +
                        "  (w.timeworked / 60) AS minutes, " +
                        "  concat(p.pkey, '-', i.issuenum) AS ticket, " +
                        "  DATE_FORMAT(STARTDATE, '%d.%m.%Y') AS date, " +
                        "  DATE_FORMAT(STARTDATE, '%H:%i:%s') AS time, " +
                        " (SELECT sum(timeworked) / 60 FROM worklog WHERE worklog.AUTHOR = w.AUTHOR AND cast(worklog.STARTDATE as DATE) = cast(w.STARTDATE AS DATE)) AS total_minutes, " +
                        " (SELECT max(id) FROM avatar WHERE owner = w.AUTHOR) AS avatar_id, " +
                        "  wu.display_name, " +
                        " truncate(sp_cfv.NUMBERVALUE, 1) AS sp," +
                        " i.SUMMARY AS ticket_description," +
                        " s.pname AS ticket_status, " +
                        " w.worklogbody, " +
                        "  w.AUTHOR, " +
                        "  concat('\"',wu.display_name, '\", \"', w.AUTHOR, '\"') " +
                        "FROM worklog w " +
                        "LEFT JOIN jiraissue i ON i.id = w.issueid " +
                        "LEFT JOIN issuestatus s ON s.id = i.issuestatus " +
                        "LEFT JOIN project p ON i.PROJECT = p.ID " +
                        "LEFT JOIN cwd_user wu ON wu.lower_user_name = w.AUTHOR " +
                        "LEFT JOIN (customfieldvalue sp_cfv " +
                        "      JOIN customfield sp_cf ON sp_cf.ID = sp_cfv.CUSTOMFIELD AND cfname = 'Story Points') ON sp_cfv.ISSUE = i.ID " +
                        "WHERE EXISTS(SELECT null FROM cwd_membership WHERE cwd_membership.child_id = wu.id AND lower_parent_name = 'jira-developers') AND EXISTS(SELECT null FROM cwd_membership WHERE cwd_membership.child_id = wu.id AND lower_parent_name = 'avalon.build') " +
                        "AND STARTDATE >= ? AND STARTDATE <= ? " +
                        "AND (? IS NULL OR w.author = ? ) " +
                        "ORDER BY startdate ",
                new Object[]{ dateFr, dateTo, user, user},
                new int[]{DATE, DATE, VARCHAR, VARCHAR});

        //
        List<Row> rowsList = new ArrayList<>();
        Map<String, Row> rows = new HashMap<>();

        for (Map result: results) {
            String userId = (String) result.get("author");
            Row row = rows.get(userId);
            if (row == null) {
                row = new Row();
                rows.put(userId, row);
                BigDecimal avatarId = (BigDecimal) result.get("avatar_id");
                row.setUser(new User((String) result.get("display_name"), userId, avatarId != null ? String.valueOf(avatarId.intValue()) : null));
                rowsList.add(row);
            }
            WorkLog wLog = new WorkLog();
            wLog.setStartDate((String) result.get("date"));
            wLog.setHouers((int) ((BigDecimal) result.get("minutes")).intValue() / 60);
            wLog.setMinutes((int) ((BigDecimal) result.get("minutes")).intValue() % 60);
            BigDecimal sp = (BigDecimal) result.get("sp");
            wLog.setTicket(new Ticket((String) result.get("ticket"), (sp != null ? sp.floatValue() : null), (String) result.get("ticket_status"), (String) result.get("ticket_description")));
            wLog.setDescription(addSpacesToCamelCase((String) result.get("worklogbody")));
            row.addWorkLog(wLog);

            wLog = new WorkLog();
            wLog.setStartDate((String) result.get("date"));
            wLog.setHouers((int) ((BigDecimal) result.get("total_minutes")).intValue() / 60);
            wLog.setMinutes((int) ((BigDecimal) result.get("total_minutes")).intValue() % 60);
            row.addTotalLog(wLog.getStartDate(), wLog);
            row.setTotalMinutesPerDate((String) result.get("date"), ((BigDecimal) result.get("total_minutes")).intValue());

            row.addMinutes(((BigDecimal) result.get("minutes")).intValue());

        }

        return rowsList;

    }

    private static String addSpacesToCamelCase(String txt) {
        String r = "";
        int lastSpacePosition = 0;
        for (int i = 0; i < txt.length(); i++) {
            Character c = txt.charAt(i);
            if (Character.isUpperCase(c) && (i - lastSpacePosition) > 20) {
                r = r + ' ';
                lastSpacePosition = i;
            }
            r = r + c;
        }

        return r;
    }

    // Add new customer
    public void addCustomer(String name, String email) {

        jdbcTemplate.update("INSERT INTO customer(name, email, created_date) VALUES (?,?,?)",
                name, email, new Date());

    }

    public static void main(String[] args) {
        System.out.println(addSpacesToCamelCase("FuckingShitsHappens"));
    }
}
