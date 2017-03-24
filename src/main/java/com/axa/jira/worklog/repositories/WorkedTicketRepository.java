package com.axa.jira.worklog.repositories;

import com.axa.jira.worklog.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.*;

import static java.sql.Types.*;

/**
 * Created by A.Solianyk on 20.03.2017.
 */
@Repository
public class WorkedTicketRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, List<Ticket>> findByUserDates(String userId, Date dateFr, Date dateTo) {
        if (dateFr == null || dateTo == null || userId == null) {
            throw new IllegalArgumentException("All arguments are required.");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(dateFr);
        int days = (int)(dateTo.getTime() - dateFr.getTime()) / 1000  / 60 / 60 / 24;

        Map<String, List<Ticket>> tickets = new HashMap<>();
        Object[] params = new Object[days*2];
        int[] paramsType = new int[days*2];
        StringBuilder sql = new StringBuilder();
        int pI = 0;
        for (int i = 0; i < days; i++) {
            if (i > 0) {
                sql.append(" UNION ALL ");
            }
            sql.append("SELECT " +
                            " DATE_FORMAT(day, '%d.%m.%Y') AS day," +
                    " truncate(sp_cfv.NUMBERVALUE, 1) AS sp, " +
                    " concat(p.pkey, '-', i.issuenum) AS ticket_number, " +
                    " SUMMARY, " +
                    " assignee.NEWVALUE, " +
                    " status.NEWSTRING AS status " +
                    " FROM (SELECT ? AS day, jiraissue.* FROM jiraissue) i " +
                    " LEFT JOIN project p ON i.PROJECT = p.ID " +
                    " INNER JOIN changeitem  assignee ON ( " +
                            " SELECT " +
                            " max(changeitem.id) AS max_assignee " +
                            " FROM changeitem " +
                            " INNER JOIN changegroup ON changegroup.ID = changeitem.groupid " +
                            " WHERE changegroup.issueid = i.id AND changeitem.FIELD = 'assignee' AND changegroup.CREATED <= day) " +
                            " = assignee.id AND assignee.NEWVALUE = ? " +
                    " INNER JOIN changeitem status ON ( " +
                            " SELECT " +
                            " max(changeitem.id) AS max_assignee " +
                            " FROM changeitem " +
                            " INNER JOIN changegroup ON changegroup.ID = changeitem.groupid " +
                            " WHERE changegroup.issueid = i.id AND changeitem.FIELD = 'status' AND changegroup.CREATED <= day) " +
                            " = status.id AND status.NEWSTRING IN ('Open', 'Developed', 'Approved', /*'Preliminary test',*/ 'In Progress', 'Reopened')" +
                    " LEFT JOIN (customfieldvalue sp_cfv " +
                            "      JOIN customfield sp_cf ON sp_cf.ID = sp_cfv.CUSTOMFIELD AND cfname = 'Story Points') ON sp_cfv.ISSUE = i.ID ");
            params[pI] = c.getTime();
            paramsType[pI] = DATE;
            pI++;
            params[pI] = userId;
            paramsType[pI] = VARCHAR;
            pI++;
        }

        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql.toString(), params, paramsType);

        for (Map<String, Object> result : resultList) {
            String dayKey = (String) result.get("day");
            List<Ticket> dayTickets = tickets.get(dayKey);
            if (dayTickets == null) {
                dayTickets = new ArrayList<>();
                tickets.put(dayKey, dayTickets);
            }
            dayTickets.add(new Ticket((String) result.get("ticket_number"),
                    result.get("sp") != null ? ((BigDecimal) result.get("sp")).floatValue() : null,
                    (String) result.get("status"),
                    (String) result.get("summary")));
        }

        return tickets;
    }
}
