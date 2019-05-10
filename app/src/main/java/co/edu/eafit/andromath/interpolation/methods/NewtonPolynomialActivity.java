package co.edu.eafit.andromath.interpolation.methods;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Arrays;

import co.edu.eafit.andromath.R;

public class NewtonPolynomialActivity extends AppCompatActivity {

    double points[], b[], p[];

    TextView polynomial1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newton_polynomial);

        polynomial1 = (TextView) findViewById(R.id.polynomial1);

        Intent intent = getIntent();
        points = (double[]) intent.getExtras().getSerializable("points");

        b = new double[points.length / 2];
        p = new double[points.length / 2];
        b[0] = points[1]; // b0 = y0

        nip(points, points.length / 2);
    }

    public void nip(double[] points, int pointsn) {

        for (int i = 1; i < pointsn; i++) {

            if (i == 1) b[i] = (points[(i * 2) + 1] - b[0]) / findDividerB(points, i);
            else {
                p(points, b, i - 1, points[i * 2]);
                double acum = 0;
                for (int j = 0; j < i; j++) acum -= p[j];
                b[i] = (points[(i * 2) + 1] + acum) / findDividerB(points, i);
            }
        }

        polynomial1.setText(Arrays.toString(b));
    }

    public void p(double[] points, double[] b, int index, double x) {

        double mul = 1;

        for (int i = 0; i < index + 1; i++) {
            for (int j = 0; j < i; j++) {
                mul *= (x - points[j * 2]); // x - xn
            }
            p[i] = (b[i] * mul);
            mul = 1;
        }
    }

    public double findDividerB(double[] points, int n) {

        double divider = 1;

        for (int i = 0; i < n; i++)
            divider *= points[n * 2] - points[i * 2];

        return divider;
    }
}
