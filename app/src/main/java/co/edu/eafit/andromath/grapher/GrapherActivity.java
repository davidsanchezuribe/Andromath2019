package co.edu.eafit.andromath.grapher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import co.edu.eafit.andromath.evalex.Expression;

import java.math.BigDecimal;
import java.util.Objects;

import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.util.Constants;

public class GrapherActivity extends AppCompatActivity {

    private GraphView graphView;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grapher);
        Objects.requireNonNull(getSupportActionBar()).hide();

        intent = getIntent();
        graphView = (GraphView) findViewById(R.id.graph);

        this.graph();
    }

    private void graph() {

        DataPoint[] dataPoints =  (DataPoint[]) intent.getSerializableExtra("points");

        double xAxisValueMax = intent.getDoubleExtra("xAxisValueMax", 50);
        double xAxisValueMin = intent.getDoubleExtra("xAxisValueMin", -50);

        double highestY = intent.getDoubleExtra("highestY", 50);
        double lowestY = intent.getDoubleExtra("lowestY", -50);

        graphView.addSeries(new LineGraphSeries(dataPoints));

        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMaxX(xAxisValueMax);
        graphView.getViewport().setMinX(xAxisValueMin);

        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMaxY(highestY);
        graphView.getViewport().setMinY(lowestY);
    }
}