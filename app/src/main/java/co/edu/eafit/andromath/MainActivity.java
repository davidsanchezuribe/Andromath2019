package co.edu.eafit.andromath;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Objects;

import co.edu.eafit.andromath.interpolation.InterpolationLandingActivity;
import co.edu.eafit.andromath.linearsystems.LinearSystemsLandingActivity;
import co.edu.eafit.andromath.singlevar.SingleVariableLandingActivity;
import co.edu.eafit.andromath.diff.DiffLandingActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    public void singleVariableButton(View v) {
        Intent intent = new Intent(this, SingleVariableLandingActivity.class);
        startActivity(intent);
    }

    public void linearSystemsButton(View v) {
        Intent intent = new Intent(this, LinearSystemsLandingActivity.class);
        startActivity(intent);
    }

    public void InterpolationButton(View v) {
        Intent intent = new Intent(this, InterpolationLandingActivity.class);
        startActivity(intent);
    }

    public void DiffButton(View v) {
        Intent intent = new Intent(this, DiffLandingActivity.class);
        startActivity(intent);
    }
}