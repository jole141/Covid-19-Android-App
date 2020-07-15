package com.jole141.covidhrvatska;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    private SeekBar days;
    private Button button;
    private ImageButton toGraph;
    private TextView slucaj;
    private TextView izlijec;
    private TextView preminuli;
    private TextView sv1;
    private TextView sv2;
    private TextView sv3;
    private TextView datumAz;
    private RequestQueue mQueue;
    private String[] namedate = {"January", "February", "March", "April","May", "June", "July","August","September", "October","November","December"};
    String[] countries ={"Afghanistan","Albania","Algeria","Andorra","Angola","Antigua and Barbuda","Argentina","Armenia", "Australia","Austria","Azerbaijan","Bahamas","Bahrain","Bangladesh","Barbados","Belarus","Belgium", "Benin","Bhutan","Bolivia","Bosnia and Herzegovina","Brazil","Brunei","Bulgaria","Burkina Faso","Cabo Verde", "Cambodia","Cameroon","Canada","Central African Republic","Chad","Chile","China","Colombia","Congo (Brazzaville)", "Congo (Kinshasa)","Costa Rica","Cote d'Ivoire","Croatia","Diamond Princess","Cuba","Cyprus","Czechia","Denmark", "Djibouti","Dominican Republic","Ecuador","Egypt","El Salvador","Equatorial Guinea","Eritrea","Estonia","Eswatini", "Ethiopia","Fiji","Finland","France","Gabon","Gambia","Georgia","Germany","Ghana","Greece","Guatemala","Guinea","Guyana", "Haiti","Holy See","Honduras","Hungary","Iceland","India","Indonesia","Iran","Iraq","Ireland","Israel","Italy","Jamaica","Japan","Jordan","Kazakhstan","Kenya","Korea","South","Kuwait","Kyrgyzstan","Latvia","Lebanon","Liberia","Liechtenstein","Lithuania","Luxembourg","Madagascar","Malaysia","Maldives","Malta","Mauritania","Mauritius","Mexico","Moldova","Monaco","Mongolia","Montenegro","Morocco","Namibia","Nepal","Netherlands","New Zealand","Nicaragua","Niger","Nigeria","North Macedonia","Norway","Oman","Pakistan","Panama","Papua New Guinea","Paraguay","Peru","Philippines","Poland","Portugal","Qatar","Romania","Russia","Rwanda","Saint Lucia","Saint Vincent and the Grenadines","San Marino","Saudi Arabia","Senegal","Serbia","Seychelles","Singapore","Slovakia","Slovenia","Somalia","South Africa","Spain","Sri Lanka","Sudan","Suriname","Sweden","Switzerland","Taiwan*","Tanzania","Thailand","Togo","Trinidad and Tobago","Tunisia","Turkey","Uganda","Ukraine","United Arab Emirates","United Kingdom","Uruguay","US","Uzbekistan","Venezuela","Vietnam","Zambia","Zimbabwe","Dominica","Grenada","Mozambique","Syria","Timor-Leste","Belize","Laos","Libya","West Bank and Gaza","Guinea-Bissau","Mali","Saint Kitts and Nevis","Kosovo","Burma","MS Zaandam","Botswana","Burundi","Sierra Leone","Malawi"};


    public static String getInfo() {
        return info;
    }

    public static String info = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner2);
        button = findViewById(R.id.button);
        toGraph = findViewById(R.id.toGraph);
        slucaj = findViewById(R.id.zarazeni);
        izlijec = findViewById(R.id.izlj);
        preminuli = findViewById(R.id.prem);
        sv1 = findViewById(R.id.sv1);
        sv2 = findViewById(R.id.sv2);
        sv3 = findViewById(R.id.sv3);
        days = findViewById(R.id.seekBar);
        datumAz = findViewById(R.id.datum);
        mQueue = Volley.newRequestQueue(this);


        //Spinner of countries
        ArrayAdapter spinnerArrayAdapter = new ArrayAdapter(this,
                android.R.layout.browser_link_context_header,
                countries);
        spinner.setAdapter(spinnerArrayAdapter);

        jsonParse();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse();
            }
        });

        toGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                info = spinner.getSelectedItem().toString();
                seeGraph();
            }
        });


    }

    public void seeGraph() {
        jsonParse();
        Intent intent = new Intent(this, Graph.class);
        startActivity(intent);
    }


    private void jsonParse() {

        String url = "https://pomber.github.io/covid19/timeseries.json";

        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            //Getting data of selected country
                            JSONArray jsonArray = response.getJSONArray(spinner.getSelectedItem().toString());

                            //Getting last updated data
                            JSONObject datas = jsonArray.getJSONObject(jsonArray.length()-1 - (10- days.getProgress()));

                            //Getting data and setting text in app
                            int confirmedRH = datas.getInt("confirmed");
                            int deathsRH = datas.getInt("deaths");
                            int recoveredRH = datas.getInt("recovered");
                            slucaj.setText(convertNumber(confirmedRH));
                            izlijec.setText(convertNumber(recoveredRH));
                            preminuli.setText(convertNumber(deathsRH));

                            //Converting date e.g. :  11-11-2020 -> 11 November 2020
                            String[] date = datas.getString("date").split("-");
                            datumAz.setText("Updated on  " + date[2] + " " + namedate[Integer.parseInt(date[1])-1] + " " + date[0] + "");


                            //Difference between current day and previous day (number of new cases per day)
                            JSONObject data = jsonArray.getJSONObject(jsonArray.length()-2 - (10- days.getProgress()));
                            int C = data.getInt("confirmed");
                            int D = data.getInt("deaths");
                            int R = data.getInt("recovered");

                            int difC = confirmedRH-C;
                            int difR = recoveredRH-R;
                            int difD = deathsRH-D;
                            sv1.setText(spinner.getSelectedItem().toString() + changes(difC));
                            sv2.setText(spinner.getSelectedItem().toString() + changes(difR));
                            sv3.setText(spinner.getSelectedItem().toString() + changes(difD));

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

    //Converts number e.g.: 1234 -> 1,234
    private String convertNumber(long number) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] convert = new String[Long.toString(number).length()];
        for(int i = 0; i < convert.length; i++) {
            convert[i] = String.valueOf(number%10);
            number /= 10;
        }
        for(int i = 0; i < convert.length; i++) {
            stringBuilder.append(convert[i]);
            if(i%3 == 2 && i != convert.length-1) stringBuilder.append(",");
        }
        return stringBuilder.reverse().toString();
    }

    //If county doesn't have any new cases returns no changes, else returns number of new cases
    private String changes(long number) {
        String result;
        if (number == 0) {
            result = " no changes";
        }else {
           result = " " + convertNumber(number) + " new";
        }
        return result;
    }



}
