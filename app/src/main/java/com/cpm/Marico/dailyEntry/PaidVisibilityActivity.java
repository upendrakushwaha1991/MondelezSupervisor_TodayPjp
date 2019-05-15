package com.cpm.Marico.dailyEntry;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cpm.Marico.R;
import com.cpm.Marico.adapter.SpinnerAdapterView;
import com.cpm.Marico.database.MaricoDatabase;
import com.cpm.Marico.getterSetter.AssetInsertdataGetterSetter;
import com.cpm.Marico.getterSetter.BrandMaster;
import com.cpm.Marico.getterSetter.CategoryMaster;
import com.cpm.Marico.getterSetter.JourneyPlan;
import com.cpm.Marico.getterSetter.MenuMaster;
import com.cpm.Marico.getterSetter.NonExecutionReason;
import com.cpm.Marico.utilities.AlertandMessages;
import com.cpm.Marico.utilities.CommonFunctions;
import com.cpm.Marico.utilities.CommonString;
import com.crashlytics.android.Crashlytics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class PaidVisibilityActivity extends AppCompatActivity {

    List<Integer> checkHeaderArray = new ArrayList<Integer>();
    boolean checkflag = true,category_img=true;
    private MaricoDatabase database;
    Dialog dialog;
    Context context;
    SharedPreferences preferences;
    FloatingActionButton fab;
    boolean flag = true;
    int size = 5;
    Toolbar toolbar;
    String _pathforcheck, _path, str;
    private String username, store_id, visit_date, visit_date_formatted,menu_id="",_pathforcheck1 = "";
    static int child_position = -1,group_position = -1;
    private ArrayList<NonExecutionReason>  reasonData = new ArrayList<>();
    Bitmap bmp, dest;
    JourneyPlan journeyPlan;
    MenuMaster menuMaster;
    ExpandableListView expListView;
    List<BrandMaster> listDataHeader;
    HashMap<BrandMaster, List<BrandMaster>> listDataChild;
    ArrayList<BrandMaster> displayData;
    String Error_Message,reasonId="";

    ExpandableListAdapter listAdapter;
    String[] spinner_list = {"Select", "YES", "NO"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_visibility);

        declaration();
        prepareListData();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        // setting list adapter
        expListView.setAdapter(listAdapter);

        // used for by default open spinner
//        for (int i = 0; i < listAdapter.getGroupCount(); i++)
//            expListView.expandGroup(i);

        expListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastItem = firstVisibleItem + visibleItemCount;

                if (firstVisibleItem == 0) {
                    fab.show();//.setVisibility(View.VISIBLE);
                } else if (lastItem == totalItemCount) {
                    fab.hide();//setVisibility(View.INVISIBLE);
                } else {
                    fab.show();//setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView arg0, int arg1) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
                expListView.clearFocus();
                expListView.invalidateViews();
            }
        });

        // Listview Group click listener
        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                return false;
            }
        });

        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                InputMethodManager inputManager = (InputMethodManager) getApplicationContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getWindow().getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
        });

        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

                InputMethodManager inputManager = (InputMethodManager) getApplicationContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getWindow().getCurrentFocus() != null) {
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    getCurrentFocus().clearFocus();
                }
            }
        });

        // Listview on child click listener
        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                return false;
            }
        });
    }


    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<BrandMaster, List<BrandMaster>>();
        database.open();
        reasonData = database.getNonExecutionReason(menu_id);
        listDataHeader = database.getSavedPaidVisibilityHeaderData(store_id,visit_date);
        if(listDataHeader.size() == 0) {
            listDataHeader = database.getBrandMasterData(journeyPlan);
        }
        if (listDataHeader.size() > 0) {
            // Adding child data
            for (int i = 0; i < listDataHeader.size(); i++) {
                database.open();
                displayData = database.getSavedPaidVisibilityInsertedChildData(listDataHeader.get(i).getBrandId(),store_id,visit_date);
                if(displayData.size() == 0) {
                    displayData = database.getDisplayData(listDataHeader.get(i).getBrandId(),journeyPlan);
                }
                listDataChild.put(listDataHeader.get(i), displayData);
            }
        }
    }



    private void declaration() {
        context = this;
        database = new MaricoDatabase(context);
        database.open();
        if (getIntent().getSerializableExtra(CommonString.TAG_OBJECT) != null && getIntent().getSerializableExtra(CommonString.KEY_MENU_ID) != null) {
            journeyPlan = (JourneyPlan) getIntent().getSerializableExtra(CommonString.TAG_OBJECT);
            menuMaster  = (MenuMaster) getIntent().getSerializableExtra(CommonString.KEY_MENU_ID);
            store_id = String.valueOf(journeyPlan.getStoreId());
            menu_id  = String.valueOf(menuMaster.getMenuId());
        }

        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        visit_date_formatted =  preferences.getString(CommonString.KEY_YYYYMMDD_DATE, "");
        store_id   = String.valueOf(journeyPlan.getStoreId());
        visit_date = String.valueOf(journeyPlan.getVisitDate());
        username   = preferences.getString(CommonString.KEY_USERNAME, null);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Paid Visibility - " + visit_date);

        str = CommonString.FILE_PATH;

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateData(listDataChild, listDataHeader)) {
                    saveData();
                } else {
                    Snackbar.make(expListView, Error_Message, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }


    // @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("MakeMachine", "resultCode: " + resultCode);
        switch (resultCode) {
            case 0:
                Log.i("MakeMachine", "User cancelled");
                break;
            case -1:

                if (_pathforcheck != null && !_pathforcheck.equals("")) {
                    if (new File(str + _pathforcheck).exists()) {
                        bmp = CommonFunctions.convertBitmap(str + _pathforcheck);
                        dest = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system

                        Canvas cs = new Canvas(dest);
                        Paint tPaint = new Paint();
                        tPaint.setTextSize(100);
                        tPaint.setColor(Color.RED);
                        tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        cs.drawBitmap(bmp, 0f, 0f, null);
                        float height = tPaint.measureText("yY");
                        cs.drawText(dateTime, 20f, height + 15f, tPaint);
                        try {
                            dest.compress(Bitmap.CompressFormat.JPEG, 100,
                                    new FileOutputStream(new File(str + _pathforcheck)));
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        try {
                            bmp = CommonFunctions.convertBitmap(str + _pathforcheck);
//                            Bitmap bitmapsimplesize = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / size, bmp.getHeight() / size, true);
//                            bmp.recycle();
                            listDataChild.get(listDataHeader.get(group_position)).get(child_position).setImage1(_pathforcheck);
                            _pathforcheck = "";
                            listAdapter.notifyDataSetChanged();
                            //  expListView.invalidateViews();

                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                        }
                    }
                }

                if (_pathforcheck1 != null && !_pathforcheck1.equals("")) {
                    if (new File(str + _pathforcheck1).exists()) {
                        bmp = BitmapFactory.decodeFile(str + _pathforcheck1);
                        dest = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Bitmap.Config.ARGB_8888);
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
                        String dateTime = sdf.format(Calendar.getInstance().getTime()); // reading local time in the system

                        Canvas cs = new Canvas(dest);
                        Paint tPaint = new Paint();
                        tPaint.setTextSize(100);
                        tPaint.setColor(Color.RED);
                        tPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                        cs.drawBitmap(bmp, 0f, 0f, null);
                        float height = tPaint.measureText("yY");
                        cs.drawText(dateTime, 20f, height + 15f, tPaint);
                        try {
                            dest.compress(Bitmap.CompressFormat.JPEG, 100,
                                    new FileOutputStream(new File(str + _pathforcheck1)));
                        } catch (FileNotFoundException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        try {
                            bmp = CommonFunctions.convertBitmap(str + _pathforcheck1);
//                            Bitmap bitmapsimplesize = Bitmap.createScaledBitmap(bmp, bmp.getWidth() / size, bmp.getHeight() / size, true);
//                            bmp.recycle();
                            listDataChild.get(listDataHeader.get(group_position)).get(child_position).setImage2(_pathforcheck1);
                            // img_str1 = _pathforcheck1;
                            _pathforcheck1 = "";
                            listAdapter.notifyDataSetChanged();
//                            expListView.invalidateViews();

                        } catch (Exception e) {
                            Crashlytics.logException(e);
                            e.printStackTrace();
                        }
                    }
                }

                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {
        private Context _context;
        private List<BrandMaster> _listDataHeader;
        private HashMap<BrandMaster, List<BrandMaster>> _listDataChild;

        public ExpandableListAdapter(Context context, List<BrandMaster> listDataHeader,
                                     HashMap<BrandMaster, List<BrandMaster>> listChildData) {
            this._context = context;
            this._listDataHeader = listDataHeader;
            this._listDataChild = listChildData;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild,
                                 View convertView, final ViewGroup parent) {

            final BrandMaster childText    = (BrandMaster) getChild(groupPosition, childPosition);
            final  BrandMaster headerTitle = (BrandMaster) getGroup(groupPosition);

            ViewHolder holder = null;
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.paid_visibility_child_list, null);
                holder = new ViewHolder();
                holder.paid_visibility = (TextView) convertView.findViewById(R.id.paid_visibility_txt);
                holder.image1    = (ImageView) convertView.findViewById(R.id.img_cam1);
                holder.image2    = (ImageView) convertView.findViewById(R.id.img_cam2);
                holder.image_layout  = (LinearLayout)convertView.findViewById(R.id.image_layout);
                holder.reason_layout = (LinearLayout)convertView.findViewById(R.id.reason_layout);
                holder.child_card_view = (CardView)convertView.findViewById(R.id.child_card_view);
                holder.paid_visibility_exist_spinner = (Spinner)convertView.findViewById(R.id.paid_visibility_exist_spinner);
                holder.reasonSpinner = (Spinner)convertView.findViewById(R.id.promotion_reason);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.paid_visibility.setTypeface(null, Typeface.BOLD);
            holder.paid_visibility.setText(childText.getDisplay());

            holder.image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    child_position = childPosition;
                    group_position = groupPosition;
                    _pathforcheck = store_id + "_" + username.replace(".", "") + "_Paid_Visibility_Image1-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                    _path = CommonString.FILE_PATH + _pathforcheck;
                    //CommonFunctions.startCameraActivity(activity, _path);
                    CommonFunctions.startAnncaCameraActivity(context, _path, null,false);
                }
            });

            holder.image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    child_position = childPosition;
                    group_position = groupPosition;
                    _pathforcheck1 = store_id + "_" + username.replace(".", "") + "_Paid_Visibility_Image2-" + visit_date_formatted + "-" + CommonFunctions.getCurrentTimeHHMMSS() + ".jpg";
                    _path = CommonString.FILE_PATH + _pathforcheck1;
                    //CommonFunctions.startCameraActivity(activity, _path);
                    CommonFunctions.startAnncaCameraActivity(context, _path, null,false);
                }
            });

            SpinnerAdapterView adapter = new SpinnerAdapterView(getApplicationContext(), spinner_list);
            holder.paid_visibility_exist_spinner.setAdapter(adapter);

            final ViewHolder finalHolder = holder;
            holder.paid_visibility_exist_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int itemPos, long l) {
                    if (itemPos == 0) {
                        finalHolder.image_layout.setVisibility(View.GONE);
                        finalHolder.reason_layout.setVisibility(View.GONE);
                        childText.setPresent("");
                        deleteImagesIfExist();
                        deleteReasonDataIfExist();

                    } else if (itemPos == 1) {
                        finalHolder.image_layout.setVisibility(View.VISIBLE);
                        finalHolder.reason_layout.setVisibility(View.GONE);
                        childText.setPresent("1");
                        deleteReasonDataIfExist();
                    } else {
                        finalHolder.image_layout.setVisibility(View.GONE);
                        finalHolder.reason_layout.setVisibility(View.VISIBLE);
                        childText.setPresent("0");
                        deleteImagesIfExist();
                    }
                }

                private void deleteReasonDataIfExist() {
                    if(childText.getReasonId() != 0){
                        finalHolder.reasonSpinner.setSelection(0);
                        childText.setReasonId(0);
                    }
                }

                private void deleteImagesIfExist() {

                    if(!childText.getImage1().equalsIgnoreCase("")){
                        finalHolder.image1.setImageResource(R.mipmap.camera_orange);
                        File file = new File(CommonString.FILE_PATH + childText.getImage1());
                        if (file.exists()) {
                            childText.setImage1("");
                            file.delete();
                        }
                    }

                    if(!childText.getImage2().equalsIgnoreCase("")){
                        finalHolder.image2.setImageResource(R.mipmap.camera_orange);
                        File file = new File(CommonString.FILE_PATH + childText.getImage2());
                        if (file.exists()) {
                            childText.setImage2("");
                            file.delete();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            if( childText.getPresent().equalsIgnoreCase("")){
                holder.paid_visibility_exist_spinner.setSelection(0);
            }else if(childText.getPresent().equalsIgnoreCase("1")){
                holder.paid_visibility_exist_spinner.setSelection(1);
            }else{
                holder.paid_visibility_exist_spinner.setSelection(2);
            }

            if(childText.getImage1().equalsIgnoreCase("")){
                holder.image1.setImageResource(R.mipmap.camera_orange);
            }else{
                holder.image1.setImageResource(R.mipmap.camera_green);
            }

            if(childText.getImage2().equalsIgnoreCase("")){
                holder.image2.setImageResource(R.mipmap.camera_orange);
            }else{
                holder.image2.setImageResource(R.mipmap.camera_green);
            }

            CustomAdapter customAdapter=new CustomAdapter(getApplicationContext(),reasonData);
            holder.reasonSpinner.setAdapter(customAdapter);

            holder.reasonSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int itemPos, long l) {
                    if (itemPos == 0) {
                        childText.setReasonId(0);
                    }  else {
                        reasonId = String.valueOf(reasonData.get(itemPos).getReasonId());
                        childText.setReasonId(Integer.parseInt(reasonId));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            if(childText.getReasonId() != 0){
                for(int i = 0;i<reasonData.size();i++){
                    if(childText.getReasonId() == reasonData.get(i).getReasonId()){
                        holder.reasonSpinner.setSelection(i);
                    }
                }
            }

            if(checkHeaderArray.contains(groupPosition)) {
                if (!checkflag) {
                    holder.child_card_view.setCardBackgroundColor(getResources().getColor(R.color.red));
                }else holder.child_card_view.setCardBackgroundColor(getResources().getColor(R.color.white));
            }else holder.child_card_view.setCardBackgroundColor(getResources().getColor(R.color.white));

            return convertView;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            final  BrandMaster headerTitle = (BrandMaster) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.list_group_paid_visibility, null);
            }

            TextView lblListHeader  = (TextView) convertView.findViewById(R.id.lblListHeader);
            CardView cardView       = (CardView) convertView.findViewById(R.id.card_view);
            LinearLayout groupView  = (LinearLayout)convertView.findViewById(R.id.group_ll_view);

            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle.getBrand());

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expListView.isGroupExpanded(groupPosition)) {
                        expListView.collapseGroup(groupPosition);
                    } else {
                        expListView.expandGroup(groupPosition);
                    }
                }
            });

            if (!checkflag) {
                if (checkHeaderArray.contains(groupPosition)) {
                    groupView.setBackgroundColor(getResources().getColor(R.color.red));
                } else {
                    groupView.setBackgroundColor(getResources().getColor(R.color.ColorPrimaryLight));
                }
            } else {
                groupView.setBackgroundColor(getResources().getColor(R.color.ColorPrimaryLight));
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    public class ViewHolder {
        TextView paid_visibility;
        Spinner paid_visibility_exist_spinner,reasonSpinner;
        ImageView image1,image2;
        CardView child_card_view;
        LinearLayout reason_layout,image_layout;

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isFinishing()) {
            if ( dialog != null) {
                dialog.dismiss();
                dialog = null;
            }
        }
    }


    private class CustomAdapter extends BaseAdapter {

        Context context;
        ArrayList<NonExecutionReason> reasonData;

        public CustomAdapter(Context context, ArrayList<NonExecutionReason> reasonData) {
            this.context = context;
            this.reasonData = reasonData;
        }

        @Override
        public int getCount() {
            return reasonData.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            view = LayoutInflater.from(context).inflate(R.layout.custom_spinner_item,null);
            TextView names = (TextView) view.findViewById(R.id.tv_ans);
            names.setText(reasonData.get(i).getReason());
            return view;
        }
    }


    private void saveData() {
        long i1 = database.insertPaidVisibilityCompleteData(listDataChild,listDataHeader,store_id,menu_id, visit_date,username);
        if (i1 > 0) {
           // AlertandMessages.showToastMsg(PaidVisibilityActivity.this,"Data saved successfully");
            finish();
        } else {
            AlertandMessages.showToastMsg(PaidVisibilityActivity.this,"Data not saved");
        }
    }

    private boolean validateData(HashMap<BrandMaster, List<BrandMaster>> listDataChild, List<BrandMaster> listDataHeader) {
        checkHeaderArray.clear();
        for (int i = 0; i < listDataHeader.size(); i++) {
            for (int j = 0; j < listDataChild.get(listDataHeader.get(i)).size(); j++) {
                if(listDataChild.get(listDataHeader.get(i)).get(j).getPresent().equalsIgnoreCase("")){
                    checkflag = false;
                    Error_Message = getResources().getString(R.string.promotion_present_error);
                    break;
                }else if(listDataChild.get(listDataHeader.get(i)).get(j).getPresent().equalsIgnoreCase("1")){
                    if(listDataChild.get(listDataHeader.get(i)).get(j).getImage1().equalsIgnoreCase("") &&
                            listDataChild.get(listDataHeader.get(i)).get(j).getImage2().equalsIgnoreCase("")) {
                        checkflag = false;
                        Error_Message = getResources().getString(R.string.image_error);
                        break;
                    }else{
                        checkflag = true;
                    }
                } else {
                    if(listDataChild.get(listDataHeader.get(i)).get(j).getReasonId() == 0) {
                        checkflag = false;
                        Error_Message = getResources().getString(R.string.reason_id_error);
                        break;
                    }else{
                        checkflag = true;
                    }
                }
            }

            if (checkflag == false) {
                if (!checkHeaderArray.contains(i)) {
                    checkHeaderArray.add(i);
                }
                break;
            }
        }
        expListView.invalidateViews();
        listAdapter.notifyDataSetChanged();
        return checkflag;
    }


    @Override
    public void onBackPressed() {
        new AlertandMessages(PaidVisibilityActivity.this, null, null, null).backpressedAlert();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // NavUtils.navigateUpFromSameTask(this);
            new AlertandMessages(PaidVisibilityActivity.this, null, null, null).backpressedAlert();
        }
        return super.onOptionsItemSelected(item);
    }

}
