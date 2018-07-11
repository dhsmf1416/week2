package com.example.q.myapplication;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;

public class ContactActivity extends Fragment {
    // The ListView
    TextView idView;
    EditText nickView;
    EditText descriptionView;
    private ArrayList<JSONObject> myJSONs;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int REQUEST_CALL = 1;
    SendMessageHandler mMainHandler = null;
    String nick = "";
    String description = "";
    Boolean isWoman = false;
    Bitmap bitmap = null;
    ImageView thumbView;
    Button btnCamera;
    Switch manman;
    Button btnSubmit;
    final int SET_MY_INFO = 1;
    final int CHANGE_MY_INFO = 2;
    class SendMessageHandler extends Handler {
        AlertDialog alert;
        AlertDialog.Builder alert_confirm;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case SET_MY_INFO:
                    EditText x = getView().findViewById(R.id.nickText);
                    if(x == null)
                        return;
                    x.setText(nick);
                    x = getView().findViewById(R.id.description);
                    x.setText(description);
                    Switch y = getView().findViewById(R.id.switch1);
                    y.setChecked(isWoman);
                    if(bitmap != null)
                        thumbView.setImageBitmap(bitmap);
                    break;
                case CHANGE_MY_INFO:
                    alert_confirm = new AlertDialog.Builder(getContext());
                    alert_confirm.setMessage("당신의 정보를 변경했습니다.");
                    alert_confirm.setNeutralButton("확인",null);
                    alert = alert_confirm.create();
                    alert.setTitle("SUCCESS");
                    alert.show();
                    break;
                default:
                    break;
            }
        }
        // postHTTP!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_main,container,false);
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        idView = getView().findViewById(R.id.idview);
        idView.setText(MainActivity.currentID);
        thumbView = getView().findViewById(R.id.thumbview);
        mMainHandler = new SendMessageHandler();
        postHttp php = new postHttp();
        JSONObject jsonParam = new JSONObject();
        try {
            if (Math.random() <= 0.5)
                jsonParam.put("sex", "M");
            else
                jsonParam.put("sex","F");
            jsonParam.put("nick",MainActivity.currentID);
            jsonParam.put("id",MainActivity.currentID);
            jsonParam.put("description","암유알맨암윰아");
            jsonParam.put("def","true");
            jsonParam.put("thumb","none");
        }catch(JSONException e) {
            e.printStackTrace();
        }
        php.execute("https://mymy.koreacentral.cloudapp.azure.com/api/detail",jsonParam);

        btnCamera = getView().findViewById(R.id.getphoto);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
            }
        });
        nickView = getView().findViewById(R.id.nickText);
        btnSubmit = getView().findViewById(R.id.submit);
        descriptionView = getView().findViewById(R.id.description);
        manman = getView().findViewById(R.id.switch1);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject jsonParam = new JSONObject();
                    jsonParam.put("id", MainActivity.currentID);
                    jsonParam.put("nick",nickView.getText().toString());
                    jsonParam.put("description",descriptionView.getText().toString());
                    if(manman.isChecked())
                        jsonParam.put("sex","F");
                    else
                        jsonParam.put("sex","M");
                    System.out.println("!#!@#$#");
                    if(bitmap != null)
                    {
                        byte[] image = getBytes(bitmap);
                        byte[] finals = Base64.encode(image,Base64.DEFAULT);
                        jsonParam.put("thumb", Arrays.toString(finals));
                        System.out.println(jsonParam);
                        System.out.println("-------");
                    }
                    else
                        jsonParam.put("thumb","none");
                    jsonParam.put("def","false");
                    postHttp php = new postHttp();
                    php.execute("https://mymy.koreacentral.cloudapp.azure.com/api/detail",jsonParam);
                }
                catch(JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED) {
            return;
        }
        bitmap = (Bitmap) data.getExtras().get("data");
        thumbView.setImageBitmap(bitmap);


    }
    /**
     * Show the contacts in the ListView.
     */

    public static byte[] getBytes(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        return stream.toByteArray();
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
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            // Android version is lesser than 6.0 or the permission is already granted.
            myJSONs = mySort(getContactNames());
            MyAdapter adapter = new MyAdapter(getActivity(), R.layout.row_contact, myJSONs);
        }
    }



    public ArrayList<JSONObject> mySort(ArrayList<JSONObject> myList)
    {
        ArrayList<JSONObject> sortedList = new ArrayList<JSONObject>();
        int sz0 = myList.size();
        int sz = 0;
        for(int x = 0; x <= sz0-1 ; x++){
            sz = myList.size();
            int z = 0;
            for(int y = 1; y <= sz-1 ; y++) {
                try {
                    String first = myList.get(z).get("name").toString();
                    String second = myList.get(y).get("name").toString();
                    if (first.compareToIgnoreCase(second) > 0)
                        z = y;
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            sortedList.add(myList.remove(z));
        }
        return sortedList;
    }


    public class MyAdapter extends BaseAdapter{
        Context context;
        ArrayList<JSONObject> con2;
        LayoutInflater inf;
        int layout;

        MyAdapter(Context c, int layout, ArrayList<JSONObject> con){
            this.context = c;
            this.con2 = con;
            this.layout = layout;
            inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = inf.inflate(layout, null);
            TextView TextView1 = (TextView) convertView.findViewById(R.id.textView);
    /*        TextView TextView2 = (TextView) convertView.findViewById(R.id.textView2);
            TextView TextView3 = (TextView) convertView.findViewById(R.id.textView3);*/
            JSONObject JO = con2.get(position);
            try {
                Object name = JO.get("name");
                TextView1.setText("   "+ name );
       /*         Object number = JO.get("number");"     Name       :            " +
                TextView2.setText("     Number   :            " + number);
                Object email = JO.get("email");
                TextView3.setText("     E-mail      :            " + email);  */
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
            return convertView;
        }
        @Override
        public Object getItem(int position)
        {
            return position;
        }
        @Override
        public long getItemId(int position)
        {
            return position;
        }
        @Override
        public int getCount()
        {
            return con2.size();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we cannot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Read the name of all the contacts.
     *
     * @return a list of names.
     */
    private ArrayList<JSONObject> getContactNames() {
        ArrayList<JSONObject> contacts = new ArrayList<>();
        // Get the ContentResolver
        ContentResolver cr = getActivity().getContentResolver();
        // Get the Cursor of all the contacts
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        // Move the cursor to first. Also check whether the cursor is empty or not.
        try {
            if (cursor.moveToFirst()) {
                // Iterate through the cursor
                do {
                    // Get the contacts name
                    JSONObject tmpJson = new JSONObject();

                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    tmpJson.put("name",name);
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String phone = null;
                    Integer hasPhone = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                    if (hasPhone > 0) {
                        Cursor cp = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                        if (cp != null && cp.moveToFirst()) {
                            phone = cp.getString(cp.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            cp.close();
                        }
                    }
                    tmpJson.put("number",phone);

                    String email = null;
                    Cursor ce = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{id}, null);
                    if (ce != null && ce.moveToFirst()) {
                        email = ce.getString(ce.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        ce.close();
                    }
                    tmpJson.put("email",email);
                    contacts.add(tmpJson);
                } while (cursor.moveToNext());
            }
            // Close the curosor
            cursor.close();
        }
        catch(JSONException e)
        {
            e.printStackTrace();
        }
        return contacts;
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
                nick = parsedResult.get("nickname").toString();
                description = parsedResult.get("descript").toString();
                if((parsedResult.get("sexsexse").toString()).equals("M"))
                    isWoman = false;
                else
                    isWoman = true;
                if(!parsedResult.get("thumbpat").toString().equals("none"))
                    bitmap = getImage(parsedResult.get("thumbpat").toString());
                String isBase = ((JSONObject)objects[1]).get("def").toString();
                if(isBase.equals("true"))
                    mMainHandler.sendEmptyMessage(SET_MY_INFO);
                else
                    mMainHandler.sendEmptyMessage(CHANGE_MY_INFO);
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
}
