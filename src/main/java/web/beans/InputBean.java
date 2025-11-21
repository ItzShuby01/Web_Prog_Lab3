package web.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import web.util.ValidationException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// A Session-scoped bean to hold the user's current input (X, Y, R) and provide the selection options for the UI.

@Named("inputBean")
@SessionScoped
public class InputBean implements Serializable {
    private static final long serialVersionUID = 1L;

    //  Inputs
    private Double x;
    private Double y;
    private Double r = 3.0;

    // Flag to differentiate submission source: true for canvas, false for main form
    private boolean isCanvasSubmission = false;

    // Allowed R Values
    private final List<Double> rOptions = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
    // Allowed X values
    private final List<Double> xOptions = Arrays.asList(-3.0, -2.0, -1.0, 0.0, 1.0, 2.0, 3.0, 4.0, 5.0);
    private final Set<Double> xOptionsSet = xOptions.stream().collect(Collectors.toSet());
    private final Set<Double> rOptionsSet = rOptions.stream().collect(Collectors.toSet());


    // Validation Methods

    // R VALIDATION
    public void validateR() throws ValidationException {
        if (r == null || !rOptionsSet.contains(r)) {
            throw new ValidationException("R must be one of the allowed whole numbers: 1, 2, 3, 4, 5.");
        }
    }

    public void validateFormInputs() throws ValidationException {
        // X VALIDATION
        if (x == null || !xOptionsSet.contains(x)) {
            throw new ValidationException("For form submissions, X must be one of the selected values: -3 to 5.");
        }

        // Y VALIDATION
        if (y == null || y < -3.0 || y > 5.0) {
            throw new ValidationException("For form submissions, Y must be between -3 and 5.");
        }

        // R Validation is always strict
        validateR();
    }

    // Validation specific to the canvas click (continuous X/Y within graph range).
    public void validateCanvasInputs() throws ValidationException {
        // X VALIDATION (must be between -5 and 5, based on the graph bounds)
        if (x == null || x < -5.0 || x > 5.0) {
            throw new ValidationException("For canvas clicks, X must be between -5 and 5.");
        }

        // Y VALIDATION (must be between -5 and 5, based on the graph bounds)
        if (y == null || y < -5.0 || y > 5.0) {
            throw new ValidationException("For canvas clicks, Y must be between -5 and 5.");
        }

        // R Validation is always strict
        validateR();
    }


    //  Getters and Setters for JSF Binding
    public Double getX() { return x; }

    // Used by the X command buttons.
    public void setX(Double xVal) { this.x = xVal; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Double getR() { return r; }
    public void setR(Double r) { this.r = r; }

    public boolean isCanvasSubmission() { return isCanvasSubmission; }
    public void setCanvasSubmission(boolean isCanvasSubmission) { this.isCanvasSubmission = isCanvasSubmission; }

    public List<Double> getrOptions() { return rOptions; }
    public List<Double> getxOptions() { return xOptions; }

    // Checks if the R value is one of the discrete options.
    public boolean isRValid() {
        return r != null && rOptionsSet.contains(r);
    }
}