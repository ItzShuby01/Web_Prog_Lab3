package web.model;

// Determines if the point is inside the area or not(i.e hit/miss)
public class AreaCalculator {
    public static boolean calculate(double x, double y, double r) {
        // R must be a positive
        if (r <= 0) {
            return false;
        }

        // Quadrant 1: Triangle (x >= 0, y >= 0)
        // Line passes through (R/2, 0) and (0, R). Equation: y = -2x + R
        boolean inTriangle = (x >= 0) && (y >= 0) &&
                (y <= (-2.0 * x + r));


        // Quadrant 3: Rectangle (x <= 0, y <= 0)
        // Bounded by: -R/2 <= x <= 0 and -R <= y <= 0
        boolean inRectangle = (x <= 0) && (y <= 0) &&
                (x >= -r / 2.0) && (y >= -r);


        // Quadrant 4: Quarter Circle in Quadrant IV (x >= 0, y <= 0) ---
        // Bounded by: x^2 + y^2 <= R^2
        boolean inQuarterCircle = (x >= 0) && (y <= 0) &&
                (Math.pow(x, 2) + Math.pow(y, 2) <= Math.pow(r, 2));


        // The point is a "Hit" if it is in any of the three colored regions.
        return inTriangle || inRectangle || inQuarterCircle;
    }
}