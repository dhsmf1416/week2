package com.example.q.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

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

public class chatActivity extends AppCompatActivity {
    String fromid;
    String toid;
    TextView whowho;
    Button sendButton;
    EditText chatText;
    ListView chatView;
    String nick;
    JSONObject repeatParam;
    ArrayList<String> chatList;
    final int GET_CHAT_INFO = 1;
    SendMessageHandler mMainHandler;
    static boolean isExit = true;
    Button exitButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        isExit = false;
        exitButton = findViewById(R.id.exit);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isExit = true;
                finish();
            }
        });
        mMainHandler = new SendMessageHandler();
        whowho = findViewById(R.id.idid);
        chatView = findViewById(R.id.chatlog);
        Intent myIntent = new Intent(this.getIntent());
        fromid = myIntent.getStringExtra("fromid");
        toid = myIntent.getStringExtra("toid");
        nick = myIntent.getStringExtra("nick");
        whowho.setText(nick+" ("+toid+") 와(과)의 대화");
        getChatText();
        chatText = findViewById(R.id.chattext);
        sendButton = findViewById(R.id.sendbutton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ourText = chatText.getText().toString();
                chatText.setText("");
                postHttp2 php2 = new postHttp2();
                try {
                    JSONObject jsonParam = new JSONObject();
                    if(toid.compareTo(fromid)>0) {
                        jsonParam.put("fromid", fromid);
                        jsonParam.put("toid", toid);
                        jsonParam.put("reverse","false");
                        jsonParam.put("chat",ourText);
                    } else {
                        jsonParam.put("fromid", toid);
                        jsonParam.put("toid", fromid);
                        jsonParam.put("reverse","true");
                        jsonParam.put("chat",ourText);
                    }


                    php2.execute("https://mymy.koreacentral.cloudapp.azure.com/api/send", jsonParam);
                }catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    public void getChatText()
    {
        postHttp php = new postHttp();
        try {
            repeatParam = new JSONObject();
            if(toid.compareTo(fromid)>0) {
                repeatParam.put("fromid", fromid);
                repeatParam.put("toid", toid);
            }
            else {
                repeatParam.put("fromid",toid);
                repeatParam.put("toid",fromid);
            }

            new Handler().postDelayed(new Runnable() {// 1 초 후에 실행
                @Override public void run() {
                    if(isExit)
                        return;
                    postHttp php = new postHttp();
                    php.execute("https://mymy.koreacentral.cloudapp.azure.com/api/chat", repeatParam);
                    getChatText();
                } }, 1000);
        } catch(JSONException e)
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
                JSONArray parsedResult = new JSONArray(result);
                chatList = new ArrayList<>();
                for(int i=0;i<parsedResult.length();i++)
                {
                    String sendman = "";
                    JSONObject x =parsedResult.getJSONObject(i);
                    if(x.get("fromidfr").toString().equals(fromid))
                    {
                        if(x.get("reverser").toString().equals("false"))
                            sendman = "나" + ": ";
                        else
                            sendman = toid + ": ";
                    }
                    else
                    {
                        if(x.get("reverser").toString().equals("false"))
                            sendman = toid + ": ";
                        else
                            sendman = "나" + ": ";
                    }
                     chatList.add(sendman + x.get("chatchat").toString());
                }
                mMainHandler.sendEmptyMessage(GET_CHAT_INFO);
                System.out.println(parsedResult);
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
    class SendMessageHandler extends Handler {
        AlertDialog alert;
        AlertDialog.Builder alert_confirm;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case GET_CHAT_INFO:
                    String[] array2 = new String[chatList.size()];
                    array2 = chatList.toArray(array2);
                    chatView.setAdapter(new ArrayAdapter<String>(chatActivity.this,android.R.layout.simple_list_item_1,array2));
                    break;
                default:
                    break;
            }
        }
        // postHTTP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
}
