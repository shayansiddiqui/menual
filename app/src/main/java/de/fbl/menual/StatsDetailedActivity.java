package de.fbl.menual;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import de.fbl.menual.models.FoodItem;
import de.fbl.menual.utils.Constants;

public class StatsDetailedActivity extends AppCompatActivity {
    double[] statistics = new double[0];
    String[] statisticsText = new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats_detailed);
        Bundle extras = getIntent().getExtras();
        FoodItem foodItem = (FoodItem) extras.get(Constants.FOOD_ITEM_KEY);
        this.statistics = foodItem.getStaticsValues();
        this.statisticsText = foodItem.getStatisticText();

        initializeBottomBars();
    }

    private void initializeBottomBars() {
        final ArrayList<String> xVals = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList();
        for(int i=16; i<38;i++){
            if(statistics[i]>-1){
                xVals.add(statisticsText[i]);
                entries.add(new BarEntry(i-16, (float) statistics[i]));
            }
        }

        HorizontalBarChart horizontalBarChart = (HorizontalBarChart) findViewById(R.id.statistics_detailed);

        XAxis bottomAxis = horizontalBarChart.getXAxis();
        bottomAxis.setLabelCount(entries.size());

        bottomAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        BarDataSet dataSet = new BarDataSet(entries, "Hi");
        BarData barData = new BarData(dataSet);

        horizontalBarChart.setData(barData);
        horizontalBarChart.setDrawBorders(false);
        bottomAxis.setDrawGridLines(false);

        horizontalBarChart.getAxisRight().setDrawGridLines(false);
        horizontalBarChart.getAxisRight().setDrawLabels(false);
        horizontalBarChart.getAxisLeft().setDrawGridLines(false);
        horizontalBarChart.getAxisLeft().setDrawLabels(false);

        horizontalBarChart.getDescription().setEnabled(false);
        horizontalBarChart.getLegend().setEnabled(false);

        horizontalBarChart.getAxisRight().setAxisMaximum(1);
        horizontalBarChart.getAxisLeft().setAxisMaximum(1);
        horizontalBarChart.getAxisLeft().setAxisMinimum(0);

        horizontalBarChart.animateXY(1000, 1000);


        horizontalBarChart.invalidate();
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                StatsDetailedActivity.this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
