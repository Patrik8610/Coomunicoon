package com.example.axellarsson.communicoon;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;

public class customListView extends ArrayAdapter<String>{

    private String[] name;
    private String[] image;
    private Activity context;
    Bitmap bitmap;

    public customListView(Activity context, String[] name, String[] image) {
        super(context, R.layout.archive, name);
        this.context = context;
        this.name = name;
        this.image = image;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View r=convertView;
        ViewHolder viewHolder=null;

        if (r==null)
        {
            LayoutInflater layoutInflater=context.getLayoutInflater();
            r=layoutInflater.inflate(R.layout.archive, null, true);
            viewHolder = new ViewHolder(r);
            r.setTag(viewHolder);
        }
        else
        {
         viewHolder = (ViewHolder)r.getTag();
        }
        viewHolder.tvw1.setText(name[position]);

        new GetImageFromURL(viewHolder.ivw).execute(image[position]);

        return r;
    }

    class ViewHolder{
        TextView tvw1;
        ImageView ivw;

        ViewHolder(View v){
            tvw1=(TextView)v.findViewById(R.id.Issue);
            ivw=(ImageView)v.findViewById(R.id.imageView);
        }
    }
    public class GetImageFromURL extends AsyncTask<String, Void, Bitmap>
    {

        ImageView imgView;
        public GetImageFromURL(ImageView imgv)
        {
            this.imgView=imgv;
        }
        @Override
        protected Bitmap doInBackground(String... url) {
            String urldisplay=url[0];
            bitmap = null;

            try
            {
                InputStream ist = new java.net.URL(urldisplay).openStream();
                bitmap = BitmapFactory.decodeStream(ist);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
            return bitmap;
        }
        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            super.onPostExecute(bitmap);
            imgView.setImageBitmap(bitmap);
        }
    }
}
