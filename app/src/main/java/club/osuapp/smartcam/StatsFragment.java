package club.osuapp.smartcam;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import club.osuapp.smartcam.databinding.FragmentStatsBinding;

public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private LineChart chart;


    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        binding = FragmentStatsBinding.inflate(inflater, container, false);

        LineChart chart = (LineChart) container.findViewById(R.id.chart);

        ArrayList<Entry> people = new ArrayList<Entry>();
        people.add(new Entry(0,10));
        people.add(new Entry(1,30));
        people.add(new Entry(2,20));
        people.add(new Entry(4,15));

        LineDataSet setPeople1 = new LineDataSet(people, "People");

        LineData data = new LineData(setPeople1);
        chart.setData(data); //keeps crashing when this is run
        chart.invalidate(); //and this one too


        return binding.getRoot();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;


    }
}