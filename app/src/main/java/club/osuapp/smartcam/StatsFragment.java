package club.osuapp.smartcam;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import club.osuapp.smartcam.databinding.FragmentStatsBinding;

public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private LineChart chart;
    EditText edText;
    Button bttn;
    TextView textView;

    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();
    String detailSelection = "minutely";

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        super.onCreate(savedInstanceState);

        binding = FragmentStatsBinding.inflate(inflater, container, false);

        binding.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("StatsFragment", "Button 1 pushed");
                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        startDate.set(Calendar.YEAR, year);
                        startDate.set(Calendar.MONTH, month);
                        startDate.set(Calendar.DAY_OF_MONTH, day);

                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);

                        // Create a new instance of TimePickerDialog and return it
                        TimePickerDialog timePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                startDate.set(Calendar.HOUR_OF_DAY, hour);
                                startDate.set(Calendar.MINUTE, minute);

                                RunDataQuery();
                            }
                        }, hour, minute,
                                DateFormat.is24HourFormat(getActivity()));
                        timePicker.show();
                    }
                }, year, month, day);
                datePicker.show();
            }
        });

        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Create a new instance of DatePickerDialog and return it
                DatePickerDialog datePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        endDate.set(Calendar.YEAR, year);
                        endDate.set(Calendar.MONTH, month);
                        endDate.set(Calendar.DAY_OF_MONTH, day);

                        int hour = c.get(Calendar.HOUR_OF_DAY);
                        int minute = c.get(Calendar.MINUTE);

                        // Create a new instance of TimePickerDialog and return it
                        TimePickerDialog timePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                                endDate.set(Calendar.HOUR_OF_DAY, hour);
                                endDate.set(Calendar.MINUTE, minute);

                                RunDataQuery();
                            }
                        }, hour, minute,
                                DateFormat.is24HourFormat(getActivity()));
                        timePicker.show();
                    }
                }, year, month, day);
                datePicker.show();
            }
        });

        Spinner spinner = binding.spinner;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.spinner_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                detailSelection = getResources().getStringArray(R.array.spinner_options)[i];
                RunDataQuery();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                detailSelection = "minutely";
                spinner.setSelection(0);
                RunDataQuery();
            }
        });

        spinner.setSelection(0);

        return binding.getRoot();

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //change startDate default to current time - 1 hour
        //error messages
        //add date as text above buttons


        //endDate = view.findViewById(R.id.end_button);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void RunDataQuery() {
        binding.statsQueryLoading.setVisibility(View.VISIBLE);
        binding.chart.setVisibility(View.INVISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        long startDateEpoch = startDate.getTimeInMillis() / 1000;
        long endDateEpoch = endDate.getTimeInMillis() / 1000;
        String url = "http://159.65.110.8:5000/people?start=" + startDateEpoch + "&end=" + endDateEpoch + "&range=" + detailSelection;

        JsonArrayRequest arrayReq = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray resp) {
                        try {
                            if (resp.length() == 0) {
                                //Show error text
                                return;
                            }

                            ArrayList<Entry> people = new ArrayList<Entry>();

                            for (int i = 0; i < resp.length(); i++) {
                                JSONObject jobj = resp.getJSONObject(i);
                                int timestamp = jobj.getInt("timestamp");
                                int count = jobj.getInt("count");

                                people.add(new Entry(timestamp, count));
                            }

                            LineChart chart = binding.chart;

                            LineDataSet setPeople1 = new LineDataSet(people, "People");

                            LineData data = new LineData(setPeople1);
                            chart.setData(data);
                            chart.invalidate();

                            binding.statsQueryLoading.setVisibility(View.INVISIBLE);
                            binding.chart.setVisibility(View.VISIBLE);
                        } catch (JSONException e) {
                            Log.e("StatsFragment", e.toString());
                            //Show error text
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("StatsFragment", error.toString());
                //Show error text
            }
        }
        );

        queue.add(arrayReq);
    }
}
