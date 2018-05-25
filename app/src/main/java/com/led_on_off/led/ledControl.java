package com.led_on_off.led;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class ledControl extends ActionBarActivity {
   // Button btnOn, btnOff, btnDis;
    Button btnForward, btnBack, btnLeft, btnRight, btnStop;
    ImageButton Discnt;

    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;//
    BluetoothSocket btSocket = null;//new
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_control);

        //call the widgets
        btnForward = (Button)findViewById(R.id.buttonForward);
        btnBack = (Button)findViewById(R.id.buttonBack);
        btnLeft = (Button) findViewById(R.id.buttonLeft);
        btnRight = (Button) findViewById(R.id.buttonRight);
        btnStop = (Button) findViewById(R.id.buttonStop);

        Discnt = (ImageButton)findViewById(R.id.discnt);

        new ConnectBT().execute(); //Call the class to connect

        //commands to be sent to bluetooth
        btnForward.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                goForward();      //method to go forward
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goBack();   //method to go back
            }
        });

        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goLeft();   //method to go left
            }
        });

        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                goRight();   //method to go right
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                stop();   //method to stop
            }
        });
        Discnt.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("Error");}
        }
        finish(); //return to the first layout
    }

    private void goForward()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("F".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error go Forward");
            }
        }
    }

    private void goBack()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("B".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error go Back");
            }
        }
    }

    private void goLeft()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("L".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error go Left");
            }
        }
    }

    private void goRight()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("R".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error go Right");
            }
        }
    }

    private void stop()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("S".toString().getBytes());
            }
            catch (IOException e)
            {
                msg("Error Stop");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_led_control, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(ledControl.this, "Connecting...", "Please wait!!!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                 myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                 BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                 btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                 BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                 btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            }
            else
            {
                msg("Connected.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
