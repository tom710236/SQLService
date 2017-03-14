package com.example.tom.sqlservice;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    String url = "http://demo.shinda.com.tw/ModernWebApi/getProduct.aspx";
    ArrayList<ProductInfo> trans;
    private MyDBHelper helper;
    SQLiteDatabase db;
    final String DB_NAME = "tblTable";
    ListView listView;
    ContentValues addbase;
    int DB_NUM = 0;
    final String tableName = "tblOrder";//資料表名稱
    String ID,name,NO,DT;





    public class ProductInfo {
        private String cProductID;
        private String cProductName;
        private String cGoodsNo;
        private String cUpdateDT;


        ProductInfo(final String ProductID, final String ProductName, final String GoodsNo,final String UpdateDT) {
            this.cProductID = ProductID;
            this.cProductName = ProductName;
            this.cGoodsNo = GoodsNo;
            this.cUpdateDT = UpdateDT;

        }

        @Override
        public String toString() {
            return this.cProductID +  this.cProductName  + this.cGoodsNo + this.cUpdateDT;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }



    class Get extends Thread {
        @Override
        public void run() {
            okHttpGet();
        }
    }

    private void okHttpGet(){

        final OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback(){

            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.e("json",json);
                parseJson(json);
            }
        });

    }

    private void parseJson(String json) {

        try {
            trans = new ArrayList<ProductInfo>();

            final JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {

                JSONObject obj = array.getJSONObject(i);
                trans.add(new ProductInfo(obj.optString("cProductID"), obj.optString("cProductName"),obj.optString("cGoodsNo"),obj.optString("cUpdateDT")));
                ID = obj.optString("cProductID");
                name = obj.optString("cProductName");
                NO = obj.optString("cGoodsNo");
                DT = obj.optString("cUpdateDT");

                addbase = new ContentValues();
                addbase.put("cProductID", ID);
                addbase.put("cProductName", name);
                addbase.put("cGoodsNo", NO);
                addbase.put("cUpdateDT", DT);
                db.insert(DB_NAME, null, addbase);

            }



        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

    public void onClick (View v){
        DB_NUM++;
        helper = new MyDBHelper(this, DB_NAME, null, 30);
        db = helper.getWritableDatabase();
        Log.e("DB", String.valueOf(DB_NUM));
        Get get = new Get();
        get.start();

    }
    public void onClick2 (View v){
        listView = (ListView)findViewById(R.id.lv);
        final ArrayAdapter<ProductInfo> list = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1,
                trans);
                listView.setAdapter(list);
    }
    public void onClick3 (View v){

    }
    public void onClick4(View v){
        Intent intent = new Intent(this,Delay.class);
        startService(intent);
    }
    public void onClick5(View v){
        Intent intent = new Intent(this,Delay.class);
        stopService(intent);
    }




}
