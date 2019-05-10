package co.edu.eafit.andromath.linearsystems.gaussianelimination.util;

import android.content.res.Resources;

public class ViewUtils {

    public static int getPxFromDp(int dp) {
        return (int) (dp * Resources.getSystem()
                .getDisplayMetrics().density);
    }
}