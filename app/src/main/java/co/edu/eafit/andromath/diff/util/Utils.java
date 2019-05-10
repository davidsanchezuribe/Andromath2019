package co.edu.eafit.andromath.diff.util;

/**
 * Created by User on 5/29/2017.
 */

public class Utils {

    public static double fivePointDiffForward(double h, double[] y) {
        double y1[] = y;
        if (h > 0) {
            return (-25 * y[0] + 48 * y[1] - 36 * y[2] + 16 * y[3] - 3 * y[4]) / (12 * h);
        } else {
            return (-25 * y[4] + 48 * y[3] - 36 * y[2] + 16 * y[1] - 3 * y[0]) / (12 * h);
        }
    }

    public static double fivePointDiffCenter(double h, double[] y) {
        return (y[0] - 8 * y[1] + 8 * y[3] - y[4])/(12 * h);
    }

    public static double threePointDiffForward(double h, double[] y){
        if(h < 0){
            return (-3 * y[0] + 4 * y[1] - y[2])/(2 * h);
        } else {
            return (-3 * y[2] + 4 * y[1] - y[0])/(2 * h);
        }
    }

    public static double threePointDiffCenter(double h, double[] y){
        return (-y[0] + y[2])/(2 * h);
    }

    public static double twoPointDiffForward(double h, double[] y){
        if(h > 0){
            return (y[1] - y[0])/h;
        } else {
            return (y[0] - y[1])/h;
        }
    }
}