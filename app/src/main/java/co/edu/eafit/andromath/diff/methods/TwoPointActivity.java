package co.edu.eafit.andromath.diff.methods;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.math.BigDecimal;

import co.edu.eafit.andromath.MainActivity;
import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.diff.util.Utils;
import co.edu.eafit.andromath.linearsystems.LinearSystemElectionActivity;

public class TwoPointActivity extends AppCompatActivity {

    Button x0_btn, x1_btn;
    TextView res_et;

    double y[], x[], h, res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_point);
        Intent intent = getIntent();
        x = (double[])intent.getExtras().getSerializable("x");
        y = (double[])intent.getExtras().getSerializable("y");
        h = Math.abs(intent.getExtras().getDouble("h"));
        res_et = (TextView)findViewById(R.id.two_point_diff_res_et);
        String s = BigDecimal.valueOf(x[0]).setScale(6, BigDecimal.ROUND_HALF_UP).toString() + "...";
        x0_btn = (Button)findViewById(R.id.two_point_diff_x0_btn);
        x0_btn.setText(s);
        s = BigDecimal.valueOf(x[1]).setScale(6, BigDecimal.ROUND_HALF_UP).toString() + "...";
        x1_btn = (Button)findViewById(R.id.two_point_diff_x1_btn);
        x1_btn.setText(s);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(TwoPointActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void x0(View v){
        res = Utils.twoPointDiffForward(h,y);
        Log.d("RES", Double.toString(res));
        res_et.setText("f'(x) = " + Double.toString(res));

        res_et.setVisibility(View.VISIBLE);
    }

    public void x1(View v){
        res = Utils.twoPointDiffForward(-h,y);
        Log.d("RES", Double.toString(res));
        res_et.setText("f'(x) = " + Double.toString(res));

        res_et.setVisibility(View.VISIBLE);
    }
}