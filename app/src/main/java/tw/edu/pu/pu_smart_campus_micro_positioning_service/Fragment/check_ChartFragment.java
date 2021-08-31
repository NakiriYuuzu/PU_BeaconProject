package tw.edu.pu.pu_smart_campus_micro_positioning_service.Fragment;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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

import tw.edu.pu.pu_smart_campus_micro_positioning_service.R;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.CheckModel;
import tw.edu.pu.pu_smart_campus_micro_positioning_service.VariableAndFunction.ShareData;

public class check_ChartFragment extends Fragment {

    View view;
    ShareData shareData;
    BarChart barChart;
    BarData data;

    ArrayList<BarEntry> entries = new ArrayList<>();
    ArrayList<String> labelNames = new ArrayList<>();
    ArrayList<Integer> valuePeople = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_check__chart, container, false);
        barChart = view.findViewById(R.id.barChart);

        importData();
        initBarChart();

        return view;
    }

    private void initBarChart() {
        BarDataSet barDataSet = new BarDataSet(entries, "景點");
        barDataSet.setColor(ContextCompat.getColor(requireActivity(), R.color.lightBlue));

        Description description = new Description();
        description.setText("人流");

        barChart.setDescription(description);

        data = new BarData(barDataSet);
        barChart.setData(data);

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
        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void importData() {
        ArrayList<CheckModel> checkData = new ArrayList<>();
        shareData = new ShareData(getContext(), checkData);

        checkData = shareData.loadData();

        if (checkData != null) {
            for (CheckModel data : checkData) {
                labelNames.add(data.getTitleNames());
                valuePeople.add(Integer.valueOf(data.getNumPeople()));
            }
        }

        for (int i = 0; i < valuePeople.size(); i++) {
            entries.add(new BarEntry(i, valuePeople.get(i)));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}