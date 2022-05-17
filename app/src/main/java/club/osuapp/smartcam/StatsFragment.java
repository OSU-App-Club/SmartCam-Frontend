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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "https://osuapp.club/smartcam/people";

        JsonArrayRequest arrayReq = new JsonArrayRequest(Request.Method.GET, url, null,
                (Response.Listener) response -> {
                    //Turn off loading spinner, enable graph, load graph with data
                }, (Response.ErrorListener) error -> {
            //Turn off loading spinner, enable textview, set it to show error message
        });

        queue.add(arrayReq);

        try {
            JSONArray resp = new JSONArray("[\n" +
                    "    {\n" +
                    "        \"timestamp\": 1652136419,\n" +
                    "        \"count\": 3\n" +
                    "    },\n" +
                    "    {\n" +
                    "        \"timestamp\": 1652136465,\n" +
                    "        \"count\": 5\n" +
                    "    }\n" +
                    "]");

            for (int i = 0; i < resp.length(); i++) {
                JSONObject jobj = resp.getJSONObject(i);
                int timestamp = jobj.getInt("timestamp");
                int count = jobj.getInt("count");

                //Take these and make a new array out of them, then put it in the chart data
            }

            LineChart chart = (LineChart) view.findViewById(R.id.chart);

            ArrayList<Entry> people = new ArrayList<Entry>();
            people.add(new Entry(0,10));
            people.add(new Entry(1,30));
            people.add(new Entry(2,20));
            people.add(new Entry(3,15));
            people.add(new Entry(4,1));
            people.add(new Entry(5,38));
            people.add(new Entry(6,27));
            people.add(new Entry(7,52));
            people.add(new Entry(8,18));
            people.add(new Entry(9,13));
            people.add(new Entry(10,99));
            people.add(new Entry(11,35));

            LineDataSet setPeople1 = new LineDataSet(people, "People");

            LineData data = new LineData(setPeople1);
            chart.setData(data);
            chart.invalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

