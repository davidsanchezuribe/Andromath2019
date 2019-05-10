package co.edu.eafit.andromath.util;

import java.math.RoundingMode;

public class Constants {

    public static final String VARIABLE = "x";
    public static final String EQUATION = "equation";
    public static final String POINTS = "points";
    public static final String SELECTION = "selection";
    public static final String NOTATION_FORMAT = "0.0E0";

    public static final int NORMAL_METHOD = 0;
    public static final int RELAXED_METHOD = 1;

    public static final int DECIMALS_QUANTITY = 10;
    public static final RoundingMode ROUNDING_MODE = RoundingMode.DOWN;

    public static final String MATRIX = "matrix";

    public enum ErrorCodes {
        X_ROOT("The entered X is a root", false),
        INVALID_DELTA("Delta is not valid", false),
        INVALID_ITER("Wrong number of iterations", false),
        INVALID_RANGE("Not suitable range", false),
        OUT_OF_RANGE("Out of range", false);

        private String message;
        private boolean displayProcedure;

        ErrorCodes(String message, boolean displayProcedure) {
            this.message = message;
            this.displayProcedure = displayProcedure;
        }

        public String getMessage() {
            return message;
        }

        public boolean isDisplayProcedure() {
            return displayProcedure;
        }
    }
}