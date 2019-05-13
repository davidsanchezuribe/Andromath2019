package co.edu.eafit.andromath.singlevar.methods;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
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
import static co.edu.eafit.andromath.util.Constants.ErrorCodes.OUT_OF_RANGE;
import static co.edu.eafit.andromath.util.Constants.ErrorCodes.X_ROOT;
import static co.edu.eafit.andromath.util.Constants.NOTATION_FORMAT;
import static co.edu.eafit.andromath.util.Constants.VARIABLE;

public class NewtonActivity extends AppCompatActivity {

    private static final String tag = NewtonActivity.class.getSimpleName();

    EditText initialValueInput, derivativeInput, toleranceInput, iterationsInput;
    TextView function, result, iterations, approximatedX, ySolution, yDerivative, tolerance;
    Expression expressionF, expressionG;
    TableLayout procedure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newton);
        Objects.requireNonNull(getSupportActionBar()).hide();

        initialValueInput = (EditText) findViewById(R.id.editTextInitialValue);
        derivativeInput = (EditText) findViewById(R.id.editTextDerivative);
        toleranceInput = (EditText) findViewById(R.id.editTextTolerance);
        iterationsInput = (EditText) findViewById(R.id.editTextIterations);

        function = (TextView) findViewById(R.id.textViewFunction);
        result = (TextView) findViewById(R.id.textViewResult);

        procedure = (TableLayout) findViewById(R.id.tableLayoutProcedure);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = getIntent();
        String equation = "f(x) = " + intent.
                getStringExtra(EQUATION);
        function.setText(equation);

        expressionF = new Expression(intent.
                getStringExtra(EQUATION));

        procedure.setStretchAllColumns(true);
    }

    public void newton(View v) {

        initialValueInput.setSelected(false);
        derivativeInput.setSelected(false);
        toleranceInput.setSelected(false);
        iterationsInput.setSelected(false);
        result.setVisibility(View.VISIBLE);

        List<TableRow> tableIterations = new ArrayList<>();

        procedure.removeViews(1,
                procedure.getChildCount() - 1);

        Pair<String, Boolean> solution =
                newton(tableIterations);

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
    public Pair<String, Boolean> newton(List<TableRow> tableIterations) {

        String message;

        boolean displayProcedure;

        try {
            BigDecimal x0 = BigDecimal.valueOf(Double.parseDouble(
                    initialValueInput.getText().toString()));

            BigDecimal tol = BigDecimal.valueOf(Double.parseDouble(
                    toleranceInput.getText().toString()));

            BigDecimal x1;
            int niter = Integer.parseInt(iterationsInput.getText().toString());
            expressionG = new Expression(derivativeInput.getText().toString());

            BigDecimal y = expressionF.with(VARIABLE, x0).eval();
            BigDecimal dy = expressionG.with(VARIABLE, x0).eval();
            int count = 0;
            BigDecimal error = tol.add(BigDecimal.ONE);

            if (niter < 1) {
                message = INVALID_ITER.getMessage();
                displayProcedure = INVALID_ITER.isDisplayProcedure();
            } else if (y.compareTo(BigDecimal.ZERO) == 0) {
                message = X_ROOT.getMessage();
                displayProcedure = X_ROOT.isDisplayProcedure();
            }

            tableIterations.add(createProcedureIteration(count + 1, x0, y, dy, error));
            while (error.compareTo(tol) > 0 && y.compareTo(BigDecimal.ZERO) != 0 && dy.compareTo(BigDecimal.ZERO) != 0 && count < niter) {
                //x1 = x0 - (y/dy)
                BigDecimal div = new BigDecimal(y.divide(dy, 5, BigDecimal.ROUND_HALF_EVEN).toString());
                x1 = x0.subtract(div);
                y = expressionF.with(VARIABLE, x1).eval();
                dy = expressionG.with(VARIABLE, x1).eval();
                error = x1.subtract(x0).abs();
                x0 = x1;
                count++;

                tableIterations.add(createProcedureIteration(count + 1, x0, y, dy, error));
            }

            if (y.compareTo(BigDecimal.ZERO) == 0) {
                tableIterations.add(createProcedureIteration(count + 1, x0, y, dy, error));
                message = "x = " + x0.toString() + " is a root";
                displayProcedure = true;
            } else if (error.compareTo(tol) < 0) {
                message = "x = " + x0.toString() + " is an approximated root\nwith E = " + error.toString();
                tableIterations.add(createProcedureIteration(count + 1, x0, y, dy, error));
                displayProcedure = true;
            } else if (dy.compareTo(BigDecimal.ZERO) == 0) {
                message = "at x = " + x0.toString() + " there are possibly multiple roots";
                displayProcedure = false;
            } else {
                message = "The method failed after "
                        + count + " iterations";
                displayProcedure = true;
            }
        } catch (Expression.ExpressionException e) {
            return null; // The equation is not valid.
        } catch (ArithmeticException | NumberFormatException e) {
            displayProcedure = OUT_OF_RANGE.isDisplayProcedure();
            message = OUT_OF_RANGE.getMessage();
        }

        return new Pair<>(message, displayProcedure);
    }


    private TableRow createProcedureIteration(int count, BigDecimal x, BigDecimal y,
                                              BigDecimal dy, BigDecimal error) {

        TableRow iterationResult = new TableRow(this);

        NumberFormat formatter = new DecimalFormat(NOTATION_FORMAT);
        formatter.setRoundingMode(RoundingMode.HALF_UP);
        formatter.setMinimumFractionDigits(3);

        iterations = new TextView(this);
        iterations.setPadding(15, 10, 15, 10);
        iterations.setGravity(Gravity.CENTER);
        iterations.setText(String.valueOf(count));

        approximatedX = new TextView(this);
        approximatedX.setPadding(15, 10, 15, 10);
        approximatedX.setGravity(Gravity.CENTER);
        approximatedX.setText(formatter.format(x));

        ySolution = new TextView(this);
        ySolution.setPadding(15, 10, 15, 10);
        ySolution.setGravity(Gravity.CENTER);
        ySolution.setText(formatter.format(y));

        yDerivative = new TextView(this);
        yDerivative.setPadding(15, 10, 15, 10);
        yDerivative.setGravity(Gravity.CENTER);
        yDerivative.setText(formatter.format(dy));

        tolerance = new TextView(this);
        tolerance.setPadding(15, 10, 15, 10);
        tolerance.setGravity(Gravity.CENTER);
        tolerance.setText(formatter.format(error));

        iterationResult.addView(iterations);
        iterationResult.addView(approximatedX);
        iterationResult.addView(ySolution);
        iterationResult.addView(yDerivative);
        iterationResult.addView(tolerance);

        return iterationResult;
    }

    private void createTableProcedure(List<TableRow> tableIterations) {

        for (TableRow tableRow : tableIterations) {
            procedure.addView(tableRow);
        }
    }
}