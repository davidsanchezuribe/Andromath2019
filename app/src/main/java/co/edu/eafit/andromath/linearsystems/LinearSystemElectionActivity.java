package co.edu.eafit.andromath.linearsystems;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.math.BigDecimal;
import java.util.Objects;

import co.edu.eafit.andromath.R;
import co.edu.eafit.andromath.linearsystems.directfactorization.DirectLUFactorizationActivity;
import co.edu.eafit.andromath.linearsystems.gaussianelimination.PartialPivotingGaussEliminationActivity;
import co.edu.eafit.andromath.linearsystems.gaussianelimination.SimpleGaussEliminationActivity;
import co.edu.eafit.andromath.linearsystems.gaussianelimination.TotalPivotingGaussEliminationActivity;
import co.edu.eafit.andromath.linearsystems.iterativemethods.GaussSeidelActivity;
import co.edu.eafit.andromath.linearsystems.iterativemethods.JacobiActivity;
import co.edu.eafit.andromath.linearsystems.lufactorization.PivotingGaussLUFactorizationActivity;
import co.edu.eafit.andromath.linearsystems.lufactorization.SimpleGaussLUFactorizationActivity;

import static co.edu.eafit.andromath.util.Constants.MATRIX;
import static co.edu.eafit.andromath.util.Constants.SELECTION;

public class LinearSystemElectionActivity extends AppCompatActivity {

    Bundle bundleMatrix;

    AlertDialog alertGaussianElimination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_system_election);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Intent intent = getIntent();

        BigDecimal[][] matrixValues = (BigDecimal[][]) intent.
                getExtras().getSerializable(MATRIX);

        bundleMatrix = new Bundle();
        bundleMatrix.putSerializable(MATRIX, matrixValues);
    }

    public void gaussianElimination(View v) {
        CharSequence[] values = {"SIMPLE ELIMINATION",
                "PARTIAL PIVOTING", "TOTAL PIVOTING"};

        AlertDialog.Builder builder = new AlertDialog.Builder(
                LinearSystemElectionActivity.this);

        builder.setTitle("GAUSSIAN ELIMINATION");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                Intent intent;

                switch (item) {
                    case 0:
                        intent = new Intent(LinearSystemElectionActivity.this,
                                SimpleGaussEliminationActivity.class);
                        intent.putExtras(bundleMatrix);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(LinearSystemElectionActivity.this,
                                PartialPivotingGaussEliminationActivity.class);
                        intent.putExtras(bundleMatrix);
                        startActivity(intent);
                        break;
                    case 2:
                        intent = new Intent(LinearSystemElectionActivity.this,
                                TotalPivotingGaussEliminationActivity.class);
                        intent.putExtras(bundleMatrix);
                        startActivity(intent);
                        break;
                }

                alertGaussianElimination.dismiss();
            }
        });

        alertGaussianElimination = builder.create();
        alertGaussianElimination.show();
    }

    public void luFactorization(View v) {

        CharSequence[] values = {"SIMPLE ELIMINATION", "PARTIAL PIVOTING"};

        AlertDialog.Builder builder = new AlertDialog.Builder(
                LinearSystemElectionActivity.this);

        builder.setTitle("LUMatrix FACTORIZATION");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                Intent intent;

                switch (item) {
                    case 0:
                        intent = new Intent(LinearSystemElectionActivity.this,
                                SimpleGaussLUFactorizationActivity.class);
                        intent.putExtras(bundleMatrix);
                        startActivity(intent);
                        break;
                    case 1:
                        intent = new Intent(LinearSystemElectionActivity.this,
                                PivotingGaussLUFactorizationActivity.class);
                        intent.putExtras(bundleMatrix);
                        startActivity(intent);
                        break;
                }

                alertGaussianElimination.dismiss();
            }
        });

        alertGaussianElimination = builder.create();
        alertGaussianElimination.show();
    }

    public void directFactorization(View v) {

        CharSequence[] values = {"DOOLITTLE", "CHOLESKY", "CROUT"};

        AlertDialog.Builder builder = new AlertDialog.Builder(
                LinearSystemElectionActivity.this);

        builder.setTitle("DIRECT FACTORIZATION");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                Intent intent;

                switch (item) {
                    case 0:
                        bundleMatrix.putSerializable(SELECTION, 0);
                        intent = new Intent(LinearSystemElectionActivity.this,
                                DirectLUFactorizationActivity.class);
                        intent.putExtras(bundleMatrix);
                        startActivity(intent);
                        break;
                    case 1:
                        bundleMatrix.putSerializable(SELECTION, 1);
                        intent = new Intent(LinearSystemElectionActivity.this,
                                DirectLUFactorizationActivity.class);
                        intent.putExtras(bundleMatrix);
                        startActivity(intent);
                        break;
                    case 2:
                        bundleMatrix.putSerializable(SELECTION, 2);
                        intent = new Intent(LinearSystemElectionActivity.this,
                                DirectLUFactorizationActivity.class);
                        intent.putExtras(bundleMatrix);
                        startActivity(intent);
                        break;
                }

                alertGaussianElimination.dismiss();
            }
        });

        alertGaussianElimination = builder.create();
        alertGaussianElimination.show();
    }

    public void iterativeMethods(View v) {

        CharSequence[] values = {"JACOBI METHOD","GAUSS SEIDEL"};
        AlertDialog.Builder builder = new AlertDialog.Builder(LinearSystemElectionActivity.this);
        builder.setTitle("ITERATIVE METHODS");

        builder.setSingleChoiceItems(values, -1, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                Intent i;

                switch(item) {
                    case 0:
                        i = new Intent(LinearSystemElectionActivity.this,
                                JacobiActivity.class);
                        i.putExtras(bundleMatrix);
                        startActivity(i);
                        break;
                    case 1:
                        i = new Intent(LinearSystemElectionActivity.this,
                                GaussSeidelActivity.class);
                        i.putExtras(bundleMatrix);
                        startActivity(i);
                        break;
                }
                alertGaussianElimination.dismiss();
            }
        });

        alertGaussianElimination = builder.create();
        alertGaussianElimination.show();
    }
}
