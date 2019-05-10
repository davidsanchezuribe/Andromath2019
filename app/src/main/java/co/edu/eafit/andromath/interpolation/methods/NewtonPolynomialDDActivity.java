package co.edu.eafit.andromath.interpolation.methods;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.Arrays;

import co.edu.eafit.andromath.R;

public class NewtonPolynomialDDActivity extends AppCompatActivity {

    double[] points;
    double[][] polynomial;

    TextView polynomial2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newton_polynomial_dd);

        polynomial2 = (TextView) findViewById(R.id.polynomial2);

        Intent intent = getIntent();
        points = (double[]) intent.getExtras().getSerializable("points");

        nipdd(points, points.length / 2);
    }

    public void nipdd(double[] points, int pointsn) {

        polynomial = new double[pointsn][pointsn + 1];

        // Filling matrix with points
        for (int i = 0; i < pointsn; i++) {
            polynomial[i][0] = points[i * 2];
            polynomial[i][1] = points[(i * 2) + 1];
        }

        for (int j = 2; j <= pointsn; j++) {
            for (int i = j - 1; i < pointsn; i++)
                polynomial[i][j] = (polynomial[i - 1][j - 1] - polynomial[i][j - 1])
                        / (polynomial[(i - j) + 1][0] - polynomial[i][0]);
        }

        String res = "";

        for (int i = 0; i < pointsn; i++)
            res += Arrays.toString(polynomial[i]) + "\n"; // Polynomial

        polynomial2.setText(res);
    }
}