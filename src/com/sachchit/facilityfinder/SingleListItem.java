package com.sachchit.facilityfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class SingleListItem extends Activity{
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      
        //full screen
      		requestWindowFeature(Window.FEATURE_NO_TITLE);
      		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
      				WindowManager.LayoutParams.FLAG_FULLSCREEN);
      		
      		
        this.setContentView(R.layout.single_list_item_view);
        
        TextView hosp_name = (TextView) findViewById(R.id.hospname);
        //TextView hosp_name = (TextView) findViewById(R.id.hospname);
        TextView hosp_add = (TextView) findViewById(R.id.haddress);
        TextView contact = (TextView) findViewById(R.id.contact);
        ImageButton mapbutton=(ImageButton)findViewById(R.id.mpb);
        final Intent i = getIntent();
        final double latt22=i.getDoubleExtra("latt2", 0.00);
        final double longg22=i.getDoubleExtra("longg2", 0.00);
        mapbutton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i1 = new Intent(getApplicationContext(), MainActivity.class);
		        i1.putExtra("lattt22", latt22);
				i1.putExtra("longgg22", longg22);
				startActivity(i1);
			}
		});
        
        
        // getting attached intent data
        //int ho=i.getIntExtra("hosp", 0);
        //String host=Integer.toString(ho);
        //double latt22=i.getDoubleExtra("latt2", 0.00);
        //double longg22=i.getDoubleExtra("longg2", 0.00);
       // String hos_name=i.getStringExtra("latt2");
        String hos_name=i.getStringExtra("hospname");
        String htits=i.getStringExtra("htit");
        String hadds=i.getStringExtra("hadd");
        String cons=i.getStringExtra("con");
        
        //int product = i.getIntExtra(product, 0);
        // displaying selected product name
        hosp_name.setText(hos_name+" "+htits);
        //hosp_name.setText(hos_name+" "+htits);
        hosp_add.setText("Address :\n"+hadds);
        contact.setText("Contact :\n"+cons);
        
	}
}
