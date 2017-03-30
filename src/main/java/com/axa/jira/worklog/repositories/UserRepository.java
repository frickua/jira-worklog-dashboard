package com.axa.jira.worklog.repositories;

import com.axa.jira.worklog.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by A.Solianyk on 17.02.2017.
 */
@Repository
public class UserRepository {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<User> findDevelopers() {

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "SELECT " +
                        "wu.*," +
                        "(SELECT max(id) FROM avatar WHERE owner = w.AUTHOR) AS avatar_id  " +
                        "FROM cwd_user wu " +
                        "WHERE " +
                        "EXISTS(" +
                            "SELECT " +
                                "null " +
                            "FROM cwd_membership " +
                            "WHERE cwd_membership.child_id = wu.id AND lower_parent_name = 'jira-developers') " +
                        "AND EXISTS(" +
                            "SELECT " +
                                "null " +
                            "FROM cwd_membership " +
                            "WHERE cwd_membership.child_id = wu.id AND lower_parent_name = 'avalon.build')");

        //
        List<User> users = new ArrayList<>();

        for (Map result: results) {
            String userId = (String) result.get("lower_user_name");
            BigDecimal avatarId = (BigDecimal) result.get("avatar_id");
            users.add(new User((String) result.get("display_name"), userId, avatarId != null ? String.valueOf(avatarId.intValue()) : null));
        }
        return users;
    }
}
