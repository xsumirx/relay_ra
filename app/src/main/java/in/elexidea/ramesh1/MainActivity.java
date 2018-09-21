package in.elexidea.ramesh1;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingDeque;

public class MainActivity extends Activity implements View.OnClickListener {


    Button[] btn = new Button[16];
    boolean [] state = new boolean[16];
    LinearLayout l1;


    public static Socket socket;//,socketImage;
    private static  int SERVER_PORT = 8000;
    private static  String SERVER_IP = "192.168.4.1";

    //private static  int SERVER_PORT_IMAGE = 8221;
    //private static  String SERVER_IP_IMAGE = "192.168.1.3";

    private boolean connected = false;
    public static boolean isConnecting = false;

    private Thread connectionThread,checkerThread;
    private Thread connectionThreadImage;

    public static boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FullScreen();
        for(int i = 0; i<17; i++)
        {
            try {
                state[i] = false;
                btn[i] = (Button) findViewById(getResId("btn" + String.valueOf(i+1), R.id.class));
                btn[i].setOnClickListener(this);
            }catch (Exception ex){
                Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        // new Thread(new ClientThread()).start();
        connectionThread = new Thread(new ClientThread());
        connectionThread.start();
        //new Thread(new CheckConnection()).start();
        isConnecting = true;
        MyTimerTask myTask = new MyTimerTask();
        Timer myTimer = new Timer();
        myTimer.schedule(myTask, 5000, 7000);


        //connectionThreadImage = new Thread(new ClientThreadImage());
        //connectionThreadImage.start();

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        FullScreen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FullScreen();
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void FullScreen() {
        if(Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if(Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    
    // ----------------- || ----- || ------- || -------- || ------- || -------- || -------- || ------ || ------- || ------- || -------
    // ----------------- || ----- || ------- || -------- || ------- || -------- || -------- || ------ || ------- || ------- || -------
    //
    // Aasish Garg ----- |( ---- |( -------- |( -------- || ---   9873539696 | -----


    public void sendCmd(byte io,byte cmd)
    {
        try {
            if (socket != null) {
                OutputStream out = socket.getOutputStream();
                BufferedOutputStream b = new BufferedOutputStream(out);

                byte[] data = new byte[4];

                data[0] = (byte)'A';
                data[1] = (byte)io;
                data[2] = (byte)cmd;
                data[3] = (byte)'\r';
                b.write(data);
                b.flush();

            }
        }catch (UnknownHostException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendCmdOnOff(boolean isON)
    {
        try {
            if (socket != null) {
                OutputStream out = socket.getOutputStream();
                BufferedOutputStream b = new BufferedOutputStream(out);

                byte[] data = new byte[4];

                data[0] = (byte)'A';
                if(isON) {
                    data[1] = (byte) 90;
                    data[2] = (byte) 49;

                }else
                {
                    data[1] = (byte) 90;
                    data[2] = (byte) 48;

                }
                data[3] = (byte) '\r';
                b.write(data);
                b.flush();


            }
        }catch (UnknownHostException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    public void onClick(View v) {
        //Toast.makeText(this, String.valueOf(v.getId()) + "  " + ((Button)v).getText().toString(), Toast.LENGTH_SHORT).show();
        switch (v.getId())
        {
            case R.id.btn1:
            {

                if(state[0]) {
                    state[0] = false;
                    btn[0].setBackgroundResource(R.drawable.button);
                    //btn[0].setTextColor(getResources().getColor(R.color.black));
                    sendCmdOnOff(false);
                }
                else {
                    state[0] = true;
                    btn[0].setBackgroundResource(R.drawable.buttonon);
                    //btn[0].setTextColor(getResources().getColor(R.color.white));
                    sendCmdOnOff(true);
                }

                break;
            }

            case R.id.btn2:
            {
                sendImage("01");
                if(state[1]) {
                    state[1] = false;
                    btn[1].setBackgroundResource(R.drawable.button);
                    //btn[1].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)48,(byte)48);
                }
                else {
                    state[1] = true;
                    btn[1].setBackgroundResource(R.drawable.buttonon);
                    //btn[1].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)48,(byte)49);
                }

                break;
            }

            case R.id.btn3:
            {

                sendImage("02");
                int x = 2;
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(48 + x - 1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(48 + x-1),(byte)49);
                }

                break;
            }

            case R.id.btn4:
            {

                sendImage("03");
                int x = 3;
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(48 + x-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(48 + x-1),(byte)49);
                }

                break;
            }

            case R.id.btn5:
            {

                int x = 4;
                sendImage("04");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(48 + x-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(48 + x-1),(byte)49);
                }

                break;
            }

            case R.id.btn6:
            {

                int x = 5;
                int cmd = 5;
                sendImage("05A");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(48 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(48 + cmd-1),(byte)49);
                }

                break;
            }

            case R.id.btn7:
            {

                int x = 6;
                int cmd = 5;
                sendImage("05B");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(48 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(48 + cmd-1),(byte)49);
                }

                break;
            }

            case R.id.btn8:
            {

                int x = 7;
                int cmd = 5;
                sendImage("05C");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(48 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(48 + cmd-1),(byte)49);
                }

                break;
            }

            case R.id.btn9:
            {

                int x = 8;
                int cmd = 6;
                sendImage("06");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(48 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(48 + cmd-1),(byte)49);
                }

                break;
            }

            case R.id.btn10:
            {

                int x = 9;
                int cmd = 7;
                sendImage("07");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(48 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(48 + cmd-1),(byte)49);
                }

                break;
            }

            case R.id.btn11:
            {

                int x = 10;
                int cmd = 8;
                sendImage("08");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(48 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(48 + cmd-1),(byte)49);
                }

                break;
            }

            case R.id.btn12:
            {

                int x = 11;
                int cmd = 9;
                sendImage("09");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(48 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(48 + cmd-1),(byte)49);
                }

                break;
            }

            case R.id.btn13:
            {

                int x = 12;
                int cmd = 10;
                sendImage("10");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(48 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(48 + cmd-1),(byte)49);
                }

                break;
            }
            case R.id.btn14:
            {

                int x = 13;
                int cmd = 1;
                sendImage("11A");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(65 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(65 + cmd-1),(byte)49);
                }

                break;
            }

            case R.id.btn15:
            {

                int x = 14;
                int cmd = 1;
                sendImage("11B");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(65 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(65 + cmd-1),(byte)49);
                }

                break;
            }

            case R.id.btn16:
            {

                int x = 15;
                int cmd = 1;
                sendImage("11C");
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(65 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(65 + cmd-1),(byte)49);
                }

                break;
            }

            case R.id.btn17:
            {

                int x = 16;
                int cmd = 2;
                if(state[x]) {

                    state[x] = false;
                    btn[x].setBackgroundResource(R.drawable.button);
                    //btn[x].setTextColor(getResources().getColor(R.color.black));
                    sendCmd((byte)(65 + cmd-1),(byte)48);
                }
                else {
                    state[x] = true;
                    btn[x].setBackgroundResource(R.drawable.buttonon);
                    //btn[x].setTextColor(getResources().getColor(R.color.white));
                    sendCmd((byte)(65 + cmd-1),(byte)49);
                }

                break;
            }

            default:break;
        }

    }





    class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                socket = new Socket(serverAddr, SERVER_PORT);

                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream())),
                        true);
                socket.setKeepAlive(true);
                connected = true;
                char[] s = new char[]{'a','d','m','i','n','\r','\n'};
                //out.print(s);
                //out.flush();
                //out.flush();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            isConnecting = false;
        }
    }


    /*
    class ClientThreadImage implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP_IMAGE);
                socketImage = new Socket(serverAddr, SERVER_PORT_IMAGE);

                PrintWriter out = new PrintWriter(new BufferedWriter(
                        new OutputStreamWriter(socketImage.getOutputStream())),
                        true);
                socketImage.setKeepAlive(true);
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }*/

    class MyTimerTask extends TimerTask {
        // Important : keep TCP connection alive
        public void run() {
            sendMessage("OK");
        }
    }


    class CheckConnection implements Runnable {
        @Override
        public void run() {
            while(true) {
                try {
                    InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                    Socket n = new Socket(serverAddr, SERVER_PORT);
                    n.close();
                    isActive = true;
                } catch (UnknownHostException e1) {
                    isActive = false;
                    e1.printStackTrace();
                } catch (IOException e1) {
                    isActive = false;
                    e1.printStackTrace();
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public void sendImage(String str)
    {
        /*
        try {

            if(socketImage == null)
            {
                return;
            }

            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socketImage.getOutputStream())),
                    true);

            out.println(str);
            //out.flush();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }

    public void sendMessage(String str)  {


        try {

            if(socket == null)
            {
                if(connectionThread.isAlive() == false)
                {
                    isConnecting = true;
                    connectionThread.run();
                }
                else {
                    isConnecting = true;
                    connectionThread.start();
                }
                return;
            }

            /*
            if(isActive == false && !isConnecting)
            {
                if(connectionThread.isAlive() == false )
                {
                    isConnecting = true;
                    connectionThread.run();
                }
                else
                connectionThread.start();
                return;
            }
            */

            isConnecting = false;

            PrintWriter out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);

            out.println(str);
            //out.flush();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}



