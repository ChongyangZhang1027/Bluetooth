package com.runoob.bluetooth;

import java.util.ArrayList;
import java.util.Set;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private Button On, Off, Visible, list, Scan;
    private BluetoothAdapter BA;
    BluetoothAdapter blueadapter = BluetoothAdapter.getDefaultAdapter();
    private Set<BluetoothDevice>pairedDevices;
    public BluetoothDevice ScanDevices;
    private ListView lv;
    public ArrayList scan_list = new ArrayList();
    public ArrayAdapter scan_adapter;
    public String action;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        On = findViewById(R.id.button1);
        Off = findViewById(R.id.button2);
        Scan = findViewById(R.id.button3);
        Visible = findViewById(R.id.button4);
        list = findViewById(R.id.button5);
        lv = findViewById(R.id.listView1);
        BA = BluetoothAdapter.getDefaultAdapter();
        Toast.makeText(getApplicationContext(),"ZCY-OTZ", Toast.LENGTH_SHORT).show();
    }

    //打开蓝牙
    public void on(View view){
        //判断是否已经打开
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(),"Turned on"
                    ,Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    //列出已经配对过的设备，即本地记录
    public void list(View view){
        lv.setAdapter(null);
        pairedDevices = BA.getBondedDevices();

        ArrayList list = new ArrayList();
        for(BluetoothDevice bt : pairedDevices)
            list.add(bt.getName());

        Toast.makeText(getApplicationContext(),"Showing Paired Devices",
                Toast.LENGTH_SHORT).show();
        final ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, list);
        lv.setAdapter(adapter);

    }

    //建立广播
    class foundReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            //Toast.makeText(getApplicationContext(), "nmsl",Toast.LENGTH_LONG).show();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                ScanDevices = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                //for(BluetoothDevice bt : ScanDevices) {

                    String deviceName = ScanDevices.getName();
                    short rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI);
                    //当RSSI过小时不展示
                    if(rssi>-80){
                    scan_list.add(deviceName+" : "+rssi);}

                scan_adapter.notifyDataSetChanged();//更新list中的值
                Toast.makeText(getApplicationContext(), "Scanning...",Toast.LENGTH_LONG).show();
            }
            else{
                //扫描无结果
                Toast.makeText(getApplicationContext(), "No Available Devices",Toast.LENGTH_LONG).show();
            }
        }
    }

    //开启扫描前判断蓝牙是否可用
    public void doDiscovery() {
        if(BA.isEnabled()){
            BA.startDiscovery();
        }else{
            //Toast.makeText(getApplicationContext(), "蓝牙未打开",Toast.LENGTH_LONG).show();
        }
    }

    //扫描可见设备
    public void scan(View view){
        //清空原来list中内容
        lv.setAdapter(null);
        //开始扫描
        doDiscovery();
        scan_adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, scan_list);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        foundReceiver receiver = new foundReceiver();
        this.registerReceiver(receiver, filter);
        Toast.makeText(getApplicationContext(),"Showing Available Devices",
                Toast.LENGTH_SHORT).show();
        lv.setAdapter(scan_adapter);
    }

    //关闭蓝牙
    public void off(View view){
        BA.disable();
        Toast.makeText(getApplicationContext(),"Turned off" ,
                Toast.LENGTH_LONG).show();
    }

    //设备对外界可见
    public void visible(View view){
        Toast.makeText(getApplicationContext(), "Your device is visible",Toast.LENGTH_LONG).show();
        Intent getVisible = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(getVisible, 0);
    }

    //销毁
    /*@Override
    void onDestroy(){
        super.onDestroy();//解除注册
        unregisterReceiver(receiver);
    }*/
}