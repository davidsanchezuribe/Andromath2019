package co.edu.eafit.andromath.diff.methods;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Space;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.Arrays;

import co.edu.eafit.andromath.MainActivity;
import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.diff.util.Utils;
import co.edu.eafit.andromath.linearsystems.LinearSystemElectionActivity;

public class FivePointDiffActivity extends AppCompatActivity {

    Button x0_btn, x1_btn, x2_btn, x3_btn, x4_btn;
    TextView res_et, textView8;

    double y[], x[], h, res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_five_point_diff);
        Intent intent = getIntent();
        x = (double[])intent.getExtras().getSerializable("x");
        y = (double[])intent.getExtras().getSerializable("y");
        h = Math.abs(intent.getExtras().getDouble("h"));

        res_et = (TextView)findViewById(R.id.five_point_diff_res_et);
        String s = BigDecimal.valueOf(x[0]).setScale(6, BigDecimal.ROUND_HALF_UP).toString() + "...";
        x0_btn = (Button)findViewById(R.id.five_point_diff_x0_btn);
        x0_btn.setText(s);
        s = BigDecimal.valueOf(x[1]).setScale(6, BigDecimal.ROUND_HALF_UP).toString() + "...";
        x1_btn = (Button)findViewById(R.id.five_point_diff_x1_btn);
        x1_btn.setText(s);
        s = BigDecimal.valueOf(x[2]).setScale(6, BigDecimal.ROUND_HALF_UP).toString() + "...";
        x2_btn = (Button)findViewById(R.id.five_point_diff_x2_btn);
        x2_btn.setText(s);
        s = BigDecimal.valueOf(x[3]).setScale(6, BigDecimal.ROUND_HALF_UP).toString() + "...";
        x3_btn = (Button)findViewById(R.id.five_point_diff_x3_btn);
        x3_btn.setText(s);
        s = BigDecimal.valueOf(x[4]).setScale(6, BigDecimal.ROUND_HALF_UP).toString() + "...";
        x4_btn = (Button)findViewById(R.id.five_point_diff_x4_btn);
        x4_btn.setText(s);

        textView8 = (TextView) findViewById(R.id.textView8);
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(FivePointDiffActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void x0(View v){
        res = Utils.fivePointDiffForward(h,y);
        Log.d("RES", Double.toString(res));
        res_et.setText("f'(x) = " + Double.toString(res));

        textView8.setVisibility(View.VISIBLE);
        res_et.setVisibility(View.VISIBLE);
    }

    public void x1(View v){
        res = Utils.threePointDiffForward(h, Arrays.copyOfRange(y,1,4));
        Log.d("RES", Double.toString(res));
        res_et.setText("f'(x) = " + Double.toString(res));

        textView8.setVisibility(View.VISIBLE);
        res_et.setVisibility(View.VISIBLE);
    }

    public void x2(View v){
        res = Utils.fivePointDiffCenter(h, y);
        Log.d("RES", Double.toString(res));
        res_et.setText("f'(x) = " + Double.toString(res));

        textView8.setVisibility(View.VISIBLE);
        res_et.setVisibility(View.VISIBLE);
    }

    public void x3(View v){
        res = Utils.threePointDiffForward(-h,Arrays.copyOfRange(y,1,4));
        Log.d("RES", Double.toString(res));
        res_et.setText("f'(x) = " + Double.toString(res));

        textView8.setVisibility(View.VISIBLE);
        res_et.setVisibility(View.VISIBLE);
    }

    public void x4(View v){
        res = Utils.fivePointDiffForward(-h,y);
        Log.d("RES", Double.toString(res));
        res_et.setText("f'(x) = " + Double.toString(res));

        textView8.setVisibility(View.VISIBLE);
        res_et.setVisibility(View.VISIBLE);
    }
}