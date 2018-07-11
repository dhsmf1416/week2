package com.example.q.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class loginActivity extends AppCompatActivity {
    Button loginSubmit;
    EditText idText;
    EditText pwText;
    Button signUp;
    final int SEND_LOGIN_ERROR = 1;
    SendMessageHandler mMainHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mMainHandler = new SendMessageHandler();
        loginSubmit = (Button)findViewById(R.id.loginsubmit);
        idText = (EditText)findViewById(R.id.idText);
        pwText = (EditText)findViewById(R.id.password);
        signUp = (Button)findViewById(R.id.signup);
        loginSubmit.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   String myID = idText.getText().toString();
                   String myPW = pwText.getText().toString();
                   postHttp php = new postHttp();
                   try {
                       String x = "https://mymy.koreacentral.cloudapp.azure.com/api/loginch";
                       JSONObject jsonParam = new JSONObject();
                       jsonParam.put("id",myID);
                       jsonParam.put("pw",myPW);
                       php.execute(x, jsonParam);
                   } catch (Exception e)
                   {
                       e.printStackTrace();
                   }
               }
        });
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpChange();

            }
        }


        );
    }
    public void signUpChange()
    {
        Intent intent = new Intent(this, signupActivity.class);
        int requestCode=200;
        startActivityForResult(intent,requestCode);
    }

    // postHTTP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public class postHttp extends AsyncTask {

        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        @Override
        protected Object doInBackground(Object[] objects) {
            String url;
            InputStream is = null;

            String result = "";
            try {

                URL urlCon = new URL(objects[0].toString());
                HttpURLConnection httpCon = (HttpURLConnection) urlCon.openConnection();
                String json = "";
                json = ((JSONObject)objects[1]).toString();
                httpCon.setRequestProperty("Accept", "application/json");
                httpCon.setRequestProperty("Content-type", "application/json");


                // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.

                httpCon.setDoOutput(true);

                // InputStream으로 서버로 부터 응답을 받겠다는 옵션.

                httpCon.setDoInput(true);


                OutputStream os = httpCon.getOutputStream();

                os.write(json.getBytes("euc-kr"));

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
                if(parsedResult.has("error"))
                    mMainHandler.sendEmptyMessage(SEND_LOGIN_ERROR);
                else
                {
                    String yourPassword = pwText.getText().toString();
                    String dbPassword = parsedResult.get("password").toString();
                    byte[] nowSalt = getBB(parsedResult.get("saltsalt").toString());
                    System.out.println(nowSalt);
                    if(dbPassword.equals(get_SHA_128_SecurePassword(yourPassword,nowSalt)))
                    {
                       Intent intent = new Intent(loginActivity.this,MainActivity.class);
                       intent.putExtra("currentID",idText.getText().toString());
                        startActivity(intent);
                    }
                    else
                        mMainHandler.sendEmptyMessage(SEND_LOGIN_ERROR);
                }
            } catch(JSONException e)
            {
                e.printStackTrace();
            }
            return result;

        }
        public byte[] getBB(String x)
        {
            String[] strings = x.replace("[","").replace("]","").split(", ");
            byte[] result = new byte[strings.length];
            for(int i=0;i<result.length;i++)
                result[i] = Byte.parseByte(strings[i]);
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

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

        }

    }

    class SendMessageHandler extends Handler {
        TextView x;
        AlertDialog alert;
        AlertDialog.Builder alert_confirm;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SEND_LOGIN_ERROR:
                    alert_confirm = new AlertDialog.Builder(loginActivity.this);
                    alert_confirm.setMessage("등록되지 않은 ID이거나 비밀번호가 다릅니다.");
                    alert_confirm.setNeutralButton("확인",null);
                    alert = alert_confirm.create();
                    alert.setTitle("ERROR:364");
                    alert.show();
                    break;
                default:
                    break;
            }
        }
        // postHTTP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }

    private static String get_SHA_128_SecurePassword(String passwordToHash, byte[] salt)
    {
        String generatedPassword = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] bytes = md.digest(passwordToHash.getBytes());
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedPassword = sb.toString();
        }
        catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        return generatedPassword;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        idText.setText("");
        pwText.setText("");
        AlertDialog alert;
        AlertDialog.Builder alert_confirm;
        if(resultCode == 200)
        {
            alert_confirm = new AlertDialog.Builder(this);
            alert_confirm.setMessage("성공적으로 가입됐습니다!");
            alert_confirm.setNeutralButton("확인",null);
            alert = alert_confirm.create();
            alert.setTitle("Good~");
            alert.show();
        }
        if(resultCode == 300)
        {
            alert_confirm = new AlertDialog.Builder(this);
            alert_confirm.setMessage("이유는 모르지만 가입 실패 ㅠ");
            alert_confirm.setNeutralButton("확인",null);
            alert = alert_confirm.create();
            alert.setTitle("ERROR:395");
            alert.show();
        }
    }
}
