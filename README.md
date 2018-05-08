
Messenger是一种轻量级的IPC方案，他的底层实现是AIDL，这个可以通过他的构造方法看到：mTarget = IMessenger.Stub.asInterface(target);

1. 服务端进程

创建Service，同时创建Handler并通过它创建Messenger，在service的onBind中返回Messenger底层的Binder。

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
            }
        }
    }



2. 客户端进程

绑定服务端的Service，绑定成功后用服务端返回的IBinder创建Messenger，通过这个Messenger的send方法就可以给服务端发送消息了，发送消息的类型为Message对象。

        private Messenger serviceMessenger;
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Intent intent = new Intent();
            intent.setAction("com.gqq.messengerservice");
            // 5.0以上需要设置，服务端包名
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
    
如果需要服务端可以回应客户端，也需要在客户端创建一个Handler并创建Messenger，并通过Message的replyTo参数传递给服务端。
