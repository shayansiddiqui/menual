package de.fbl.menual;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.fbl.menual.adapters.FoodListAdapter;
import de.fbl.menual.models.FoodItem;
import de.fbl.menual.utils.Constants;

public class StatisticsActivity extends AppCompatActivity {

    int resultColor = R.color.yellow;
    double[] statistics = new double[0];
    String[] statisticsText = new String[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Bundle extras = getIntent().getExtras();
        FoodItem foodItem = (FoodItem) extras.get(Constants.FOOD_ITEM_KEY);


        this.resultColor = getResultColor(foodItem.getResult());
        this.statistics = foodItem.getStaticsValues();
        this.statisticsText = foodItem.getStatisticText();

        TextView statisticsTitle = (TextView) findViewById(R.id.statistics_title);
        statisticsTitle.setText(foodItem.getStatisticText()[0]);

        ImageView statisticsFoodImage = (ImageView) findViewById(R.id.statistics_food_image);
        new StatisticsActivity.ImageLoadTask(foodItem.getLowResPhoto(), statisticsFoodImage).execute();

        initializeTotalScoreBar();

        if (foodItem.getComments() != null && foodItem.getComments().get("comment1").length() > 10) {

            ImageView listItemIcon = (ImageView) findViewById(R.id.thumbs_icon);
            listItemIcon.setBackgroundResource(getResultIcon(foodItem.getResult()));

            TextView statisticsComment1 = (TextView) findViewById(R.id.statistics_comment1);
            statisticsComment1.setText(foodItem.getComments().get("comment1"));
            statisticsComment1.setTextColor(getResources().getColor(resultColor));
//            if (foodItem.getComments().get("comment2").length() > 10) {
//                TextView statisticsComment2 = (TextView) findViewById(R.id.statistics_comment2);
//                statisticsComment2.setText(foodItem.getComments().get("comment2"));
//            }

        }

        initialzePieChart();
        initializeBottomBars();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                StatisticsActivity.this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initializeTotalScoreBar() {
        final ArrayList<String> xVals = new ArrayList<>();
        xVals.add(statisticsText[1]);

        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(0, (float) statistics[1]));

        HorizontalBarChart horizontalBarChart = (HorizontalBarChart) findViewById(R.id.total_score_value_bar);
        XAxis bottomAxis = horizontalBarChart.getXAxis();

        bottomAxis.setLabelCount(entries.size(), true);
        bottomAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        bottomAxis.setCenterAxisLabels(true);

        BarDataSet dataSet = new BarDataSet(entries, "Hi");
        BarData barData = new BarData(dataSet);
        dataSet.setColor(getResources().getColor(resultColor));
        horizontalBarChart.setData(barData);
        horizontalBarChart.setDrawBorders(false);
        bottomAxis.setDrawGridLines(false);
//        bottomAxis.setDrawLabels(false);

        horizontalBarChart.getAxisRight().setDrawGridLines(false);
        horizontalBarChart.getAxisRight().setDrawLabels(false);
        horizontalBarChart.getAxisLeft().setDrawGridLines(false);
        horizontalBarChart.getAxisLeft().setDrawLabels(false);

        horizontalBarChart.getDescription().setEnabled(false);
        horizontalBarChart.getLegend().setEnabled(false);
        horizontalBarChart.getAxisRight().setAxisMaximum(100);
        horizontalBarChart.getAxisLeft().setAxisMaximum(100);
        horizontalBarChart.getAxisLeft().setAxisMinimum(0);

        horizontalBarChart.animateXY(1000, 1000);
        horizontalBarChart.invalidate();
    }


    private void initialzePieChart() {
        PieChart pieChart = (PieChart) findViewById(R.id.statistics_piechart);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) statistics[8], statisticsText[10]));
        entries.add(new PieEntry((float) statistics[9], statisticsText[9]));
        entries.add(new PieEntry((float) statistics[10], statisticsText[8]));
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setValueTextSize(16);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setText("Nutrition Distribution");
        pieChart.animateXY(1000, 1000);
    }

    private void initializeBottomBars() {
        final ArrayList<String> xVals = new ArrayList<>();
        xVals.add(statisticsText[7]);
        xVals.add(statisticsText[6]);
        xVals.add(statisticsText[5]);
        xVals.add(statisticsText[3]);
        xVals.add(statisticsText[4]);
        xVals.add(statisticsText[2]);

        ArrayList<BarEntry> entries = new ArrayList();
        entries.add(new BarEntry(5, (float) statistics[2]));
        entries.add(new BarEntry(4, (float) statistics[4]));
        entries.add(new BarEntry(3, (float) statistics[3]));
        entries.add(new BarEntry(2, (float) statistics[5]));
        entries.add(new BarEntry(1, (float) statistics[6]));
        entries.add(new BarEntry(0, (float) statistics[7]));

        HorizontalBarChart horizontalBarChart = (HorizontalBarChart) findViewById(R.id.statistics_bottom_bar);

        XAxis bottomAxis = horizontalBarChart.getXAxis();
        bottomAxis.setLabelCount(entries.size());

        bottomAxis.setValueFormatter(new IndexAxisValueFormatter(xVals));
        bottomAxis.setPosition(XAxis.XAxisPosition.BOTTOM);


        BarDataSet dataSet = new BarDataSet(entries, "Hi");
        BarData barData = new BarData(dataSet);

        dataSet.setColor(getResources().getColor(resultColor));
        horizontalBarChart.setData(barData);
        horizontalBarChart.setDrawBorders(false);
        bottomAxis.setDrawGridLines(false);

        horizontalBarChart.getAxisRight().setDrawGridLines(false);
        horizontalBarChart.getAxisRight().setDrawLabels(false);
        horizontalBarChart.getAxisLeft().setDrawGridLines(false);
        horizontalBarChart.getAxisLeft().setDrawLabels(false);

        horizontalBarChart.getDescription().setEnabled(false);
        horizontalBarChart.getLegend().setEnabled(false);

        horizontalBarChart.getAxisRight().setAxisMaximum(100);
        horizontalBarChart.getAxisLeft().setAxisMaximum(100);
        horizontalBarChart.getAxisLeft().setAxisMinimum(0);

        horizontalBarChart.animateXY(1000, 1000);
        horizontalBarChart.invalidate();
        dataSet.setColors(getColorFromScore(statistics[2]), getColorFromScore(statistics[4]), getColorFromScore(statistics[3]), getColorFromScore(statistics[5]), getResources().getColor(R.color.other), getResources().getColor(R.color.other));

    }

    private int getResultIcon(String result) {
        int background = R.drawable.yellow;
        switch (result) {
            case "green":
                background = R.drawable.green;
                break;
            case "red":
                background = R.drawable.red;
                break;
            case "yellow":
                background = R.drawable.yellow;
                break;
        }
        return background;
    }

    private int getColorFromScore(double score) {
        if (score >= 100) {
            return getResources().getColor(R.color.green);
        } else if (score > 90) {
            return getResources().getColor(R.color.yellow);
        } else {
            return getResources().getColor(R.color.red);
        }
    }

    private int getResultColor(String result) {
        int color = R.color.yellow;
        switch (result) {
            case "green":
                color = R.color.green;
                break;
            case "red":
                color = R.color.red;
                break;
            case "yellow":
                color = R.color.yellow;
                break;
        }
        return color;
    }

    private class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                if (url == null || url.isEmpty()) {
                    url = "https://d2eawub7utcl6.cloudfront.net/images/nix-apple-grey.png";
                }

                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }

    }
}
