package com.devninja.sqlprofile;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordListActivity extends AppCompatActivity {

    private ArrayList<RecordModel> mList;
    private DbHelper dbHelper;
    private CustomAdapter customAdapter = null;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        ButterKnife.bind(this);
        dbHelper = new DbHelper(this);
        mList = new ArrayList<>();

        customAdapter = new CustomAdapter(this, mList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(customAdapter);

        Cursor mCursor = dbHelper.getData();

        mList.clear();

        while (mCursor.moveToNext()){
            int id = mCursor.getInt(0);
            String name = mCursor.getString(1);
            String age = mCursor.getString(2);
            String phone = mCursor.getString(3);
            byte[] image = mCursor.getBlob(4);

            mList.add(new RecordModel(id, name, age, phone, image));

        }

        customAdapter.notifyDataSetChanged();

        if(mList.size() == 0){
            Toast.makeText(getApplicationContext(), "No record found !", Toast.LENGTH_LONG).show();
        }
    }
}
