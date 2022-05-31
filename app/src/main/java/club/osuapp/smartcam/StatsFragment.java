package club.osuapp.smartcam;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

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
    DatePickerDialog datePicker;
    EditText edText;
    Button bttn;
    TextView textView;



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

        //Add date/time selectors to the fragment_stats.xml layout file
        //Give those defaults (end=current time, start=1 hour ago)
        //Whenever those are changed, update the URL and rerun the request
        //Add a drop down to select between minutes, hours, days, months, years
        //When that's changed, update the URL and rerun the request


        //endDate = view.findViewById(R.id.end_button);

        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://159.65.110.8:5000/people?start=123&end=456&range=hourly";

        JsonArrayRequest arrayReq = new JsonArrayRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray resp) {
                    try {
                        ArrayList<Entry> people = new ArrayList<Entry>();

                        for (int i = 0; i < resp.length(); i++) {
                            JSONObject jobj = resp.getJSONObject(i);
                            int timestamp = jobj.getInt("timestamp");
                            int count = jobj.getInt("count");

                            people.add(new Entry(timestamp, count));

                            //Take these and make a new array out of them, then put it in the chart data
                        }

                        LineChart chart = (LineChart) view.findViewById(R.id.chart);

                        LineDataSet setPeople1 = new LineDataSet(people, "People");

                        LineData data = new LineData(setPeople1);
                        chart.setData(data);
                        chart.invalidate();
                    } catch (JSONException e) {
                        Log.e("StatsFragment", e.toString());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("StatsFragment", error.toString());
                }
            }
        );

        queue.add(arrayReq);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


}

