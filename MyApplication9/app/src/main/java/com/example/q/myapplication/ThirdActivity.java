package com.example.q.myapplication;


import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class ThirdActivity extends Fragment {
    ArrayList<String> fromList;
    ArrayList<String> toList;
    JSONObject friendInfo;
    final int SHOW_MY_LIST = 1;
    final int SHOW_FREIND = 2;
    SendMassgeHandler mMainHandler;
    FloatingActionButton refreshButton;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.third_main,container,false);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshButton = getView().findViewById(R.id.refresh);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postHttp php = new postHttp();
                mMainHandler = new SendMassgeHandler();
                try {
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id",MainActivity.currentID);
                    php.execute("https://mymy.koreacentral.cloudapp.azure.com/api/reqres", jsonParam);
                }catch(JSONException e)
                {
                    e.printStackTrace();
                }

            }
        });
        postHttp php = new postHttp();
        mMainHandler = new SendMassgeHandler();
        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("id",MainActivity.currentID);
            php.execute("https://mymy.koreacentral.cloudapp.azure.com/api/reqres", jsonParam);
        }catch(JSONException e)
        {
            e.printStackTrace();
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
                System.out.println(result);
                JSONArray parsedResult = new JSONArray(result);
                fromList = new ArrayList<>();
                toList = new ArrayList<>();
                MainActivity.myFriends = new ArrayList<>();
                for(int i=0;i<parsedResult.length();i++)
                {
                    JSONObject tt = parsedResult.getJSONObject(i);
                    if((tt.get("fromidfr").toString()).equals(MainActivity.currentID))
                    {
                        if(!toList.contains(tt.get("toidtoid").toString()))
                            toList.add(tt.get("toidtoid").toString());
                    }
                }
                for(int i=0;i<parsedResult.length();i++)
                {
                    JSONObject yy = parsedResult.getJSONObject(i);
                    if(yy.get("toidtoid").toString().equals(MainActivity.currentID))
                    {
                        if(toList.contains(yy.get("fromidfr").toString()))
                        {
                            toList.remove(yy.get("fromidfr").toString());
                            MainActivity.myFriends.add(yy.get("fromidfr").toString());
                        }
                        else
                            fromList.add(yy.get("fromidfr").toString());
                    }
                }
                mMainHandler.sendEmptyMessage(SHOW_MY_LIST);
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
    public class postHttp2 extends AsyncTask {

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
            try
            {
                friendInfo = new JSONObject(result);
                mMainHandler.sendEmptyMessage(SHOW_FREIND);
            }catch(JSONException e)
            {
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
    class SendMassgeHandler extends Handler {
        TextView x;
        AlertDialog alert;
        AlertDialog.Builder alert_confirm;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SHOW_MY_LIST:
                    ListView x = getView().findViewById(R.id.receive);
                    String[] array = new String[fromList.size()];
                    array = fromList.toArray(array);
                    x.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,array));

                    ListView y = getView().findViewById(R.id.give);
                    String[] array2 = new String[toList.size()];
                    array2 = toList.toArray(array2);
                    y.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,array2));


                    String[] array3 = new String[MainActivity.myFriends.size()];
                    array3 = MainActivity.myFriends.toArray(array3);
                    GalleryActivity.z.setAdapter(new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,array3));
                    GalleryActivity.z.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            postHttp2 php2 = new postHttp2();
                            try {
                                JSONObject jsonParam = new JSONObject();
                                jsonParam.put("id",MainActivity.myFriends.get(i));
                                php2.execute("https://mymy.koreacentral.cloudapp.azure.com/api/myfriends", jsonParam);
                            }catch(JSONException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                    break;
                case SHOW_FREIND:
                    try {
                        alert_confirm = new AlertDialog.Builder(getContext());
                        String mySex;
                        if(friendInfo.get("sexsexse").toString().equals("M"))
                            mySex = "남자";
                        else
                            mySex = "여자";
                        final String myFriendId = friendInfo.get("idididid").toString();
                        System.out.println(friendInfo);
                        alert_confirm.setTitle(friendInfo.get("nickname").toString() + "(" + myFriendId + ") " + mySex);
                        final View view = View.inflate(getContext(),R.layout.indivisual,null);
                        ImageView d = view.findViewById(R.id.dialog_imageview);
                        if(!friendInfo.get("thumbpat").toString().equals("none"))
                            d.setImageBitmap(getImage(friendInfo.get("thumbpat").toString()));
                        TextView v = view.findViewById(R.id.textView16);
                        v.setText(friendInfo.get("descript").toString());
                        final String nicknick = friendInfo.get("nickname").toString();
                        alert_confirm.setView(view);
                        alert_confirm.setNeutralButton("1:1 채팅", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String toPerson = myFriendId;
                                String nick = nicknick;
                                String fromPerson = MainActivity.currentID;
                                Intent intent = new Intent(getActivity(), chatActivity.class);
                                intent.putExtra("fromid",fromPerson);
                                intent.putExtra("toid",toPerson);
                                intent.putExtra("nick",nick);
                                startActivity(intent);
                            }
                        });
                        alert_confirm.setNegativeButton("닫기",null);
                        alert = alert_confirm.create();
                        alert.show();
                    }catch (JSONException e)
                    {
                        e.printStackTrace();
                    }

                    break;
                default:
                    break;
            }
        }

    }

    public Bitmap getImage(String encodedImage)
    {
        byte[] image = Base64.decode(getBB(encodedImage), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(image,0,image.length);
    }

    public byte[] getBB(String x)
    {
        String[] strings = x.replace("[","").replace("]","").split(", ");
        byte[] result = new byte[strings.length];
        for(int i=0;i<result.length;i++)
            result[i] = Byte.parseByte(strings[i]);
        return result;
    }
}
