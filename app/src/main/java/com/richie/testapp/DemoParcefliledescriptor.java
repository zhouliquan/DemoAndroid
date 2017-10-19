package com.richie.testapp;

import android.Manifest;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DemoParcefliledescriptor extends AppCompatActivity {

    private static final String TAG = "DemoPFD";

    private static final String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private static final int PERMISSIONS_CODE = 3006;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(PERMISSIONS, PERMISSIONS_CODE);
        }

        FileOutputStream outputStream = new FileOutputStream(getStreamFd());
        try {
            outputStream.write(99);
            outputStream.write(98);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private FileDescriptor getStreamFd() {
        ParcelFileDescriptor[] pipes = null;

        try {
            pipes = ParcelFileDescriptor.createPipe();
            new TransferThread(new ParcelFileDescriptor.AutoCloseInputStream(pipes[0])).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pipes[1].getFileDescriptor();
    }

    static class TransferThread extends Thread {
        InputStream in;
        FileOutputStream out;

        TransferThread(InputStream in, FileOutputStream out) {
            this.in = in;
            this.out = out;
        }

        TransferThread(InputStream in) {
            this.in = in;

            File outFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/zlq_pdf");
            Log.i(TAG, "File: " + outFile.getAbsolutePath());

            try {
                out = new FileOutputStream(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buf = new byte[1024*2];

            int len;
            try {
                while((len=in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                    Log.i(TAG, "out:" + len);

                }
                Log.i(TAG, "Pipe stop");

                in.close();
                out.flush();
                out.getFD().sync();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
