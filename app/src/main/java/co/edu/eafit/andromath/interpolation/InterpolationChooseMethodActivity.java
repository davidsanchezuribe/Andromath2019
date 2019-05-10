package co.edu.eafit.andromath.interpolation;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import co.edu.eafit.andromath.interpolation.InterpolationLandingActivity;
import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.interpolation.methods.BasedOnESActivity;
import co.edu.eafit.andromath.interpolation.methods.LagrangePolynomialActivity;
import co.edu.eafit.andromath.interpolation.methods.NevillesMethodActivity;
import co.edu.eafit.andromath.interpolation.methods.NewtonPolynomialActivity;
import co.edu.eafit.andromath.interpolation.methods.NewtonPolynomialDDActivity;

public class InterpolationChooseMethodActivity extends AppCompatActivity {

    double points[];
    Bundle bun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interpolation_choose_method);

        Intent intent = getIntent();
        points = (double[]) intent.getExtras().getSerializable("points");
        bun = new Bundle();
        bun.putSerializable("points", points);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(InterpolationChooseMethodActivity.this, InterpolationLandingActivity.class);
        startActivity(i);
    }

    public void boesMethod(View v) {
        Intent i = new Intent(this, BasedOnESActivity.class);
        i.putExtras(bun);
        startActivity(i);
    }

    public void nipMethod(View v) {
        Intent i = new Intent(this, NewtonPolynomialActivity.class);
        i.putExtras(bun);
        startActivity(i);
    }

    public void nipddMethod(View v) {
        Intent i = new Intent(this, NewtonPolynomialDDActivity.class);
        i.putExtras(bun);
        startActivity(i);
    }

    public void lipMethod(View v) {
        Intent i = new Intent(this, LagrangePolynomialActivity.class);
        i.putExtras(bun);
        startActivity(i);
    }

    public void nmMethod(View v) {
        Intent i = new Intent(this, NevillesMethodActivity.class);
        i.putExtras(bun);
        startActivity(i);
    }
}
