package com.example.q.myapplication;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Button;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.GridView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.graphics.Color;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.animation.ObjectAnimator;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.graphics.Rect;
import android.graphics.Point;
import android.content.res.ColorStateList;
import android.support.design.widget.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.app.Activity.RESULT_CANCELED;

public class GalleryActivity extends Fragment {
    FloatingActionButton plusButton;
    static ListView z;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gallery_main,container,false);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        z = getView().findViewById(R.id.friends);
        postHttp php = new postHttp();
        plusButton = getView().findViewById(R.id.plusbutton);
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("id", MainActivity.currentID);
            php.execute("https://mymy.koreacentral.cloudapp.azure.com/api/myfriends",jsonParam);
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                AlertDialog.Builder ad = new AlertDialog.Builder(getContext());
                ad.setTitle("친구추가");
                ad.setMessage("친구의 아이디를 입력하세요");
                final EditText et = new EditText(getContext());
                ad.setView(et);
                ad.setPositiveButton("요청", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Text 값 받아서 로그 남기기
                        String value = et.getText().toString();
                        postHttp php = new postHttp();
                        try {
                            JSONObject jsonParam = new JSONObject();
                            jsonParam.put("fromid",MainActivity.currentID);
                            jsonParam.put("toid",value);
                            php.execute("https://mymy.koreacentral.cloudapp.azure.com/api/frireq",jsonParam);
                        }catch(JSONException e)
                        {
                            e.printStackTrace();
                        }
                            dialog.dismiss();     //닫기
                            // Event
                    }
                });
                ad.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

                ad.show();


            }
        });


    }
    public static byte[] getBytes(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0,stream);
        return stream.toByteArray();
    }
    public static Bitmap getImage(byte[] image)
    {
        return BitmapFactory.decodeByteArray(image,0,image.length);
    }

    public class MyThumbnaildapter extends ArrayAdapter<String> {

        public MyThumbnaildapter(Context context, int textViewResourceId,
                                 String[] objects) {
            super(context, textViewResourceId, objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub

            View row = convertView;
            if(row==null){
                LayoutInflater inflater=getLayoutInflater();
                row=inflater.inflate(R.layout.row, parent, false);
            }

            TextView textfilePath = (TextView)row.findViewById(R.id.FilePath);
            textfilePath.setText("솨랑해요~~~~");
            ImageView imageThumbnail = (ImageView)row.findViewById(R.id.Thumbnail);

            Bitmap bmThumbnail;
       //     bmThumbnail = ThumbnailUtils.createVideoThumbnail(videoFileList[position], MediaStore.Images.Thumbnails.MICRO_KIND);
      //      imageThumbnail.setImageBitmap(bmThumbnail);
            return row;
        }

    }


    public class postHttp extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            String url;
            InputStream is = null;

            String result = "";
            try {

                URL urlCon = new URL(objects[0].toString());
                HttpURLConnection httpCon = (HttpURLConnection) urlCon.openConnection();
                String json = "";
                json = ((JSONObject) objects[1]).toString();
                httpCon.setRequestProperty("Accept", "application/json");
                httpCon.setRequestProperty("Content-type", "application/json");


                // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.

                httpCon.setDoOutput(true);

                // InputStream으로 서버로 부터 응답을 받겠다는 옵션.

                httpCon.setDoInput(true);


                OutputStream os = httpCon.getOutputStream();

                os.write(json.getBytes("UTF-8"));

                os.flush();

                // receive response as inputStream

                try {

                    is = httpCon.getInputStream();

                    // convert inputstream to string
                    if (is != null)
                        result = convertInputStreamToString(is);
                    else
                        result = "Did not work!";

                } catch (IOException e) {

                    e.printStackTrace();

                } finally {

                    httpCon.disconnect();

                }

            } catch (IOException e) {

                e.printStackTrace();

            } catch (Exception e) {

                System.out.println("InputStream" + e.getLocalizedMessage());

            }
            try {
                JSONObject parsedResult = new JSONObject(result);
                if(parsedResult.has("SUCCESS"))
                    return result;
                JSONArray x = (JSONArray)parsedResult.get("myfriend");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;

        }

        private String convertInputStreamToString(InputStream inputStream) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while ((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }
    }
}

