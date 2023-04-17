package com.aurux.rockstat.utils;


import android.content.Context;
import android.content.res.Configuration;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.aurux.rockstat.R;
import com.aurux.rockstat.data.models.ClimbLogEntry;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ChartUtils {

    private static final long MILLIS_IN_ONE_WEEK = 7 * 24 * 60 * 60 * 1000;

    public static List<Integer> getClimbsPerWeek(List<ClimbLogEntry> climbs) {
        if (climbs == null || climbs.isEmpty()) {
            return Collections.emptyList();
        }

        long currentTime = System.currentTimeMillis();
        long earliestWeekTimestamp = currentTime - (MILLIS_IN_ONE_WEEK * 4);

        List<Integer> climbsPerWeek = new ArrayList<>(Collections.nCopies(4, 0));

        for (ClimbLogEntry climb : climbs) {
            long climbTimestamp = climb.getTimestamp();

            if (climbTimestamp >= earliestWeekTimestamp) {
                int weekIndex = (int) ((currentTime - climbTimestamp) / MILLIS_IN_ONE_WEEK);
                if (weekIndex < 4) {
                    climbsPerWeek.set(weekIndex, climbsPerWeek.get(weekIndex) + 1);
                }
            }
        }

        Collections.reverse(climbsPerWeek);
        return climbsPerWeek;
    }


    public static void updateClimbsDonePerWeekChart(Context context, BarChart chart,List<ClimbLogEntry> climbs) {
        List<Integer> climbsPerWeek = getClimbsPerWeek(climbs);
        Log.d("ChartData", "Climbs: " + climbsPerWeek);

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < climbsPerWeek.size(); i++) {
            entries.add(new BarEntry(i, climbsPerWeek.get(i)));
        }

        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        int textColor = ContextCompat.getColor(context, isNightMode ? R.color.white : R.color.black);

        BarDataSet dataSet = new BarDataSet(entries, "Climbs Done per Week");
        dataSet.setColor(ContextCompat.getColor(context, R.color.purple_200));
        BarData data = new BarData(dataSet);

        data.setValueTextSize(12f);
        data.setValueTextColor(textColor);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        chart.setData(data);
        chart.setTouchEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(textColor);
        xAxis.setLabelCount(climbsPerWeek.size());
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat mFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
            @Override
            public String getFormattedValue(float value) {
                long currentTime = System.currentTimeMillis();
                long earliestWeekTimestamp = currentTime - (MILLIS_IN_ONE_WEEK * 4);
                long weekTimestamp = earliestWeekTimestamp + (long) value * MILLIS_IN_ONE_WEEK;
                return mFormat.format(new Date(weekTimestamp));
            }
        });

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setTextColor(textColor);
        yAxisLeft.setEnabled(false);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);

        yAxisLeft.setGridColor(textColor);
        yAxisLeft.setGridLineWidth(0.5f);

        chart.invalidate();
    }

    private static Map<String, Float> getAvgAttemptsPerGrade(List<ClimbLogEntry> climbs) {
        if (climbs == null || climbs.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, List<Integer>> gradeAttemptsMap = new HashMap<>();
        for (ClimbLogEntry climb : climbs) {
            String grade = climb.getGrade();
            int attempts = climb.getAttempts();

            if (!gradeAttemptsMap.containsKey(grade)) {
                gradeAttemptsMap.put(grade, new ArrayList<>());
            }

            gradeAttemptsMap.get(grade).add(attempts);
        }

        Map<String, Float> avgAttemptsPerGrade = new HashMap<>();
        for (Map.Entry<String, List<Integer>> entry : gradeAttemptsMap.entrySet()) {
            String grade = entry.getKey();
            List<Integer> attemptsList = entry.getValue();

            float sum = 0;
            for (int attempts : attemptsList) {
                sum += attempts;
            }
            float average = sum / attemptsList.size();
            avgAttemptsPerGrade.put(grade, average);
        }

        return avgAttemptsPerGrade;
    }


    public static void updateAvgAttemptsPerGradeChart(Context context, BarChart chart, List<ClimbLogEntry> climbs, String climbType) {

        Map<String, Float> avgAttemptsPerGrade = getAvgAttemptsPerGrade(climbs);
        Log.d("ChartData", "AvgAttemptsPerGrade: " + avgAttemptsPerGrade);

        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNightMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        int textColor = ContextCompat.getColor(context, isNightMode ? R.color.white : R.color.black);






        List<BarEntry> entries = new ArrayList<>();
        List<String> grades = new ArrayList<>(avgAttemptsPerGrade.keySet());

        String[] vGrades = {"VB", "V0", "V1", "V2", "V3", "V4", "V5", "V6", "V7", "V8", "V9", "V10", "V11", "V12", "V13", "V14", "V15", "V16", "V17"};
        String[] fGrades = {"1", "2", "3", "4a", "4b", "4c", "5a", "5b", "5c", "6a", "6b", "6c", "7a", "7b", "7c", "8a", "8b", "8c", "9a", "9b", "9c"};
        String[] tradGrades = {"Mod", "Diff", "VDiff", "HVD", "Sev", "HS", "VS", "HVS", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "E10", "E11"};

        switch (climbType) {
            case "Bouldering":
                Collections.sort(grades, Comparator.comparingInt(g -> Arrays.asList(vGrades).indexOf(g)));
                break;

            case "Sport":
                Collections.sort(grades, Comparator.comparingInt(g -> Arrays.asList(fGrades).indexOf(g)));
                break;

            case "Trad":
                Collections.sort(grades, Comparator.comparingInt(g -> Arrays.asList(tradGrades).indexOf(g)));
                break;

        }
        // Remove duplicates
        Set<String> uniqueGrades = new LinkedHashSet<>(grades);
        List<String> gradesWithoutDuplicates = grades.stream().distinct().collect(Collectors.toList());


        List<String> gradeLabels = new ArrayList<>(uniqueGrades);

        for (int i = 0; i < gradesWithoutDuplicates.size(); i++) {
            String grade = gradesWithoutDuplicates.get(i);
            entries.add(new BarEntry(i, avgAttemptsPerGrade.get(grade)));
        }


        BarDataSet dataSet = new BarDataSet(entries, "Average Attempts per Grade");
        dataSet.setColor(ContextCompat.getColor(context, R.color.purple_200));
        BarData data = new BarData(dataSet);

        data.setValueTextSize(12f);
        data.setValueTextColor(textColor);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        chart.setData(data);
        chart.setTouchEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(gradeLabels));
        xAxis.setCenterAxisLabels(false);
        xAxis.setTextColor(textColor);
        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setAxisMinimum(0);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setTextColor(textColor);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setEnabled(false);
        yAxisLeft.setEnabled(false);

        xAxis.setGranularity(1f);
        xAxis.setXOffset(0.5f);

        yAxisLeft.setGridColor(textColor);
        yAxisLeft.setGridLineWidth(0.5f);
        xAxis.setLabelCount(gradeLabels.size());





        chart.invalidate();
    }
}

