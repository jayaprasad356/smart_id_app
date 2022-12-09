package com.app.abcdapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.widget.Toast;

import com.app.abcdapp.R;
import com.app.abcdapp.helper.ApiConfig;
import com.app.abcdapp.helper.Constant;
import com.app.abcdapp.helper.DatabaseHelper;
import com.app.abcdapp.helper.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ImportDataActivity extends AppCompatActivity {
    Activity activity;
    DatabaseHelper databaseHelper;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_data);
        activity = ImportDataActivity.this;
        session =  new Session(activity);

        databaseHelper = new DatabaseHelper(activity);
        databaseHelper.deleteDb(activity);

        importDataApi();
    }

    private void importDataApi()
    {
        Map<String, String> params = new HashMap<>();
        params.put(Constant.IMPORT_DATA,"1");
        ApiConfig.RequestToVolley((result, response) -> {
            if (result) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean(Constant.SUCCESS)) {
                        JSONArray jsonArray = jsonObject.getJSONArray(Constant.DATA);
                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
                        String sql = "INSERT INTO tblcodes (id, student_name, id_number, ecity, pin_code) VALUES (?, ?, ?, ?, ?)";
                        db.beginTransaction();
                        SQLiteStatement stmt = db.compileStatement(sql);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            stmt.bindString(1, jsonArray.getJSONObject(i).getString(Constant.ID));
                            stmt.bindString(2, jsonArray.getJSONObject(i).getString(Constant.STUDENT_NAME));
                            stmt.bindString(3, jsonArray.getJSONObject(i).getString(Constant.ID_NUMBER));
                            stmt.bindString(4, jsonArray.getJSONObject(i).getString(Constant.ECITY));
                            stmt.bindString(5, jsonArray.getJSONObject(i).getString(Constant.PIN_CODE));
                            stmt.execute();
                            stmt.clearBindings();

                        }
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        session.setBoolean(Constant.IMPORT_DATA,true);
                        session.setBoolean("is_logged_in", true);
                        Intent intent = new Intent(activity, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Toast.makeText(this, ""+jsonObject.getString(Constant.MESSAGE), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            else {
                Toast.makeText(this, String.valueOf(response) +String.valueOf(result), Toast.LENGTH_SHORT).show();

            }
            //pass url
        }, activity, Constant.IMPORT_DATA_URL, params,true);


    }
}