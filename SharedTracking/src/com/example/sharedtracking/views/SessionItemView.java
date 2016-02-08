package com.example.sharedtracking.views;


import java.util.List;
import java.util.Map;

import com.example.sharedtracking.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class SessionItemView extends SimpleAdapter{
	

	/**static constants*/
	public static final int TYPE_FOLLOWED = 1;
	public static final int TYPE_FOLLOWING = 2;
	
	
	private View root;
	private LinearLayout frame;
	
	private TextView name;
	private TextView startDate;
	private TextView endDate;
	private ImageView sessionType;
	

	public SessionItemView(Context context,
			List<? extends Map<String, ?>> data, int resource, String[] from,
			int[] to) {
		super(context, data, resource, from, to);
		// TODO Auto-generated constructor stub
	}

    private void init(Context context, String name, String start, String end,int type) {
    	 this.root = inflate(context, R.layout.session_item, this);
    	 this.frame = (LinearLayout) root.findViewById(R.id.primary_layout);
    	 this.name = (TextView) root.findViewById(R.id.name);
    	 this.startDate = (TextView) root.findViewById(R.id.start_date);
    	 this.endDate = (TextView) root.findViewById(R.id.end_date);
    	 this.sessionType = (ImageView) root.findViewById(R.id.icon);

    	 this.name.setText(name);
    	 this.startDate.setText(start);
    	 if(end!=null){
    		 this.endDate.setText(end);
    	 }else{
    		 this.endDate.setText(DEFAULT_END_DATE);
    	 }
    	 if(type==SessionItemView.TYPE_FOLLOWED){
    		 this.sessionType.setImageResource(R.drawable.ic_followed);
    	 }else if(type==SessionItemView.TYPE_FOLLOWING){
    		 this.sessionType.setImageResource(R.drawable.ic_following);
    	 }
    	 
    }
    
    public LinearLayout getItemFrame(){
    	return this.frame;
    }
    

}
