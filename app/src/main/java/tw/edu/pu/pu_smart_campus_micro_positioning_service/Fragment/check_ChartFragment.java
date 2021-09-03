package tw.edu.pu.pu_smart_campus_micro_positioning_service.Fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.CheckModel;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData;

public class check_ChartFragment extends Fragment {

    private BarChart barChart;

    ArrayList<String> labelNames = new ArrayList<>();

    Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            updateChartData();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check__chart, container, false);
        barChart = view.findViewById(R.id.barChart);

        initBarChart();
        timer.schedule(timerTask, 10250, 10250);

        return view;
    }

    private void initBarChart() {
        BarDataSet barDataSet = new BarDataSet(getEntry(), "景點");
        barDataSet.setColor(ContextCompat.getColor(requireActivity(), R.color.lightBlue));

        BarData data = new BarData(barDataSet);
        data.setValueTextSize(12f);
        barChart.setData(data);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelNames));
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelNames.size());
        xAxis.setLabelRotationAngle(270);

        barChart.getData().notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private ArrayList<BarEntry> getEntry() {
        ArrayList<BarEntry> values = new ArrayList<>();
        ArrayList<Integer> valuePeople = new ArrayList<>();
        ArrayList<CheckModel> checkData = new ArrayList<>();

        ShareData shareData = new ShareData(getContext(), checkData);

        checkData = shareData.loadData();

        if (checkData != null) {
            for (CheckModel data : checkData) {
                labelNames.add(data.getTitleNames());
                valuePeople.add(Integer.valueOf(data.getNumPeople()));
            }
        }

        Log.e("yuuzu valuePeople: ", valuePeople.toString());

        for (int i = 0; i < valuePeople.size(); i++) {
            values.add(new BarEntry(i, valuePeople.get(i)));
        }

        return values;
    }

    private void updateChartData() {
        ArrayList<BarEntry> entries = new ArrayList<>(getEntry());

        BarDataSet set = (BarDataSet) barChart.getData().getDataSetByIndex(0);
        set.setValues(entries);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labelNames));
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labelNames.size());
        xAxis.setLabelRotationAngle(270);

        barChart.getData().notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}