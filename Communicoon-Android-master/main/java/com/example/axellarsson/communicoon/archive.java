package com.example.axellarsson.communicoon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class archive extends Fragment {

    public ListView reportList;

    public static List status = new ArrayList<String>();
    public static List classification = new ArrayList<String>();
    public static List image = new ArrayList<String>();
    public static List disclosed = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.archive, container, false);



        reportList = (ListView) view.findViewById( R.id.report_list );

        getReports();

        return view;
    }




    public void getReports(){
        StringRequest request = new StringRequest( Request.Method.POST, "http://communicoon.com/app/get/contributions.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray reports = new JSONArray( response );
                            for (int i=0; i<reports.length();i++)
                            {
                                JSONObject r = reports.getJSONObject( i );
                                status.add(r.getString( "status" ));
                                classification.add(r.getString( "classification" ));
                                image.add(r.getString( "image" ));
                                disclosed.add(r.getString( "disclosed" ));
                            }


                            ReportAdapter reportAdapter = new ReportAdapter();
                            reportList.setAdapter( reportAdapter );

                            //TODO: add a click listener for the itens in the list

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                SharedPreferences session = getActivity().getSharedPreferences( "CoonSession", Context.MODE_PRIVATE );
                Map<String,String> params = new HashMap<>(  );
                params.put( "coonId", session.getInt( "ID", 0 ) + "");
                return params;
            }
        };
        Volley.newRequestQueue( getActivity() ).add( request );

    }
    class ReportAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return status.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            view = getLayoutInflater().inflate( R.layout.report_item, null);

            TextView statusView = (TextView) view.findViewById( R.id.status );
            TextView classificationView = (TextView) view.findViewById( R.id.classification );
            ImageView imageView = (ImageView) view.findViewById( R.id.image );
            TextView disclosedView = (TextView) view.findViewById( R.id.disclosed );

            String status_ = status.get( position ).toString();
            String image_ = image.get( position ).toString();
            String classification_ = classification.get( position ).toString();
            String disclosed_ = disclosed.get( position ).toString();


            if(status_ == "null"){
                statusView.setText( "Pending" );
                statusView.setTextColor( Color.rgb( 79, 93, 115 ));
            }
            else{
                if(status_.equals("In progress")){
                    statusView.setTextColor( Color.rgb( 255, 209, 102 ) );
                }
                else {
                    statusView.setTextColor( Color.rgb( 118, 194, 175 ) );
                }

                statusView.setText( status_ );
            }

            classificationView.setText( classification_ );
            if(image_ != "null"){
                Picasso.get().load("http://communicoon.com/uploads/" + image.get( position ).toString()).into( imageView );
            }
            disclosedView.setText( disclosed_ );

            return view;
        }
    }
}


