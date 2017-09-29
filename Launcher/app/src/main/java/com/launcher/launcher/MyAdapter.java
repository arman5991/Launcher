package com.launcher.launcher;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.provider.Settings;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private PackageManager manager;
    private List<Item> list;
    private Context context;

    public MyAdapter(List<Item> list, PackageManager manager, Context context) {
        this.list = list;
        this.manager = manager;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.resycler, parent, false);
        ViewHolder view = new ViewHolder(v);
        return view;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.imgIcon.setImageDrawable(list.get(position).icon);
        holder.iconText.setText(list.get(position).namee);

        holder.imgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        try {
                            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                            audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamVolume(AudioManager.STREAM_RING), AudioManager.FLAG_SHOW_UI);
                        } catch (Exception e) {
                            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                            if (!mNotificationManager.isNotificationPolicyAccessGranted()) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                                context.startActivity(intent);
                            }
                        }
                    } else {
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamVolume(AudioManager.STREAM_RING), AudioManager.FLAG_SHOW_UI);
                    }
                }else if (position == 1) {
                    dialogBrightness();
                } else {
                    Intent i = manager.getLaunchIntentForPackage(list.get(position).label.toString());
                    context.startActivity(i);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView iconText;
        private ImageView imgIcon;

        private ViewHolder(View v) {
            super(v);
            iconText = (TextView) v.findViewById(R.id.icon_text);
            imgIcon = (ImageView) v.findViewById(R.id.img_icon);
        }
    }

    private void dialogBrightness(){
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.seekbar_dialog);
        SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.seekBar);
        dialog.setTitle("Brightness");
        dialog.getWindow().setLayout(500, 150);
        dialog.getWindow().setGravity(Gravity.TOP);
        dialog.getWindow().setBackgroundDrawableResource(R.color.seek_bar);


        float curBrightnessValue = 0;
        try {
            curBrightnessValue = android.provider.Settings.System.getInt(
                    context.getContentResolver(), android.provider.Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        int screen_brightness = (int) curBrightnessValue;

        seekBar.setProgress(screen_brightness);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
                android.provider.Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                android.provider.Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, progress);
            }
        });
        dialog.show();
    }
}
