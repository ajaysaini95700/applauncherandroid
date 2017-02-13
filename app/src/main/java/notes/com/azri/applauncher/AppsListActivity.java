package notes.com.azri.applauncher;

/**
 * Created by azri on 13/12/16.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import android.text.Editable;
import android.text.TextWatcher;

public class AppsListActivity extends Activity {
    private PackageManager manager;
    private ListView listView;
    EditText inputSearch;
    private MyAppAdapter myAppAdapter;
    private ArrayList<Post> postArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps_list);
        inputSearch = (EditText) findViewById(R.id.inputSearch);
        listView = (ListView)findViewById(R.id.apps_list);
        loadApps();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
                Intent i = manager.getLaunchIntentForPackage(postArrayList.get(pos).getPostSubTitle().toString());
                AppsListActivity.this.startActivity(i);
            }
        });
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                System.out.println("cs" +cs);
                myAppAdapter.filter(cs.toString().trim());
                listView.invalidate();
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void loadApps(){
        manager = getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);
        postArrayList=new ArrayList<>();
        for(ResolveInfo ri:availableActivities){
            postArrayList.add(new Post(ri.loadLabel(manager), ri.activityInfo.packageName , ri.activityInfo.loadIcon(manager)));
        }
        Collections.sort(postArrayList, new Comparator<Post>(){
            public int compare(Post obj1, Post obj2)
            {
                // TODO Auto-generated method stub
                return obj1.getPostTitle().toString().compareToIgnoreCase(obj2.getPostTitle().toString());
            }
        });
        myAppAdapter=new MyAppAdapter(postArrayList,AppsListActivity.this);
        listView.setAdapter(myAppAdapter);
        System.out.println("apps : " + listView);
    }

    public class MyAppAdapter extends BaseAdapter {

        public List<Post> parkingList;
        public Context context;
        ArrayList<Post> arraylist;

        private MyAppAdapter(List<Post> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
            arraylist = new ArrayList<Post>();
            arraylist.addAll(parkingList);

        }

        @Override
        public int getCount() {
            return parkingList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.list_item, null);
            }

            ImageView appIcon = (ImageView)convertView.findViewById(R.id.item_app_icon);
            appIcon.setImageDrawable(parkingList.get(position).getIcon());
            TextView appLabel = (TextView)convertView.findViewById(R.id.item_app_label);
            appLabel.setText(parkingList.get(position).getPostTitle());
            //TextView appName = (TextView)convertView.findViewById(R.id.item_app_name);
            //appName.setText(parkingList.get(position).getPostTitle());
            return convertView;
        }

        public void filter(String charText) {
            charText = charText.toLowerCase(Locale.getDefault());
            parkingList.clear();
            if (charText.length() == 0) {
                parkingList.addAll(arraylist);
            } else {
                for (Post postDetail : arraylist) {
                    if (charText.length() != 0 && postDetail.getPostTitle().toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                        parkingList.add(postDetail);
                    }
                    else if (charText.length() != 0 && postDetail.getPostSubTitle().toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                        parkingList.add(postDetail);
                    }
                }
            }
            notifyDataSetChanged();
        }
    }

}
