package com.gqq.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    private Messenger serviceMessenger;
    private Messenger replyMessenger = new Messenger(new MessengerHandle());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = new Intent();
        intent.setAction("com.gqq.messengerservice");
        // 服务端包名
        intent.setPackage("com.gqq.messengerdemo");
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessenger = new Messenger(service);
            Message message = Message.obtain();
            Bundle bundle = new Bundle();
            bundle.putString("msg", "hello, I am client");
            message.setData(bundle);
            // 将用于接收的Messenger传递给服务端
            message.replyTo = replyMessenger;
            try {
                serviceMessenger.send(message);
            } catch (RemoteException e) {
                Log.i("TAG", e.getMessage());
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    // 用于接收服务端返回的数据
    private static class MessengerHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.i("TAG", "reply from service:"+msg.getData().getString("msg"));
        }
    }
}
