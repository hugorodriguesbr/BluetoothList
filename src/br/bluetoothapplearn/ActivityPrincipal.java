package br.bluetoothapplearn;

import java.util.Set;


import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class ActivityPrincipal extends Activity {

	private static final int REQUEST_ENABLE_BT = 1;

	ListView listDevicesFound;
	Button btnScanDevice, btnList;
	TextView stateBluetooth;
	BluetoothAdapter bluetoothAdapter;
	String[] listaAUx;
	ArrayAdapter<String> btArrayAdapter;

	/** Called when the activity is first created. */


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_principal);

		btnScanDevice = (Button)findViewById(R.id.scandevice);
		btnList = (Button) findViewById(R.id.button1);

		stateBluetooth = (TextView)findViewById(R.id.bluetoothstate);
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		//listDevicesFound = (ListView)findViewById(R.id.devicesfound);
		btArrayAdapter = new ArrayAdapter<String>(ActivityPrincipal.this, android.R.layout.simple_list_item_1);
		//listDevicesFound.setAdapter(btArrayAdapter);

		CheckBlueToothState();

		btnScanDevice.setOnClickListener(btnScanDeviceOnClickListener);

		registerReceiver(ActionFoundReceiver, 
				new IntentFilter(BluetoothDevice.ACTION_FOUND));

		btnList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog.Builder mensagem = new AlertDialog.Builder(
						ActivityPrincipal.this);
				int lengt = btArrayAdapter.getCount();
				if(lengt > 0){
					listaAUx = new String[lengt];
					for(int i = 0 ; i < lengt ; i++){
						listaAUx[i] =  btArrayAdapter.getItem(i);
					}
					mensagem.setTitle("Escolha um item");
					mensagem.setItems(listaAUx, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int item) {
							mensagem.setTitle("Atenção, você escolheu:");
							mensagem.setMessage(listaAUx[item]);
							mensagem.setNeutralButton("OK", null);
							mensagem.show();					
						}
					});
					mensagem.show();
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(ActionFoundReceiver);
	}

	private void CheckBlueToothState(){
		if (bluetoothAdapter == null){
			stateBluetooth.setText("Bluetooth NOT support");
		}else{
			if (bluetoothAdapter.isEnabled()){
				if(bluetoothAdapter.isDiscovering()){
					stateBluetooth.setText("Bluetooth is currently in device discovery process.");
				}else{
					stateBluetooth.setText("Bluetooth is Enabled.");
					btnScanDevice.setEnabled(true);
				}
			}else{
				stateBluetooth.setText("Bluetooth is NOT Enabled!");
				Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			}
		}
	}


	private Button.OnClickListener btnScanDeviceOnClickListener
	= new Button.OnClickListener(){

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			btArrayAdapter.clear();
			bluetoothAdapter.startDiscovery();
		}};

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			if(requestCode == REQUEST_ENABLE_BT){
				CheckBlueToothState();
			}
		}

		private final BroadcastReceiver ActionFoundReceiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				String action = intent.getAction();
				if(BluetoothDevice.ACTION_FOUND.equals(action)) {
					BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
					btArrayAdapter.add(device.getName() + "\n" + device.getAddress());
					btArrayAdapter.notifyDataSetChanged();
				}
			}};


			@Override
			public boolean onCreateOptionsMenu(Menu menu) {
				getMenuInflater().inflate(R.menu.activity_principal, menu);
				return true;
			}
}
