package org.nadozirny_sv.ua.cubic;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


/**
 * Created by Nadozirny_SV on 25.08.2015.
 */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesHolder>  {
    private ArrayList<NotesItem> feed;
    private Context mContext;
    public NotesAdapter(Context context){
        feed=new ArrayList<NotesItem>();
        mContext=context;
    }

    @Override
    public NotesHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View  view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notes_row, null);
        return new NotesHolder(view);
    }

    @Override
    public void onBindViewHolder(NotesHolder notesHolder, int i) {
        NotesItem noteItem=feed.get(i);
        notesHolder.title.setText(noteItem.getTitle());
        notesHolder.date.setText(noteItem.getDate(mContext));
        if (noteItem.fullView) {
            notesHolder.desc.setVisibility(View.VISIBLE);
            notesHolder.desc.setText(noteItem.getDesc());

        }else{
            notesHolder.desc.setVisibility(View.GONE);
        }

    }

    public void loaddata(String query) {
        boolean firstItem=true;
        boolean notfound=true;
        File[] files=new File(DestDir.get().path).listFiles();
        for(File f: files){
            if (f.isFile()) {
                if (query.length()>0 ){
                    if (findText(query,f) || f.getName().toUpperCase().contains(query.toUpperCase())){
                        notfound=false;
                        if (getItemCount() > 0 && firstItem) clear();
                        addItem(f.getName());
                        firstItem=false;
                    }
                }else {
                    notfound=false;
                    if (getItemCount() > 0 && firstItem) clear();
                    addItem(f.getName());
                    firstItem=false;
                }
            }
        }

        if (notfound) {
            clear();
            }
        else {
            sort();
        }
    }
    private boolean findText(String param, File file) {
        BufferedReader input=null;
        try {
            input= new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line;
            StringBuffer buffer=new StringBuffer();
            while((line=input.readLine())!=null){
                buffer.append(line+System.getProperty("line.separator"));
            }
            if (buffer.toString().toUpperCase().contains(param.toUpperCase()))
                return true;

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if (input!=null) {
                try{
                    input.close();
                }catch(Exception e){
                }
            }
        }
    return false;
    }

    @Override
    public int getItemCount() {
        return (null!=feed?feed.size():0);
    }

    public void addItem(String title) {
        feed.add(0, new NotesItem(title, mContext));
    }

    public void insertItem(String s) {
        File f=new File(DestDir.get().path+"/"+s);
        if (f.exists()){
            int i=1;
            while( (f=new File(DestDir.get().path+"/"+s+"_"+i)).exists()) i++;
            s=s+"_"+i;
        }
        addItem(s);
        notifyItemInserted(0);
    }
    public void deleteItem(int pos) {
        new File(DestDir.get().path+"/"+feed.get(pos).getTitle()).
                renameTo(new File(DestDir.get().path + "/backup/" + feed.get(pos).getTitle()));
        feed.remove(pos);

    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void editItem(int pos){
        Intent i=new Intent(mContext,NoteActivity.class);
        i.putExtra("filename", feed.get(pos).getTitle());
        ((Activity)mContext).startActivityForResult(i,1);
    }
    public void clear() {
        feed.clear();
    }

    public void sort() {
        Collections.sort(feed,new Comparator<NotesItem>(){
            @Override
            public int compare(NotesItem lhs, NotesItem rhs) {
                if (lhs.getDateInt() < rhs.getDateInt()) return 1;
                else
                if (lhs.getDateInt() > rhs.getDateInt()) return -1;
                else return 0;
            }
        });
    }

    public class NotesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView show;
        TextView title;
        TextView desc;
        TextView date;

        public NotesHolder(View itemView) {
            super(itemView);
            this.show= (ImageView) itemView.findViewById(R.id.show);
            show.setOnClickListener(this);
            this.title=(TextView)itemView.findViewById(R.id.title);
            this.desc= (TextView) itemView.findViewById(R.id.desc);
            this.date= (TextView) itemView.findViewById(R.id.date);
            title.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.show:
                    feed.get(getAdapterPosition()).fullView=!feed.get(getAdapterPosition()).fullView;
                    notifyItemChanged(getAdapterPosition());
                    if (feed.get(getAdapterPosition()).fullView){
                        ((ImageView)v).setImageResource(android.R.drawable.ic_menu_more);
                    }else{
                        ((ImageView)v).setImageResource(android.R.drawable.ic_menu_revert);
                    }

                    return;
                case R.id.title:
                    editItem(getAdapterPosition());
                    return;
            }
        }


    }

}

