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


    // Method for FORM Button Clicks only.
    // Forces usage of Form X and Form Y.
    public void checkForm() {
        inputBean.setCanvasSubmission(false); // Reset flag
        try {
            inputBean.validateFormInputs();
            // STRICTLY use X and Y
            processCalculation(inputBean.getX(), inputBean.getY(), inputBean.getR());
        } catch (ValidationException e) {
            addErrorMessage(e.getMessage());
        }
    }

    // Method for CANVAS Clicks only.
    // Forces usage of CanvasX and CanvasY
    public void checkCanvas() {
        inputBean.setCanvasSubmission(true); // Set flag
        try {
            inputBean.validateCanvasInputs();
            // STRICTLY use CanvasX and CanvasY
            processCalculation(inputBean.getCanvasX(), inputBean.getCanvasY(), inputBean.getR());
        } catch (ValidationException e) {
            addErrorMessage(e.getMessage());
        }
    }

    // Common logic to avoid code duplication
    private void processCalculation(Double x, Double y, Double r) {
        try {
            if (x == null || y == null || r == null) {
                throw new ValidationException("Critical input values are missing.");
            }

            long startTime = System.nanoTime();
            boolean hit = AreaCalculator.calculate(x, y, r);
            long endTime = System.nanoTime();

            CalculationResult newResult = new CalculationResult(x, y, r, hit, (endTime - startTime));
            resultBean.addResult(newResult);

        } catch (Exception e) {
            addErrorMessage("An unexpected error occurred during calculation.");
        }
    }

    // Helper to add a FacesMessage bound to a specific component.
    private void addErrorMessage(String message) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error:", message));
    }
}