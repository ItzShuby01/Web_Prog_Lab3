package web.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import web.model.AreaCalculator;
import web.model.CalculationResult;
import web.util.ValidationException;
import java.io.Serializable;

// A Request-scoped bean that executes the area check logic.
// It uses the submission context (form vs. canvas) to enforce the correct validation rules and persists the result.

@Named("checkBean")
@RequestScoped
public class CheckAreaBean implements Serializable {

    @Inject
    private ResultHistoryBean resultBean;

    @Inject
    private InputBean inputBean;

    // The core action method triggered by the form submit or graph click.
    // Implements dual validation path based on the submission source.
    // Returns null to signal JSF to stay on the current view (main.xhtml).
    public String checkArea() {

        try {
            if (inputBean.isCanvasSubmission()) {
                // If true, use the canvas validation rules
                inputBean.validateCanvasInputs();
            } else {
                // otherwise, use the form validation rules
                inputBean.validateFormInputs();
            }

            //If Validation Passes, Proceed with Calculation
            long startTime = System.nanoTime();

            Double x = inputBean.getX();
            Double y = inputBean.getY();
            Double r = inputBean.getR();

            // check before calculation
            if (x == null || y == null || r == null) {
                throw new ValidationException("Critical input values (X, Y, R) are missing after validation.");
            }

            boolean hit = AreaCalculator.calculate(x, y, r);

            long endTime = System.nanoTime();
            long executionTime = endTime - startTime;

            // Create and persist the result
            CalculationResult newResult = new CalculationResult(x, y, r, hit, executionTime);
            resultBean.addResult(newResult);

        } catch (ValidationException e) {
            // Catch validation exceptions thrown by InputBean and display them to the user
            addErrorMessage(e.getMessage(), null); // Component ID is null to display error globally
            return null;
        } catch (Exception e) {
            addErrorMessage("An unexpected error occurred during calculation.", null);
            return null;
        }

        return null;
    }

    // Helper to add a FacesMessage bound to a specific component.
    private void addErrorMessage(String message, String clientId) {
        FacesContext.getCurrentInstance().addMessage(
                clientId,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid Input:", message)
        );
    }
}