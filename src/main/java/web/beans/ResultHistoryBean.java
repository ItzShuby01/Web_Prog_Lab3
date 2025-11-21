package web.beans;

import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import web.model.CalculationResult;
import web.model.DataBaseManager;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

// An Application-scoped bean that manages all calculation results

@Named("resultBean")
@ApplicationScoped
public class ResultHistoryBean implements Serializable {

    // Thread-safe list to hold the in-memory copy of all results,
    private CopyOnWriteArrayList<CalculationResult> results;
    private DataBaseManager dao;


    // Initializes the bean after construction. Then the data is loaded from the PostgreSQL db using the ResultDao.
    @PostConstruct
    public void init() {
        this.dao = new DataBaseManager();

        // Load existing results from the DB on application start
        List<CalculationResult> loadedResults = dao.loadAllResults();
        this.results = new CopyOnWriteArrayList<>(loadedResults);
    }

    // Adds a new result to both the PostgreSQL database and the in-memory list.
    // Called by the CheckBean after a successful calculation.
    public void addResult(CalculationResult result) {
        // 1. Save to the database
        dao.saveResult(result);

        // 2. Add to the start of the thread-safe in-memory list (For UI display)
        results.add(0, result);
    }


    // Provides the list of results for display in the main page table (main.xhtml).
    public List<CalculationResult> getResults() {
        // Return a read-only view of the list
        return Collections.unmodifiableList(results);
    }

    public String getPointsJson() {
        // Using Gson to serialize the list of results into a JSON string
        // Necessary because JSF cannot directly translate Java List to a JS array literal.
        return new Gson().toJson(this.results);
    }
}