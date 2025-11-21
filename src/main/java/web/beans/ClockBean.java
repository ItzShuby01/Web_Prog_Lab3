package web.beans;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// An ApplicationScoped bean that provides the current formatted time on the start page every 10 seconds

@Named("clockBean")
@ApplicationScoped
public class ClockBean {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

    // Returns the current formatted time.
    public String getCurrentDateTime() {
        return LocalDateTime.now().format(FORMATTER);
    }
}

