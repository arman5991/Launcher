package com.launcher.launcher;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private View decorView;
    private List<Item> apps;
    private PackageManager manager;
    private RecyclerView recyclerView;
    private ImageView imgmenu;

    private final Handler handler = new Handler();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM");
    private final SimpleDateFormat clockTime = new SimpleDateFormat("yyyy EEEE");

    private Timer timer = new Timer(false);

    private TextView tvDate;
    private TextView tvTime;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgmenu = (ImageView) findViewById(R.id.btn_img_menu);
        tvDate = (TextView) findViewById(R.id.tv_date);
        tvTime = (TextView) findViewById(R.id.tv_time);
        imgmenu.setOnClickListener(this);

        time();
        LoadApps();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 5);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        MyAdapter adapter = new MyAdapter(apps, manager, this);
        recyclerView.setAdapter(adapter);


        decorView = getWindow().getDecorView();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
        closeStatusBar();
        getStatusBarHeight();
    }

    private void time(){
        final TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tvDate.setText(dateFormat.format(new Date()));
                        tvTime.setText(clockTime.format(new Date()));
                    }
                });
            }
        };
        timer.schedule(timerTask,0,1000);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void closeStatusBar() {
        WindowManager manager = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                // this is to enable the notification to recieve touch events
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                // Draws over status bar
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        localLayoutParams.height = getStatusBarHeight();
        localLayoutParams.format = PixelFormat.TRANSPARENT;
        CustomViewGroup view = new CustomViewGroup(this);
        manager.addView(view, localLayoutParams);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        recyclerView.setVisibility(View.GONE);
        imgmenu.setVisibility(View.VISIBLE);
        tvDate.setVisibility(View.VISIBLE);
        tvTime.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_img_menu:{
                recyclerView.setVisibility(View.VISIBLE);
                imgmenu.setVisibility(View.GONE);
                tvDate.setVisibility(View.GONE);
                tvTime.setVisibility(View.GONE);
                break;
            }
        }
    }

    private void LoadApps() {
        manager = getPackageManager();
        apps =  new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);

        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        for (ResolveInfo ri : availableActivities) {
            Item app = new Item();
            app.label = ri.activityInfo.packageName; //get package name
            app.namee = ri.loadLabel(manager); // get app  name
            app.icon = ri.loadIcon(manager); // get app icon
//            if(app.namee.equals("Settings") || app.namee.equals("Calculator") || app.namee.equals("Gallery") ||
//                    app.namee.equals("Calendar") || app.namee.equals("Clock") || app.namee.equals("Camera")
//                    || app.namee.equals("Algoid") || app.namee.equals("QuickPic") || app.namee.equals("WiFi")) {
//                apps.add(app);
//            }
            apps.add(app);
        }

        Item volume = new Item();
        volume.namee = "Volume";
        volume.icon = getResources().getDrawable(R.drawable.volume);
        apps.add(0,volume);

        Item brightness = new Item();
        brightness.namee = "Brightness";
        brightness.icon = getResources().getDrawable(R.drawable.brightness);
        apps.add(1,brightness);

    }
}

