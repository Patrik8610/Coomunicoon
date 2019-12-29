package com.example.axellarsson.communicoon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

import java.util.HashMap;
import java.util.Map;

public class account extends Fragment {

    public TextView username;
    public TextView sumReports;
    public TextView sumScore;
    public TextView profileMailName;
    public TextView profileAccPhone;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.account, container, false);

        username = (TextView) view.findViewById( R.id.username);
        sumReports = (TextView) view.findViewById( R.id.sumReports );
        sumScore = (TextView) view.findViewById( R.id.sumScore );
        profileMailName = (TextView) view.findViewById( R.id.profileMailName );
        profileAccPhone = (TextView) view.findViewById( R.id.profileAccPhone );

        getData();

        return view;
    }
    public void getData(){
        StringRequest request = new StringRequest( Request.Method.POST, "http://communicoon.com/app/get/coon.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject c = new JSONObject( response );
                            username.setText( c.getString( "username" ) );
                            sumReports.setText( c.getString( "contributions" ) );
                            sumScore.setText( c.getString( "score" ) );
                            profileMailName.setText( c.getString( "email" ) );
                            String phone = c.getString( "phone" );
                            if (phone == "null")
                            {
                             phone = "";
                            }
                            profileAccPhone.setText( phone );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(),"Error", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                SharedPreferences session = getActivity().getSharedPreferences( "CoonSession", Context.MODE_PRIVATE );
                Map<String,String> params = new HashMap<>(  );
                params.put( "coonId", session.getInt( "ID", 0 ) + "");
                params.put( "coonId", session.getInt( "ID", 0 ) + "");
                params.put( "username", username.getText().toString());
                params.put( "sumReports", sumReports.getText().toString());
                params.put( "sumScore", sumScore.getText().toString());
                params.put( "profileMailName", profileMailName.getText().toString());
                params.put( "profileAccPhone", profileAccPhone.getText().toString());
                return params;
            }
        };
        Volley.newRequestQueue( getActivity() ).add( request );
    }
}
