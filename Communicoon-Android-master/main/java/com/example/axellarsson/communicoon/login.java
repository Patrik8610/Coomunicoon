package com.example.axellarsson.communicoon;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    Button btn_login;
    EditText et_username, et_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );

        btn_login = findViewById( R.id.btn_login );
        et_username = findViewById( R.id.et_username );
        et_password = findViewById( R.id.et_password );
        btn_login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_login.setEnabled( false );
                btn_login.setText( "Please wait..." );
                login();
            }
        } );
    }

    public void login(){
        StringRequest request = new StringRequest( Request.Method.POST, "http://communicoon.com/app/get/login.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        int id = Integer.parseInt( response );
                        if (id > 0){
                            SharedPreferences session = getSharedPreferences( "CoonSession", MODE_PRIVATE );
                            SharedPreferences.Editor editor = session.edit();
                            editor.putInt( "ID",id );
                            editor.apply();
                            startActivity( new Intent(getApplicationContext(),MainActivity.class) );
                        }
                        else{
                            btn_login.setEnabled( true );
                            btn_login.setText( "Login" );

                            if (id == -1)
                            {
                                Toast.makeText( getApplicationContext(), "Account not verified", Toast.LENGTH_SHORT ).show();
                            }
                            else if (id == -2)
                            {
                                Toast.makeText( getApplicationContext(),"BANNED",Toast.LENGTH_SHORT ).show();
                            }
                            else
                            {
                                Toast.makeText( getApplicationContext(),"Username or Password is wrong",Toast.LENGTH_SHORT ).show();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>(  );
                params.put( "username", et_username.getText().toString() );
                params.put( "password", et_password.getText().toString() );
                return params;
            }
        };
        Volley.newRequestQueue( this ).add( request );

    }
    public void saveInfo(View view){
        SharedPreferences sharedPreferences = getSharedPreferences( "userInfo", Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString( "username", et_username.getText().toString() );
        editor.putString( "password", et_password.getText().toString() );
        editor.apply();
    }
}
