package co.edu.eafit.andromath.interpolation.methods;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.Arrays;

import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.interpolation.utils.Utils;


public class BasedOnESActivity extends AppCompatActivity {


    TextView polynomial;

    int i;
    double points[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_based_on_es);

        Intent intent = getIntent();
        points = (double[]) intent.getExtras().getSerializable("points");

        polynomial = (TextView) findViewById(R.id.polynomial);

        calculatePolynomial(points);
    }

    public void calculatePolynomial(double[] points) {

        int len = points.length / 2;

        double[][] a = new double[len][len];
        double[] b = new double[len];

        for (i = 0; i < a.length; i++) {
            for (int j = 0; j < a[i].length; j++) {
                a[i][j] = Math.pow(points[i * 2], len - (j + 1));
            }
            b[i] = points[(i * 2) + 1];
            System.out.print(Arrays.toString(a[i]) + " = ");
            System.out.println(b[i]);
        }

        double[] sol = solveSystem(a,b);

        polynomial.setVisibility(View.VISIBLE);

        String pol = "p(x) = ";
        for (int i = 0; i < len; i++) {
            if (sol[i] >= 0 && i != 0) pol += "+";
            if ((len - (i + 1)) == 0) pol += sol[i];
            else if ((len - (i + 1)) == 1) pol += sol[i] + "x";
            else pol += sol[i] + "x^" + (len - (i + 1));
        }

        polynomial.setText(pol);
    }

    public double[] solveSystem(double[][] a, double[] b) {

        // Solving with Total Pivot Gauss

        int n = a.length;
        int marks[] = new int[n];
        for(int i = 0; i<n; i++){
            marks[i] = i;
        }
        double mult;
        Utils.MatrixMarks mm;
        //Method Begins
        double m[][] = Utils.augmentMatrix(a,b);
        for (int k = 0; k < n-1; k++){
            mm = Utils.totalPivot(m,k,marks);
            m = mm.Ab;
            marks = mm.marks;
            for (int i = k+1; i < n; i++){
                mult = m[i][k]/m[k][k];
                for (int j = k; j < n+1; j++){
                    m[i][j] = m[i][j] - mult*m[k][j];
                }
            }
        }
        double x[] = Utils.regressiveSubstitution(m);
        x = Utils.markAwareX(x,marks);

        return x;
    }
}