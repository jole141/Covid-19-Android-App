package com.jole141.covidhrvatska;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Graph extends AppCompatActivity  {

    GraphView graphView;
    GraphView graphView2;

    private String search =new MainActivity().getInfo();
    private RequestQueue mQueue;
    private Button draw;
    public BarGraphSeries<DataPoint> series2;
    public LineGraphSeries<DataPoint> series;
    private TextView st;

    int[] dpC = new int[30];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_graph);

        mQueue = Volley.newRequestQueue(this);
        st = findViewById(R.id.textView6);
        graphView = findViewById(R.id.graphView);
        graphView2 = findViewById(R.id.graphView2);
        draw = findViewById(R.id.button2);


        //Getting data from an api
        jsonParse();

        //Drawing graph (last 30 days)
        graphView.setTitle("Number of infected (last 30 days)");
        graphView.setTitleColor(Color.parseColor("#a3a3a3"));
        graphView.setTitleTextSize(40);
        graphView.getGridLabelRenderer().setGridColor(Color.parseColor("#a3a3a3"));
        graphView.getGridLabelRenderer().setVerticalLabelsColor(Color.parseColor("#a3a3a3"));
        graphView.getGridLabelRenderer().setHorizontalLabelsColor(Color.parseColor("#a3a3a3"));


        //Drawing graph (last 10 days)
        graphView2.setTitle("Number of infected (last 10 days)");
        graphView2.setTitleColor(Color.parseColor("#a3a3a3"));
        graphView2.setTitleTextSize(40);
        graphView2.getGridLabelRenderer().setGridColor(Color.parseColor("#a3a3a3"));
        graphView2.getGridLabelRenderer().setHorizontalLabelsColor(Color.parseColor("#a3a3a3"));
        graphView2.getGridLabelRenderer().setVerticalLabelsColor(Color.parseColor("#a3a3a3"));


        //Button "DRAW"
        draw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Clearing data from graph
                graphView.removeAllSeries();
                graphView2.removeAllSeries();

                //Setting colours of graphs
                series = new LineGraphSeries<>(getDataPoint30());
                series.setColor(Color.parseColor("#4fff9e"));
                series.setAnimated(true);
                series.setDrawBackground(true);
                series.setBackgroundColor(Color.parseColor("#49524d"));

                series2 = new BarGraphSeries<>(getDataPoint10());
                series2.setColor(Color.parseColor("#ff4f4f"));
                series2.setSpacing(15);
                series2.setAnimated(true);


                //Adding data
                graphView.addSeries(series);
                graphView.getViewport().setXAxisBoundsManual(true);
                graphView.getViewport().setMinX(0);
                graphView.getViewport().setMaxX(31);

                graphView2.addSeries(series2);
                graphView2.getViewport().setXAxisBoundsManual(true);
                graphView2.getViewport().setMinY(0);
                graphView2.getViewport().setMinX(0);
                graphView2.getViewport().setMaxX(11);

            }
        });
    }

    private void jsonParse() {

        String url = "https://pomber.github.io/covid19/timeseries.json";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray(search);
                            //Creating list if new cases in last 30 days
                            for (int i=1; i<31; i++) {
                                JSONObject day = jsonArray.getJSONObject(jsonArray.length()-i);
                                JSONObject day1 = jsonArray.getJSONObject(jsonArray.length()-i-1);
                                int today = Integer.parseInt(day.getString("confirmed"));
                                int before =Integer.parseInt(day1.getString("confirmed"));
                                int y = today - before;
                                dpC[30-i] = y;
                            }
                            st.setText(search);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }


    //This method gives you list of new cases per day in last 30 and 10 days
    private DataPoint[] getDataPoint30() {
        DataPoint[] dp = new DataPoint[30];
        for (int i = 0; i < 30; i++) {
            dp[i] = new DataPoint(i + 1, dpC[i]);
        }
        return dp;
    }

    private DataPoint[] getDataPoint10() {
        DataPoint[] dp = new DataPoint[10];
        for (int i = 0; i < 10; i++) {
            dp[i] = new DataPoint(i + 1, dpC[20+i]);
        }
        return dp;
    }


}
