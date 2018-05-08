package com.gqq.messengerdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

public class MessengerService extends Service {

    private Messenger messenger = new Messenger(new MessengerHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return messenger.getBinder();
    }

    private static class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("TAG", "receive message from client:" + msg.getData().getString("msg"));

            // 以下：接收到消息后，返回一条消息给客户端
            Messenger client = msg.replyTo;
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("msg", "消息已收到，稍后回复");
            message.setData(bundle);
            try {
                client.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
}
