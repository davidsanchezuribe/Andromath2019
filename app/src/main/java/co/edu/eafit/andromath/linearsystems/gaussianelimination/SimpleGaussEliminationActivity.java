package co.edu.eafit.andromath.linearsystems.gaussianelimination;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.RpcClient;

import java.math.BigDecimal;
import java.util.Objects;

import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.linearsystems.gaussianelimination.util.MatrixUtils;

import static co.edu.eafit.andromath.linearsystems.gaussianelimination.util.ViewUtils.getPxFromDp;
import static co.edu.eafit.andromath.util.Constants.DECIMALS_QUANTITY;
import static co.edu.eafit.andromath.util.Constants.MATRIX;
import static co.edu.eafit.andromath.util.Constants.ROUNDING_MODE;

public class SimpleGaussEliminationActivity extends AppCompatActivity {

    LinearLayout linearLayoutSolutionStages;

    BigDecimal[][] matrixValues, remoteMatrix;

    String solution;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_gauss_elimination);
        Objects.requireNonNull(getSupportActionBar()).hide();

        linearLayoutSolutionStages = (LinearLayout) findViewById(
                R.id.linearLayoutSolutionStages);

        solution = getResources().getString(R.string.solution);

        Intent intent = getIntent();

        matrixValues = (BigDecimal[][]) intent.
                getExtras().getSerializable(MATRIX);

        remoteMatrix = (BigDecimal[][]) intent.
                getExtras().getSerializable(MATRIX);

        addMatrixSolution(gaussElimination(matrixValues));
    }

    private BigDecimal[] gaussElimination(BigDecimal[][] augmentedMatrix) {

        int n = augmentedMatrix.length;
        BigDecimal multiplier;

        for (int k = 0; k < n - 1; k++) {

            for (int i = k + 1; i < n; i++) {

                multiplier = augmentedMatrix[i][k].divide(augmentedMatrix[k][k],
                        DECIMALS_QUANTITY, ROUNDING_MODE);

                for (int j = k; j < n + 1; j++)
                    augmentedMatrix[i][j] = augmentedMatrix[i][j].subtract(
                            multiplier.multiply(augmentedMatrix[k][j]));
            }

            addSolutionStage(k, augmentedMatrix);
        }

        return MatrixUtils.regressiveSubstitution(augmentedMatrix);
    }

    public void addSolutionStage(int k, BigDecimal[][] matrix) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        HorizontalScrollView horizontalScrollView = new
                HorizontalScrollView(this.getApplicationContext());
        horizontalScrollView.setLayoutParams(layoutParams);
        horizontalScrollView.setHorizontalScrollBarEnabled(false);

        TextView textViewStage = getStageTextView(k, layoutParams);

        TableLayout tableLayoutStage = new TableLayout(
                this.getApplicationContext());

        for (int i = 0; i < matrix.length; i++) {

            TableRow tableRowMatrix = new TableRow(
                    this.getApplicationContext());

            for (int j = 0; j < matrix[0].length; j++) {
                TextView textViewCell = getCellTextView(matrix[i][j]);

                tableRowMatrix.addView(textViewCell,
                        TableRow.LayoutParams.WRAP_CONTENT,
                        getPxFromDp(35));
            }

            tableLayoutStage.addView(tableRowMatrix);
        }

        linearLayoutSolutionStages.addView(textViewStage);
        horizontalScrollView.addView(tableLayoutStage);
        linearLayoutSolutionStages.addView(horizontalScrollView);
    }

    private TextView getStageTextView(int k, LinearLayout.
            LayoutParams layoutParams) {

        TextView textViewStage = new TextView(
                this.getApplicationContext());

        textViewStage.setTextSize(20);
        textViewStage.setLayoutParams(layoutParams);
        textViewStage.setTypeface(null, Typeface.BOLD);

        textViewStage.setPadding(0, getPxFromDp(15),
                0, getPxFromDp(5));

        textViewStage.setText("STAGE #" + (k + 1));
        textViewStage.setAllCaps(true);

        return textViewStage;
    }

    private TextView getCellTextView(BigDecimal cellValue) {

        TextView textViewCell = new TextView(
                this.getApplicationContext());

        textViewCell.setText(String.valueOf(cellValue.
                setScale(3, ROUNDING_MODE).doubleValue()));
        textViewCell.setPadding(20, 10, 20, 10);
        textViewCell.setGravity(Gravity.CENTER);

        return textViewCell;
    }

    private void addMatrixSolution(BigDecimal[] systemSolved) {

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;

        TextView textViewSolution = getSolutionTextView(layoutParams);

        TextView[] systemSolution = new TextView[systemSolved.length];

        linearLayoutSolutionStages.addView(textViewSolution);

        for (int i = 0; i < systemSolution.length; i++) {

            systemSolution[i] = new TextView(
                    this.getApplicationContext());

            systemSolution[i].setText("X" + (i + 1) + " = " + systemSolved[i].
                    setScale(DECIMALS_QUANTITY, ROUNDING_MODE).doubleValue());

            systemSolution[i].setPadding(0,
                    getPxFromDp(10), 0, getPxFromDp(5));
            systemSolution[i].setGravity(Gravity.CENTER);

            linearLayoutSolutionStages.addView(systemSolution[i]);
        }
    }

    private TextView getSolutionTextView(LinearLayout.LayoutParams layoutParams) {

        TextView textViewSolution = new TextView(
                this.getApplicationContext());

        textViewSolution.setTextSize(20);
        textViewSolution.setTypeface(null, Typeface.BOLD);
        textViewSolution.setLayoutParams(layoutParams);

        textViewSolution.setPadding(0, getPxFromDp(15),
                0, getPxFromDp(5));

        textViewSolution.setText(solution);
        textViewSolution.setAllCaps(true);

        return textViewSolution;
    }

    public void remoteExecution(View v) {
        remoteSolution(MatrixUtils.matrixAsString(remoteMatrix));
    }

    public void remoteSolution(String matrix) {

        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            String uri = "amqp://sbvfmqsr:TyV5Xs3YxndY8n-jqCIM4eDhFQgqM7gW@otter.rmq.cloudamqp.com/sbvfmqsr";

            ConnectionFactory factory = new ConnectionFactory();
            factory.setUri(uri);

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();
            RpcClient service = new RpcClient(
                    channel, "", "SimpleGaussElimination");

            Toast.makeText(this.getApplicationContext(), "Sending", Toast.LENGTH_LONG).show();

            showRemoteSolution(service.stringCall(matrix));
            connection.close();
        } catch (Exception e) {
            System.err.println("Main thread caught exception: " + e);
            e.printStackTrace();
        }
    }

    private void showRemoteSolution(String solution) {

        AlertDialog.Builder builderDialog =
                new AlertDialog.Builder(this);

        builderDialog.setTitle("REMOTE EXECUTION");
        builderDialog.setMessage(solution);
        builderDialog.setCancelable(true);

        builderDialog.setNeutralButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog solutionDialog = builderDialog.create();
        solutionDialog.show();
    }
}