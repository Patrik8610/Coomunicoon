package com.example.axellarsson.communicoon;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static android.app.Activity.RESULT_OK;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class report extends Fragment implements AdapterView.OnItemSelectedListener {

    public EditText description;
    public String classification;
    private String text;
    ImageView imageViewCamera;
    Button btnTakePic;
    String pathToFile;
    Button btnSubmit;
    Button btnPosition;

    private Bitmap bitmap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate( R.layout.report, container, false );

        btnSubmit = (Button) view.findViewById( R.id.btnSubmit );
        btnSubmit.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSubmit.setEnabled( false );
                btnSubmit.setText( "Please wait..." );
                submitReport();
            }
        } );
        description = (EditText) view.findViewById( R.id.description );
        btnTakePic = (Button) view.findViewById( R.id.btnTakePic );
        imageViewCamera = (ImageView)view.findViewById( R.id.imageViewCamera );
        Spinner spinner1 = (Spinner) view.findViewById( R.id.spinner1 );
        btnPosition = (Button) view.findViewById( R.id.btnPosition );
        btnPosition.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMaps();
            }
        } );

        if (Build.VERSION.SDK_INT>29)
        {
            requestPermissions( new String[] {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE},2 );
        }

        btnTakePic.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchPictureTakerAction();
            }
        } );
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource( getActivity(), R.array.Issues, android.R.layout.simple_spinner_item );
        adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter( adapter );
        spinner1.setOnItemSelectedListener (this);

        return view;
    }
    public void openMaps(){
        Intent intent = new Intent( getActivity(), MapsActivity.class );
        startActivity( intent );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult( requestCode, resultCode, data );

        if (resultCode == RESULT_OK)
        {
            if (requestCode == 1)
            {
                bitmap = BitmapFactory.decodeFile( pathToFile );
                imageViewCamera.setImageBitmap( bitmap );
            }
        }
    }
    private void dispatchPictureTakerAction() {
        Intent takePic = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );

        if (takePic.resolveActivity( getActivity().getPackageManager()) != null ) {
            File photoFile = null;
                photoFile = createPhotoFile();
                if (photoFile!=null) {
                    pathToFile = photoFile.getAbsolutePath();
                    Uri photoURI = FileProvider.getUriForFile( getActivity(), "com.example.axellarsson.communicoon.fileprovider", photoFile );
                    takePic.putExtra( MediaStore.EXTRA_OUTPUT, photoURI );
                    startActivityForResult( takePic, 1 );
                }
        }
    }
    private File createPhotoFile() {
        String name = new SimpleDateFormat( "yyyyMMdd_HHmmss" ).format( new Date(  ) );
        File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES );
        File image = null;
        try {
            image = File.createTempFile( name, ".jpg", storageDir );
        }
        catch (IOException e) {
            Log.d("mylog", "Excep : " + e.toString());
        }
        return image;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        classification = parent.getItemAtPosition( position ).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
    public void submitReport(){
        StringRequest request = new StringRequest( Request.Method.POST, "http://communicoon.com/app/post/contribution.php",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        btnSubmit.setEnabled( true );
                        btnSubmit.setText( "Submit" );

                        if (response.equals( "success" ))
                        {
                            description.setText( "" );
                            bitmap = null;
                            imageViewCamera.setImageResource( R.drawable.no_image );
                            Toast.makeText(getActivity(),"Report Submitted", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            Toast.makeText(getActivity(),response, Toast.LENGTH_SHORT).show();
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
                String encodedImage;

                SharedPreferences session = getActivity().getSharedPreferences( "CoonSession", Context.MODE_PRIVATE );
                Map<String,String> params = new HashMap<>(  );
                params.put( "coonId", session.getInt( "ID", 0 ) + "");
                params.put( "classification", classification);
                params.put( "description", description.getText().toString());

                SharedPreferences position = getActivity().getSharedPreferences( "CoonPosition", Context.MODE_PRIVATE );

                params.put( "address", position.getString( "address", "" ));
                params.put( "lat", position.getString( "lat", "" ));
                params.put( "lng", position.getString( "lng", "" ));

                try {
                    bitmap = resizeImage(bitmap);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(  );
                    bitmap.compress( Bitmap.CompressFormat.JPEG, 75,outputStream );
                    byte[] imageBytes = outputStream.toByteArray();
                    encodedImage = Base64.encodeToString( imageBytes,Base64.DEFAULT );
                }
                catch (Exception e){
                    encodedImage = "";
                }

                params.put( "image", encodedImage );

                return params;
            }
        };
        Volley.newRequestQueue( getActivity() ).add( request );
    }

    public Bitmap resizeImage(Bitmap bit){
        float aspectRatio = bit.getWidth() / (float) bit.getHeight();

        int width = 480;

        int height = Math.round( width / aspectRatio );

        return Bitmap.createScaledBitmap( bit, width, height, false );
    }
}

