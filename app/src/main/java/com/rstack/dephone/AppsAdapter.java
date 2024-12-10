package com.rstack.dephone;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View;
import android.widget.TextView;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder>{

    Context context1;
    List<String> stringList;
    DatabaseClass dbClass = new DatabaseClass();

    public AppsAdapter(Context context, List<String> list){

        context1 = context;
        stringList = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public CardView cardView;
        public ImageView imageView;
        public TextView textView_App_Name;
        public TextView textView_App_Package_Name;

        public ViewHolder (View view){

            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view);
            imageView = (ImageView) view.findViewById(R.id.imageview);
            textView_App_Name = (TextView) view.findViewById(R.id.Apk_Name);
            textView_App_Package_Name = (TextView) view.findViewById(R.id.Apk_Package_Name);
        }
    }

    @Override
    public AppsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View view2 = LayoutInflater.from(context1).inflate(R.layout.cardview_layout,parent,false);

        ViewHolder viewHolder = new ViewHolder(view2);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position){

        ApkInfoExtractor apkInfoExtractor = new ApkInfoExtractor(context1);

        final String ApplicationPackageName = (String) stringList.get(position);
        final String ApplicationLabelName = apkInfoExtractor.getAppName(ApplicationPackageName);
        Drawable drawable = apkInfoExtractor.getAppIconByPackageName(ApplicationPackageName);

        viewHolder.textView_App_Name.setText(ApplicationLabelName);

        //final String AppUsageByPackage = Integer.toString(dbClass.getHourByPackageName(ApplicationPackageName))+"hr "+
          //      Integer.toString(dbClass.getMinByPackageName(ApplicationPackageName))+"min";

        viewHolder.textView_App_Package_Name.setText(" ");

        viewHolder.imageView.setImageDrawable(drawable);

        //Adding click listener on CardView to open clicked application directly from here .
        viewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(view.getContext(), AppWiseSettingActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("app_name",ApplicationLabelName);
                i.putExtra("package_name",ApplicationPackageName);
                view.getContext().startActivity(i);


                /*Intent intent = context1.getPackageManager().getLaunchIntentForPackage(ApplicationPackageName);
                if(intent != null){

                    context1.startActivity(intent);

                }
                else {

                    Toast.makeText(context1,ApplicationPackageName + " Error, Please Try Again.", Toast.LENGTH_LONG).show();
                }*/
            }
        });
    }

    @Override
    public int getItemCount(){

        return stringList.size();
    }

}