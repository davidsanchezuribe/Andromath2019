package co.edu.eafit.andromath.singlevar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.Objects;

import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.singlevar.methods.BisectionActivity;
import co.edu.eafit.andromath.singlevar.methods.FalseRuleActivity;
import co.edu.eafit.andromath.singlevar.methods.FixedPointActivity;
import co.edu.eafit.andromath.singlevar.methods.IncrementalSearchActivity;
import co.edu.eafit.andromath.singlevar.methods.MultipleRootsActivity;
import co.edu.eafit.andromath.singlevar.methods.NewtonActivity;
import co.edu.eafit.andromath.singlevar.methods.SecantActivity;
import co.edu.eafit.andromath.util.Constants;

public class SingleVariableElectionActivity extends AppCompatActivity {

    private Intent intent;

    private String function;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_variable_election);
        Objects.requireNonNull(getSupportActionBar()).hide();

        function = getIntent().getStringExtra(Constants.EQUATION);
    }

    public void incrementalSearch(View v) {
        intent = new Intent(this, IncrementalSearchActivity.class);
        intent.putExtra(Constants.EQUATION, function);
        startActivity(intent);
    }

    public void bisection(View v) {
        intent = new Intent(this, BisectionActivity.class);
        intent.putExtra(Constants.EQUATION, function);
        startActivity(intent);
    }

    public void falseRule(View v) {
        intent = new Intent(this, FalseRuleActivity.class);
        intent.putExtra(Constants.EQUATION, function);
        startActivity(intent);
    }

    public void fixedPoint(View v) {
        intent = new Intent(this, FixedPointActivity.class);
        intent.putExtra(Constants.EQUATION, function);
        startActivity(intent);
    }

    public void newton(View v) {
        intent = new Intent(this, NewtonActivity.class);
        intent.putExtra(Constants.EQUATION, function);
        startActivity(intent);
    }

    public void secant(View v) {
        intent = new Intent(this, SecantActivity.class);
        intent.putExtra(Constants.EQUATION, function);
        startActivity(intent);
    }

    public void multipleRoots(View v) {
        intent = new Intent(this, MultipleRootsActivity.class);
        intent.putExtra(Constants.EQUATION, function);
        startActivity(intent);
    }
}