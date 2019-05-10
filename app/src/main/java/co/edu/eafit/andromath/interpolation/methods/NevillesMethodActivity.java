package co.edu.eafit.andromath.interpolation.methods;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;

import co.edu.eafit.andromath.R;

public class NevillesMethodActivity extends AppCompatActivity {

    double points[], polynomial[][];
    EditText xvalue;
    TextView solut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nevilles_method);

        Intent intent = getIntent();
        points = (double[]) intent.getExtras().getSerializable("points");

        xvalue = (EditText) findViewById(R.id.xvalue);
        solut = (TextView) findViewById(R.id.solut);
    }

    public void executeNeville(View v) {
        String x = xvalue.getText().toString();
        nm(points, points.length / 2, Double.parseDouble(x));
    }

    public void nm(double[] points, int pointsn, double x) {

        System.out.println(x);

        polynomial = new double[pointsn][pointsn + 1];

        // Filling matrix with points
        for (int i = 0; i < pointsn; i++) {
            polynomial[i][0] = points[i * 2];
            polynomial[i][1] = points[(i * 2) + 1];
        }

        for (int j = 2; j <= pointsn; j++) {
            for (int i = j - 1; i < pointsn; i++)
                polynomial[i][j] = (((x - polynomial[(i - j) + 1][0]) * polynomial[i][j - 1]) -
                        ((x - polynomial[i][0]) * polynomial[i - 1][j - 1]))
                        / (polynomial[i][0] - polynomial[(i - j) + 1][0]);
        }

        for (int i = 0; i < pointsn; i++)
            System.out.println(Arrays.toString(polynomial[i])); // Polynomial

        solut.setVisibility(View.VISIBLE);
        solut.setText("f(" + x + ") = " + Double.toString(polynomial[pointsn - 1][pointsn]));
        xvalue.setText("");
    }
}