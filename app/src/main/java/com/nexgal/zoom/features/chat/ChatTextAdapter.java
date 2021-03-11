package com.nexgal.zoom.features.chat;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ChatTextAdapter extends BaseAdapter {
    private Context context;
    private List<String> chatTextList = new ArrayList<>();

    public ChatTextAdapter(Context context) {
        this.context = context;
    }

    public void addMessage(String message) {
        System.out.println(message);
        this.chatTextList.add(message);
    }

    @Override
    public int getCount() {
        return this.chatTextList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.chatTextList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView message = new TextView(this.context);
        message.setText(this.chatTextList.get(position));
        return message;
    }

    public void updateMessage(List<String> newMessageList) {
        chatTextList = newMessageList;
    }
}
