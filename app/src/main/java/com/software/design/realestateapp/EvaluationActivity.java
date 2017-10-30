package com.software.design.realestateapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EvaluationActivity extends AppCompatActivity implements VolleyResponce {

    //declare on screen element variables

    EditText address, suburb, plotArea, houseArea, numBath, numBed, numGarage;
    String suburbData, addressData, bedData, bathData, plotAreaData, houseAreaData, garageData;
    boolean poolData;
    int evalAmountTest;

    double suburbPri=-1;
    boolean checked =false;
    String insertUrl = "http://lamp.ms.wits.ac.za/~s1037363/realestate_app/insertHouse2.php", weightsUrl, subUrl = "http://lamp.ms.wits.ac.za/~s1037363/realestate_app/getSuburbPrice.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);

        //assign screen element variables to fields
        address = (EditText) findViewById(R.id.edAddress);

        suburb = (EditText)findViewById(R.id.edSuburb);
        numBath = (EditText)findViewById(R.id.edNumBath);
        numBed = (EditText)findViewById(R.id.edNumBed);
        numGarage = (EditText)findViewById(R.id.edNumGarages);
        plotArea = (EditText)findViewById(R.id.edSizePlot);
        houseArea = (EditText)findViewById(R.id.edSizeHouse);

        //loads weights to be used
        //loadWeights(weightsArr);

        //php url


    }

    /*@Override
    public void handleResponce(Object response, int key) {
        if (key == 1) {

            Toast.makeText(getApplicationContext(), "handled", Toast.LENGTH_LONG).show();
            //Suburb price stuff here
            //KEY IS SO THAT BOTH VOLLEY REQUESTS CAN CALL THE SAME FUNCTION, BUT DO DIFFERENT THINGS. NOT IDEAL, BUT I DONT HAVE TIME TO DO IT PROPERLY
            //CAST IT BEFORE, ITS A BASIC OBJECT SO THAT MANY DIFFERENT THINGS CAN USE IT AS THEY NEED IT

            //(JSONArray) response ........
        }
        if (key == 2) {
            Toast.makeText(getApplicationContext(), "handled", Toast.LENGTH_LONG).show();
        }

    }*/

    public void sendInfo(final String suburbData, final String addressData, final String bedData, final String bathData, final String plotAreaData, final String houseAreaData, final String garageData, final boolean bPoolData, final String evaluationData)
    {


        System.out.println("Suburb is " + suburbData);
        int check=0;
        if(bPoolData)
        {
            check=1;
        }
        int poolData=check;
        Map<String, String> params = new HashMap<String, String>();
        params.put("ADDRESS", addressData);
        params.put("SUBURB", suburbData);
        params.put("PLOT_AREA", plotAreaData);
        params.put("HOUSE_AREA", houseAreaData);
        params.put("BEDROOMS_NUM", bedData);
        params.put("BATHROOMS_NUM", bathData);
        params.put("GARAGES_NUM", garageData);
        params.put("POOL", poolData+"");
        params.put("EVALUATION_AMOUNT", evaluationData);
        int key =2;
        VolleyRequest volleyRequest = new VolleyRequest(insertUrl, params, this, key);
        volleyRequest.makeRequest();
    }

    public void doEvaluation(View view) //gets current value from fields
    {

        doEvaluationTestable(false);
    }

    public void doEvaluationTestable(boolean isTest) {
        suburbData = suburb.getText().toString().trim();
       addressData = address.getText().toString().trim();
        bedData = numBed.getText().toString().trim();
        bathData = numBath.getText().toString().trim();
        plotAreaData = plotArea.getText().toString().trim();
        houseAreaData = houseArea.getText().toString().trim();
       garageData = numGarage.getText().toString().trim();
        poolData = checked;

        fetchSuburbPrice(suburbData);

    }

    public void loadWeights(double [] weightsArray)
    {
        ///weights are suburb, plot, house,bath, bed, garages, pool
        weightsArray[0]=0.7;
        weightsArray[1]=0.3;
        weightsArray[2]=0.3;
        weightsArray[3]=0.1;
        weightsArray[4]=0.1;
        weightsArray[5]=0.1;
        weightsArray[6]=0.05;
    }

    public int evaluate(int e_subPrice, String e_addressData, String e_bedData, String e_bathData, String e_plotAreaData, String e_houseAreaData, String e_garageData, boolean e_poolData, double [] e_weights)
    {
        double total =0;
        int poolPres=0;
        if(e_poolData)
        {
            poolPres=1;
        }
        total=e_subPrice*e_weights[0]+Double.parseDouble(e_plotAreaData)*e_weights[1]+Double.parseDouble(e_houseAreaData)*e_weights[2]+Double.parseDouble(e_bathData)*e_weights[3]+Double.parseDouble(e_bedData)*e_weights[4]+Double.parseDouble(e_garageData)*e_weights[5]+poolPres*e_weights[6];
        double normalized=e_subPrice*e_weights[0]+1000*e_weights[1]+700*e_weights[2]+1*e_weights[3]+1*e_weights[4]+1*e_weights[5]+1*e_weights[6];
        total=total/normalized;
        total=total*e_subPrice+400000;

        System.out.println("Total weight "+total);
        return (int) Math.round(total);
    }

    public void uploadEvaluation(String sPrice)
    {
        double weights[]=new double[10];
        loadWeights(weights);

        int evalAmount = evaluate(Integer.parseInt(sPrice), addressData, bedData, bathData, plotAreaData,houseAreaData,garageData,poolData, weights);
        evalAmountTest = evalAmount;
        sendInfo(suburbData, addressData, bedData, bathData, plotAreaData, houseAreaData, garageData, poolData, (evalAmount + ""));
    }


    public void fetchSuburbPrice(String suburbData)
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("NAME", suburbData);
        int key =1;
        VolleyRequest volleyRequest = new VolleyRequest(subUrl, params, this, key);
        volleyRequest.makeRequest();
    }

    public void doCheck(View view)
    {
        checked = ((CheckBox) view).isChecked();
        System.out.println("Check is clicked and is "+checked);
    }


    @Override
    public void handleResponce(Object response, Map<String, String> map, int key)
    {
        String h_response = (String) response;
        String [] array = h_response.split(" ");
        System.out.println("Array contains :" + array[0]+" and " + array[1]);
        System.out.println("Fetch response is " +h_response);
        System.out.println("Key is :" + key);
        try
        {

            switch (key)
            {
                case 1: //the fetch suburb is called
                    JSONObject jsonObject = new JSONObject( h_response);
                    System.out.println("Entered case 1");
                    JSONArray result= jsonObject.getJSONArray("SUBURB");
                    System.out.println(result);
                    JSONObject suburb = result.getJSONObject(0);
                    String subPrice = suburb.getString("AVG_PRICE");
                    System.out.println(subPrice);
                    Toast.makeText(getApplicationContext(), "handled", Toast.LENGTH_LONG).show();
                    suburbPri=Double.parseDouble(subPrice);
                    uploadEvaluation(subPrice);
                    break;

                case 2:
                    System.out.println("Entered case 2");
                    System.out.println("Fetch response is " +h_response);
                    String houseID = h_response.substring(2,h_response.length());
                    System.out.println("House ID is: "+houseID);
                    Toast.makeText(getApplicationContext(), "handled", Toast.LENGTH_LONG).show();
//
                    displayEvaluation(houseID);
                    break;
            }
        } catch (JSONException e) {
        e.printStackTrace();
    }
    }


    public void displayEvaluation(String houseID)
    {
        Intent startNewActivity = new Intent(this,HouseActivity.class);
        startNewActivity.putExtra("HOUSEID", houseID);
        startActivity(startNewActivity);
    }

    @Override
    public void handleError(Object error, int key) {
        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public Context getContext() {
        return this;
    }
}
