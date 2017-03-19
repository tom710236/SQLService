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
    //宣告
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




    //建立一個類別存JSON
    public class ProductInfo {
        private String cProductID;
        private String cProductName;
        private String cGoodsNo;
        private String cUpdateDT;

        //建構子
        ProductInfo(final String ProductID, final String ProductName, final String GoodsNo,final String UpdateDT) {
            this.cProductID = ProductID;
            this.cProductName = ProductName;
            this.cGoodsNo = GoodsNo;
            this.cUpdateDT = UpdateDT;

        }
        //方法
        @Override
        public String toString() {
            return this.cProductID +  this.cProductName  + this.cGoodsNo + this.cUpdateDT;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //實做 tblTable(繼承SQLiteOpenHelp)類別 建立資料庫（傳入的地方(this),資料庫名稱,標準模式處理Cursor,資料庫版本)
        helper = new MyDBHelper(this, DB_NAME, null, 1);
        //實做 db(繼承SQLiteDatabase)類別 getWritableDatabase用來更新 新增修改刪除
        db = helper.getWritableDatabase();

    }



    class Get extends Thread {
        @Override
        public void run() {
            okHttpGet();
        }
    }
    // Get
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
            //把get到的資料(JSON)轉為字串 並執行parseJson方法
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.e("json",json);
                parseJson(json);
            }
        });

    }
    //把json的資料解析出來 並放入SQL裡
    private void parseJson(String json) {

        try {
            //解析JSON資料
            trans = new ArrayList<ProductInfo>();
            final JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                trans.add(new ProductInfo(obj.optString("cProductID"), obj.optString("cProductName"),obj.optString("cGoodsNo"),obj.optString("cUpdateDT")));
                ID = obj.optString("cProductID");
                name = obj.optString("cProductName");
                NO = obj.optString("cGoodsNo");
                DT = obj.optString("cUpdateDT");
                //放入SQL
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
        //先刪除就有資料表格
        db.delete(DB_NAME, null, null);
        //放入新增表格
        Get get = new Get();
        get.start();

    }
    public void onClick2 (View v){
        //把JSON的值用ListView顯示出來
        listView = (ListView)findViewById(R.id.lv);
        final ArrayAdapter<ProductInfo> list = new ArrayAdapter<>(MainActivity.this,
                android.R.layout.simple_list_item_1,
                trans);
                listView.setAdapter(list);
    }
    public void onClick3 (View v){

    }
    //執行Delay
    public void onClick4(View v) {
        Intent intent = new Intent(this, Delay.class);
        startService(intent);
    }
    //停止Delay
    public void onClick5(View v){
        Intent intent = new Intent(this,Delay.class);
        stopService(intent);
    }




}
