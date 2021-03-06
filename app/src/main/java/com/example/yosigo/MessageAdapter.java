package com.example.yosigo;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    public static final int MSG_LEFT = 0;
    public static final int MSG_RIGHT = 1;
    private final Context context;
    private final List<String> id;
    private final Map<String,Date> item_date;
    private final Map<String,String> item_user;
    private final Map<String,String> item_name;
    private final Map<String,String> item_user_picto;
    private final Map<String,String> item_type;
    private final Map<String,String> item_content;
    private MediaPlayer mediaPlayer = null;
    private int posPause = 0;

    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView fecha;
        TextView nombre;
        TextView message;
        ImageView photo;
        ImageView imageSender;
        VideoView video;
        ImageButton play, pause, stop;
        LinearLayout btn_audio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fecha = (TextView) itemView.findViewById(R.id.show_date_message);
            nombre = (TextView) itemView.findViewById(R.id.show_name_sender);
            message = (TextView) itemView.findViewById(R.id.show_message);
            photo = itemView.findViewById(R.id.show_image_message);
            video = itemView.findViewById(R.id.show_video_message);
            btn_audio = itemView.findViewById(R.id.show_btn_hear_message);
            play = itemView.findViewById(R.id.btn_hear_play);
            pause = itemView.findViewById(R.id.btn_hear_pause);
            stop = itemView.findViewById(R.id.btn_hear_stop);
            imageSender = itemView.findViewById(R.id.image_sender);
        }

        public TextView getFecha() { return fecha; }
        public TextView getNombre() { return nombre; }
        public TextView getMessage(){
            return message;
        }
        public ImageView getPhoto() { return photo; }
        public VideoView getVideo() { return video; }
        public ImageView getImageSender() { return imageSender; }

        //Reproducir audio
        public LinearLayout getBtn_audio() {return btn_audio;}
        public ImageButton getPlay() { return play; }
        public ImageButton getPause() { return pause; }
        public ImageButton getStop() { return stop; }
    }

    public MessageAdapter(Context context, List<String> id, Map<String, Date> item_date,
                          Map<String, String> emisorId, Map<String, String> item_name,
                          Map<String, String> emisorImage, Map<String, String> item_type,
                          Map<String, String> item_content) {
        this.context = context;
        this.id = id;
        this.item_date = item_date;
        this.item_user = emisorId;
        this.item_name = item_name;
        this.item_user_picto = emisorImage;
        this.item_type = item_type;
        this.item_content = item_content;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_RIGHT) {
            View rowView = LayoutInflater.from(context).inflate(R.layout.chat_message_right, parent, false);
            return new ViewHolder(rowView);
        } else {
            View rowView = LayoutInflater.from(context).inflate(R.layout.chat_message_left, parent, false);
            return new ViewHolder(rowView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String id_item = id.get(position);

        //Convertir fecha a String
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String strDate = dateFormat.format(item_date.get(id_item));
        holder.getFecha().setText(strDate);

        holder.getNombre().setText(item_name.get(id_item));
        Uri uriImage = Uri.parse(item_user_picto.get(id_item));
        Glide.with(context)
                .load(uriImage)
                .into(holder.getImageSender());

        if(item_type.get(id_item).equals("Audio")){
            setAudio(holder, id_item);
        } else if(item_type.get(id_item).equals("Imagen")){
            setImage(holder, id_item);
        } else if(item_type.get(id_item).equals("Video")){
            setVideo(holder, id_item);
        } else {
            setTexto(holder, id_item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (item_user.get(id.get(position)).equals(MainActivity.sesion)){
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }

    @Override
    public int getItemCount() { return this.id.size(); }

    private void setTexto(ViewHolder holder, String id){
        holder.getMessage().setVisibility(View.VISIBLE);
        holder.getMessage().setText(item_content.get(id));
    }

    private void setAudio(ViewHolder holder, String id){
        holder.getBtn_audio().setVisibility(View.VISIBLE);

        FirebaseStorage.getInstance().getReference().
                child(item_content.get(id))
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                holder.getPlay().setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if(mediaPlayer == null) {
                            mediaPlayer = new MediaPlayer();
                            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                            try {
                                mediaPlayer.setDataSource(context, uri);
                                mediaPlayer.prepare();
                                mediaPlayer.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            mediaPlayer.seekTo(posPause);
                            mediaPlayer.start();
                        }
                    }
                });

                holder.getPause().setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        if(mediaPlayer != null) {
                            mediaPlayer.pause();
                            posPause = mediaPlayer.getCurrentPosition();
                        }
                    }
                });

                holder.getStop().setOnClickListener( new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        mediaPlayer.stop();
                        mediaPlayer = null;
                    }
                });
            }
        });
    }

    private void setImage(ViewHolder holder, String id){
        holder.getPhoto().setVisibility(View.VISIBLE);

        storageRef.child(item_content.get(id)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context)
                        .load(uri)
                        .into(holder.getPhoto());
            }
        });
    }

    private void setVideo(ViewHolder holder, String id){
        VideoView videoView = holder.getVideo();
        holder.getVideo().setVisibility(View.VISIBLE);

        storageRef.child(item_content.get(id)).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                videoView.setVideoURI(uri);
                videoView.setMediaController(new MediaController(context));
                videoView.start();
            }
        });
    }
}

