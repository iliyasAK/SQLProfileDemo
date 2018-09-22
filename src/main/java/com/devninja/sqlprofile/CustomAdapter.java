package com.devninja.sqlprofile;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<RecordModel> recordList;
    private DbHelper dbHelper;

    private final String TAG = getClass().getSimpleName() + " MyLog";

    public CustomAdapter(Context context, ArrayList<RecordModel> recordList) {
        this.context = context;
        this.recordList = recordList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        @BindViews({R.id.tName, R.id.tAge, R.id.tPhone})
        List<TextView> textViews;
        @BindView(R.id.imgIcon)
        ImageView imageView;
        @BindView(R.id.cardView)
        CardView cardView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            dbHelper = new DbHelper(context);

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_recycler_view, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final RecordModel recordModel = recordList.get(position);
                holder.textViews.get(0).setText(recordModel.getName());
                holder.textViews.get(1).setText(recordModel.getAge());
                holder.textViews.get(2).setText(recordModel.getPhone());

                byte[] byteImage = recordModel.getImage();

        Bitmap bitmap = BitmapFactory.decodeByteArray(byteImage,0, byteImage.length);
        holder.imageView.setImageBitmap(bitmap);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, recordModel.getName() + " Clicked !");
                CharSequence[] items = {"Update", "Delete"};

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
                mBuilder.setTitle("Choose your option !");
                mBuilder.setIcon(R.drawable.ic_list);
                mBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(which == 0){

                            Intent updateIntent = new Intent(context, MainActivity.class);
                                    updateIntent.putExtra("id", recordModel.getId());
                                    updateIntent.putExtra("name", recordModel.getName());
                                    updateIntent.putExtra("age", recordModel.getAge());
                                    updateIntent.putExtra("phone", recordModel.getPhone());
                                    updateIntent.putExtra("image", recordModel.getImage());
                            context.startActivity(updateIntent);

                        }else {

                            try {

                                int count = dbHelper.deleteData(recordModel.getId());

                                if(count > 0){
                                    Intent intentList = new Intent(context, RecordListActivity.class);
                                    intentList.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    context.startActivity(intentList);

                                    Toast.makeText(context, recordModel.getName() + " has been deleted successfully !", Toast.LENGTH_LONG).show();
                                    Log.w(TAG, recordModel.getName() + " has been deleted successfully !");
                                }else{
                                    Toast.makeText(context, recordModel.getName() + " has not been deleted !", Toast.LENGTH_LONG).show();
                                    Log.w(TAG, recordModel.getName() + " has not been deleted !");
                                }
                            } catch (Exception e) {
                                Log.w(TAG, e);
                            }
                        }

                    }
                });

                mBuilder.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return recordList.size();
    }

}
