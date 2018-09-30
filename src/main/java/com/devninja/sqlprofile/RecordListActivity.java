package com.devninja.sqlprofile;

import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecordListActivity extends AppCompatActivity {

    private ArrayList<RecordModel> mList, newList;
    private DbHelper dbHelper;
    private CustomAdapter customAdapter = null;
    private Toolbar toolbar;
    private MaterialSearchView searchView;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);

        ButterKnife.bind(this);
        dbHelper = new DbHelper(this);
        mList = new ArrayList<>();

        searchView = findViewById(R.id.search_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                }
            }
        });


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        searchView.setHint("Search...");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
                searchView.closeSearch();
              return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newList = new ArrayList<>();
                for (int i = 0; i < mList.size(); i++) {
                    if (mList.get(i).getName().toUpperCase().contains(newText.toUpperCase())){
                        newList.add(mList.get(i));
                    }
                    customAdapter = new CustomAdapter(getApplicationContext(), newList);
                    recyclerView.setAdapter(customAdapter);
                }

                customAdapter.notifyDataSetChanged();

                return true;
            }
        });
        return true;
    }
    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
            Toast.makeText(getApplicationContext(), "Going back", Toast.LENGTH_LONG).show();
        }
    }
}
