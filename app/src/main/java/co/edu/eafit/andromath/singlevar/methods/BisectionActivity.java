package co.edu.eafit.andromath.singlevar.methods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import co.edu.eafit.andromath.evalex.Expression;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.util.Messages;

import static co.edu.eafit.andromath.util.Constants.EQUATION;
import static co.edu.eafit.andromath.util.Constants.ErrorCodes.INVALID_ITER;
import static co.edu.eafit.andromath.util.Constants.ErrorCodes.INVALID_RANGE;
import static co.edu.eafit.andromath.util.Constants.ErrorCodes.OUT_OF_RANGE;
import static co.edu.eafit.andromath.util.Constants.ErrorCodes.X_ROOT;
import static co.edu.eafit.andromath.util.Constants.NOTATION_FORMAT;
import static co.edu.eafit.andromath.util.Constants.VARIABLE;

public class BisectionActivity extends AppCompatActivity {

    private static final String tag = BisectionActivity.class.getSimpleName();   //

    EditText xMinInput, xMaxInput, toleranceInput, iterationsInput, EditFunction;
    TextView function, result, iterations, solution, xMin,
            xMed, xMax, tolerance, solutionA, solutionB;
    Expression expression, expression2;
    TableLayout procedure;
    Button btnAcpFunction, btnFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bisection);
        Objects.requireNonNull(getSupportActionBar()).hide();

        xMinInput = (EditText) findViewById(R.id.editTextXMinValue);
        xMaxInput = (EditText) findViewById(R.id.editTextXMaxValue);
        EditFunction = (EditText) findViewById(R.id.EditFunction);
        toleranceInput = (EditText) findViewById(R.id.editTextTolerance);
        iterationsInput = (EditText) findViewById(R.id.editTextIterations);
        btnAcpFunction = (Button) findViewById(R.id.btnAcpFunction);
        btnFunction = (Button) findViewById(R.id.btnFunction);

        function = (TextView) findViewById(R.id.textViewFunction);
        result = (TextView) findViewById(R.id.textViewResult);

        procedure = (TableLayout) findViewById(R.id.tableLayoutProcedure);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        String equation = intent.
                getStringExtra(EQUATION);
        function.setText(equation);

        expression = new Expression(intent.
                getStringExtra(EQUATION));

        procedure.setStretchAllColumns(true);
    }

    public void change(View v){
        EditFunction.setVisibility(View.VISIBLE);
        EditFunction.setText(function.getText());
        function.setVisibility(View.INVISIBLE);
        btnAcpFunction.setVisibility(View.VISIBLE);
        btnFunction.setVisibility(View.INVISIBLE);
    }
    public void change2(View v){
        EditFunction.setVisibility(View.INVISIBLE);
        function.setText(EditFunction.getText());
        expression2 = new Expression(EditFunction.getText().toString());
        expression = expression2;
        function.setVisibility(View.VISIBLE);
        btnAcpFunction.setVisibility(View.INVISIBLE);
        btnFunction.setVisibility(View.VISIBLE);
    }


    public void bisection(View v) {

        xMinInput.setSelected(false);
        xMaxInput.setSelected(false);
        toleranceInput.setSelected(false);
        iterationsInput.setSelected(false);
        result.setVisibility(View.VISIBLE);

        List<TableRow> tableIterations = new ArrayList<>();

        procedure.removeViews(1,
                procedure.getChildCount() - 1);

        Pair<String, Boolean> solution =
                bisection(tableIterations);

        if (solution != null) {
            result.setText(solution.first);
            createTableProcedure(tableIterations);
            procedure.setVisibility(solution.second ?
                    View.VISIBLE : View.INVISIBLE);
        } else {
            Messages.invalidEquation(tag,
                    getApplicationContext());
        }
    }

    /**
     * @return Pair<String, Boolean>
     *     String parameter is the message.
     *     Boolean parameter is a flag to show the procedure.
     */
    private Pair<String, Boolean> bisection(List<TableRow> tableIterations) {

        String message;

        boolean displayProcedure;
        try {
            BigDecimal xi = BigDecimal.valueOf(Double.parseDouble(xMinInput.getText().toString()));
            BigDecimal xs = BigDecimal.valueOf(Double.parseDouble(xMaxInput.getText().toString()));
            BigDecimal tol = BigDecimal.valueOf(Double.parseDouble(toleranceInput.getText().toString()));
            int niter = Integer.parseInt(iterationsInput.getText().toString());
            BigDecimal xaux;

            BigDecimal yi = expression.with(VARIABLE, xi).eval();
            BigDecimal ys = expression.with(VARIABLE, xs).eval();
            if (niter < 0) {
                message = INVALID_ITER.getMessage();
                displayProcedure = INVALID_ITER.isDisplayProcedure();
            } else if (yi.compareTo(BigDecimal.ZERO) == 0) {
                message = X_ROOT.getMessage();
                displayProcedure = X_ROOT.isDisplayProcedure();

            } else if (ys.compareTo(BigDecimal.ZERO) == 0) {
                message = X_ROOT.getMessage();
                displayProcedure = X_ROOT.isDisplayProcedure();
            } else if (yi.multiply(ys).compareTo(BigDecimal.ZERO) < 0) {
                BigDecimal xm = (xi.add(xs)).divide(BigDecimal.valueOf(2));
                BigDecimal ym = expression.with("x", xm).eval();
                int count = 1;
                BigDecimal error = tol.add(BigDecimal.ONE);

                tableIterations.add(createProcedureIteration(count, xi, xs, yi, ys, xm, ym, error));
                while (ym.compareTo(BigDecimal.ZERO) != 0 && error.compareTo(tol) > 0 && count < niter) {
                    if (yi.multiply(ym).compareTo(BigDecimal.ZERO) < 0) {
                        xs = xm;
                        ys = ym;
                    } else {
                        xi = xm;
                        yi = ym;
                    }
                    xaux = xm;
                    //xm = (xi + xs)/
                    /*, BigDecimal.ROUND_HALF_EVEN*/
                    xm = (xi.add(xs)).divide(BigDecimal.valueOf(2));
                    //ym = f(xm)
                    ym = expression.with(VARIABLE, xm).eval();
                    //error = abs(xm-xaux)
                    error = xm.subtract(xaux).abs();
                    count++;

                    tableIterations.add(createProcedureIteration(count, xi, xs, yi, ys, xm, ym, error));
                }
                if (ym.compareTo(BigDecimal.ZERO) == 0) {
                    tableIterations.add(createProcedureIteration(count + 1, xi, xs, yi, ys, xm, ym, error));
                    message = "x = " + xm.toString() + " is a root";
                    displayProcedure = true;
                } else if (error.compareTo(tol) < 0) {
                    tableIterations.add(createProcedureIteration(count + 1, xi, xs, yi, ys, xm, ym, error));
                    message = "x = " + xm.toString() + " is an approximated root\nwith E = " + error.toString();
                    displayProcedure = true;
                } else {
                    message = "The method failed after "
                            + count + " iterations";
                    displayProcedure = false;
                }
            } else {
                displayProcedure = INVALID_RANGE.isDisplayProcedure();
                message = INVALID_RANGE.getMessage();
            }
        } catch (Expression.ExpressionException e) {
            return null; // The equation is not valid.
        } catch (ArithmeticException | NumberFormatException e) {
            displayProcedure = OUT_OF_RANGE.isDisplayProcedure();
            message = OUT_OF_RANGE.getMessage();
        }

        return new Pair<>(message, displayProcedure);
    }

    private TableRow createProcedureIteration(int count, BigDecimal xi,
                                              BigDecimal xs, BigDecimal yi, BigDecimal ys,
                                              BigDecimal xm, BigDecimal ym, BigDecimal Error) {

        TableRow iterationResult = new TableRow(this);

        NumberFormat formatter = new DecimalFormat(NOTATION_FORMAT);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMinimumFractionDigits(3);

        iterations = new TextView(this);
        iterations.setPadding(15, 10, 15, 10);
        iterations.setGravity(Gravity.CENTER);
        iterations.setText(String.valueOf(count));

        xMin = new TextView(this);
        xMin.setPadding(15, 10, 15, 10);
        xMin.setGravity(Gravity.CENTER);
        //xMin.setText(String.valueOf(xi));
        xMin.setText(formatter.format(xi));

        solutionA = new TextView(this);
        solutionA.setPadding(15, 10, 15, 10);
        solutionA.setGravity(Gravity.CENTER);
        solutionA.setText(formatter.format(yi));

        xMax = new TextView(this);
        xMax.setPadding(15, 10, 15, 10);
        xMax.setGravity(Gravity.CENTER);
        xMax.setText(formatter.format(xs));

        solutionB = new TextView(this);
        solutionB.setPadding(15, 10, 15, 10);
        solutionB.setGravity(Gravity.CENTER);
        solutionB.setText(formatter.format(ys));

        xMed = new TextView(this);
        xMed.setPadding(15, 10, 15, 10);
        xMed.setGravity(Gravity.CENTER);
        xMed.setText(formatter.format(xm));

        solution = new TextView(this);
        solution.setPadding(15, 10, 15, 10);
        solution.setGravity(Gravity.CENTER);
        solution.setText(formatter.format(ym));

        tolerance = new TextView(this);
        tolerance.setPadding(15, 10, 15, 10);
        tolerance.setGravity(Gravity.CENTER);
        tolerance.setText(formatter.format(Error));

        iterationResult.addView(iterations);
        iterationResult.addView(xMin);
        iterationResult.addView(solutionA);
        iterationResult.addView(xMax);
        iterationResult.addView(solutionB);
        iterationResult.addView(xMed);
        iterationResult.addView(solution);
        iterationResult.addView(tolerance);

        return iterationResult;
    }

    private void createTableProcedure(List<TableRow> tableIterations) {

        for (TableRow tableRow : tableIterations) {
            procedure.addView(tableRow);
        }
    }

}