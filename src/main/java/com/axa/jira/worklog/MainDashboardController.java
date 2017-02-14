package com.axa.jira.worklog;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.xml.ws.RequestWrapper;
import java.util.Map;
import java.util.Objects;

/**
 * Created by A.Solianyk on 14.02.2017.
 */
@Controller
public class MainDashboardController {

    @RequestMapping("/")
    public String getMainDashboardView(Map<String , Object> model) {
        model.put("message", "Hi there!:)");
        return "main_dashboard";
    }
}
