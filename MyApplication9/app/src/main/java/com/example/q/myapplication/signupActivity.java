package com.example.q.myapplication;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

public class signupActivity extends AppCompatActivity {
    Button idValid;
    Button pwValid;
    Button signUp;
    EditText idText;
    EditText pwText;
    EditText pwText2;
    TextView pwAlarm;
    Button exit;
    boolean isGoodId = false;
    boolean isGoodPw = false;
    private SendMassgeHandler mMainHandler = null;
    final int SEND_ID_SAME_ERROR = 1;
    final int SEND_GOOD_ID_INFO= 2;
    final int SUBMIT_ID_ERROR=3;
    final int SUBMIT_PW_ERROR=4;
    final int SEND_OK_SIGNUP=5;
    final int SEND_FAIL_SIGNUP=6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        idValid = findViewById(R.id.idvalid);
        exit = findViewById(R.id.exit);
        signUp = findViewById(R.id.signup);
        idText = findViewById(R.id.idText);
        pwText = findViewById(R.id.pwtext);
        pwText2 = findViewById(R.id.pwtext2);
        pwValid = findViewById(R.id.pwvalid);
        pwAlarm = findViewById(R.id.pwalarm);
        mMainHandler = new SendMassgeHandler();
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(600);
                finish();
            }
        });
        pwValid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String myPassword = pwText.getText().toString();
                if (myPassword.length() < 8 || myPassword.length() > 16) {
                    pwAlarm.setText("8자에서 16자까지만 가능합니다.");
                    isGoodPw = false;
                    return;
                }
                if (!myPassword.equals(pwText2.getText().toString())) {
                    pwAlarm.setText("비밀번호 두 개가 다릅니다.");
                    isGoodPw = false;
                    return;
                }
                pwAlarm.setText("좋은 비밀번호입니다.");
                isGoodPw = true;

            }
        });
        idValid.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                postHttp php = new postHttp();
                try {

                    String myUrl = "https://mymy.koreacentral.cloudapp.azure.com/api/idcheck";
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id",idText.getText().toString());
                    php.execute(myUrl,jsonParam,"idcheck");
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        signUp.setOnClickListener(new View.OnClickListener()
      {
          @Override
          public void onClick(View view)
          {
                if(!isGoodId)
                {
                    mMainHandler.sendEmptyMessage(SUBMIT_ID_ERROR);
                    return;

                }

              if(!isGoodPw)
              {
                  mMainHandler.sendEmptyMessage(SUBMIT_PW_ERROR);
                  return;

              }

                postHttp php = new postHttp();
                try {
                    String idid = idText.getText().toString();
                    String pwpw = pwText.getText().toString();
                    byte[] mySalt = getSalt();
                    pwpw = get_SHA_128_SecurePassword(pwpw,mySalt);
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id",idid);
                    jsonParam.put("pw",pwpw);
                    jsonParam.put("sa",Arrays.toString(mySalt));
                    String myUrl = "https://mymy.koreacentral.cloudapp.azure.com/api/signup";
                    php.execute(myUrl,jsonParam,"signup");
                } catch (Exception e)
                {
                    e.printStackTrace();
                }

          }
      }

        );

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

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

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

                json = ((JSONObject) objects[1]).toString();
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
            if(objects[2].toString().equals("signup"))
            {
                if(Integer.parseInt(result)==1)
                    setResult(200);
                else
                    setResult(300);
                signupActivity.this.finish();
                return result;
            }
            if(Integer.parseInt(result) == 0)
                mMainHandler.sendEmptyMessage(SEND_ID_SAME_ERROR);
            if(Integer.parseInt(result) == 1)
                mMainHandler.sendEmptyMessage(SEND_GOOD_ID_INFO);
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
    class SendMassgeHandler extends Handler {
        TextView x;
        AlertDialog alert;
        AlertDialog.Builder alert_confirm;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SEND_ID_SAME_ERROR:
                    x = findViewById(R.id.idalarm);
                    x.setText("중복된 ID입니다!");
                    isGoodId = false;
                    break;
                case SEND_GOOD_ID_INFO:
                    x = findViewById(R.id.idalarm);
                    x.setText("좋은 ID입니다!");
                    isGoodId = true;
                    break;
                case SUBMIT_ID_ERROR:
                    alert_confirm = new AlertDialog.Builder(signupActivity.this);
                    alert_confirm.setMessage("ID 유효체크 부탁합니다.");
                    alert_confirm.setNeutralButton("확인",null);
                    alert = alert_confirm.create();
                    alert.setTitle("ERROR:364");
                    alert.show();
                    break;
                case SUBMIT_PW_ERROR:
                    alert_confirm = new AlertDialog.Builder(signupActivity.this);
                    alert_confirm.setMessage("비밀번호 유효체크 부탁합니다.");
                    alert_confirm.setNeutralButton("확인",null);
                    alert = alert_confirm.create();
                    alert.setTitle("ERROR:365");
                    alert.show();
                    break;
                default:
                    break;
            }
        }

    }

}
