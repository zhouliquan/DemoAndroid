package com.richie.testapp;

import android.Manifest;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.nfc.Tag;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DemoSocketActivity extends AppCompatActivity implements Runnable, View.OnClickListener{


    private static final String TAG = "DemoSocket";

    private static final String[] PERMISSIONS = {
            Manifest.permission.INTERNET,
    };

    private static final int REQUEST_PERMISSIONS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_demo_socket);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, REQUEST_PERMISSIONS);
        }
        Button button = (Button)findViewById(R.id.bt_send);
        button.setOnClickListener(this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                initLocalSocket();
            }
        }).start();

        //send();
//        receiver();
//        startSocket();
    }

    LocalServerSocket lss;
    LocalSocket receiver, sender;
    final static String ADDRESS = "DemoSocket";

    public void initLocalSocket() {
        try {
            lss = new LocalServerSocket(ADDRESS);

            sender = new LocalSocket();
            sender.connect(new LocalSocketAddress(ADDRESS));

            receiver = lss.accept();

            startSocket();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream output = sender.getOutputStream();
                    output.write(20);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void receive(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream input = receiver.getInputStream();
                    int ret = input.read();
                    Log.i(TAG, "ret" + ret);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void startSocket() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        try {
//            while()
            DataInputStream dataInput = new DataInputStream(receiver.getInputStream());

            Log.i(TAG, "get: " + dataInput.readInt());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

//        send();
//        receive();

        DataOutputStream dataOutput = null;
        try {
            dataOutput = new DataOutputStream(sender.getOutputStream());
            dataOutput.writeInt(10);
            dataOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
