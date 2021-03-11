package com.nexgal.zoom.features.chat;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;

public class ChatClient {
    private Socket socket;
    private Handler messageHandler;
    public  ChatClient(final Handler messageHandler){
        this.messageHandler = messageHandler;
        try{
            IO.Options options = IO.Options.builder()
                    .setPath("/chat/")
                    .setTransports(new String[]{WebSocket.NAME})
                    .build();
            this.socket = IO.socket("http://192.168.0.12:5000",options);
            this.socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("[WebSocket]","connected");
                }
            });
            this.socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("[WebSocket]",args[0].toString());
                }
            });
            this.socket.on(ChatEvent.GET_MESSAGE, new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Message message = new Message();
                    message.obj = args[0].toString();
                    messageHandler.sendMessage(message);
                }
            });
            this.socket.connect();
        } catch (URISyntaxException e){
            Log.e("[WebSocket]","Failed to initialize");

        }
    }
    public void send(String message){
        this.socket.emit(ChatEvent.NEW_MESSAGE,message);
    }
}
