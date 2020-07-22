package com.neshijain.myapplication;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.SearchManager;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private List<Movie> movieList;
    private MovieRecyclerViewAdapter mAdapter;
    private int totalitems; // total movie items
    private int totalloadeditems; // total loaded movie items from json
    private String nextpagetoload; //next json page to load, json name
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenter.start(getApplication(), "bc98d8ec-1664-49ff-8e11-f17eb1bf8ce1",
                Analytics.class, Crashes.class);
        totalitems = 0;
        totalloadeditems = 0;


        //toolbar settings
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.offwhite));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.toolbar_title);

        //using given back button instead of default

        if(getSupportActionBar()!=null){
            Drawable drawable= getResources().getDrawable(R.drawable.back);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable newdrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 50, 50, true));
            newdrawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(newdrawable);

        }
















        //recyclerview settings
        recyclerView = findViewById(R.id.recycler_view);
        movieList = new ArrayList<>();
        mAdapter = new MovieRecyclerViewAdapter(this,movieList);

        // maintaining changes for the orientation, orientation settins fro recyccler view.
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        }
        else{
            recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        }
        recyclerView.setAdapter(mAdapter);


        //adding scroll listener so that once one pageends we load another json data
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged( RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                   if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == movieList.size() - 1) {
                        //bottom of list!
                        if((totalitems ==0 && totalloadeditems == 0) ||(totalloadeditems < totalitems))
                            fetchMovies(); //fetch movies from json when we scroll page to te end
                    }
            }
        });

        nextpagetoload = "CONTENTLISTINGPAGE-PAGE1.json"; //first json to load
        fetchMovies();  //fetch movie first time when we load the app
    }

    /**
     * fetches json by making calls to asset folder
     */

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open(nextpagetoload);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    private void fetchMovies() {
        String json = null;
        List<Movie> items = new ArrayList<Movie>();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset());
            JSONObject page  = obj.getJSONObject("page");
            JSONObject contentitems  = page.getJSONObject("content-items");
            totalitems=page.getInt("total-content-items"); //set total movie items count
            JSONArray m_jArry = contentitems.getJSONArray("content");

            if(totalloadeditems!=0)
            {
                // this happens becuase i am putting one extra loading card
                // view at the end which gets replaced when the json loads
                // in the following lines for better user experience
                //replacing the last loading card view and filling the next json
                movieList.remove(totalloadeditems);
            }
            for (int i = 0; i < m_jArry.length(); i++) {
                JSONObject jo_inside = m_jArry.getJSONObject(i);
                String title = jo_inside.getString("name");
                String image = jo_inside.getString("poster-image");
                int ii=  image.indexOf(".");
                image = image.substring(0,ii);
                Movie c= new Movie();
                int res = getResources().getIdentifier("com.neshijain.myapplication:drawable/"+image, null, null);
                if(res == 0) {
                    //filling the error image wen movie image is unknown
                    res = getResources().getIdentifier("com.neshijain.myapplication:drawable/" + "errorimage", null, null);
                } System.out.println("res is "+res);
                c.setTitle(title);
                c.setThumbnail(res);
                items.add(c);
            }

            //setting the net page to load
            int pagenumber = page.getInt("page-num");
            pagenumber = pagenumber+1;
            nextpagetoload = "CONTENTLISTINGPAGE-PAGE"+pagenumber+".json";
            totalloadeditems = totalloadeditems + page.getInt("page-size");

            //this one loading card view is added for the user expericne till json loads
            if(totalloadeditems<totalitems)
            {
                Movie c= new Movie();
                c.setTitle("Loading..");
                int  res = getResources().getIdentifier("com.neshijain.myapplication:drawable/" + "placeholder_for_missing_posters", null, null);
                c.setThumbnail(res);
                items.add(c);
            }



        } catch (Exception ex) {
            ex.printStackTrace();

        }

        movieList.addAll(items);
        // refreshing recycler view
        recyclerView.post(new Runnable() {
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
               .getActionView();

        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted

                if(query.length()>=3)
                mAdapter.getFilter().filter(query);
                else
                    {
                        String text = "Please enter 3 or more characters to search";
                        Toast.makeText(MainActivity.this,text,
                                Toast.LENGTH_LONG).show();
                    mAdapter.getFilter().filter("");
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                if(query.length()>=3)
              mAdapter.getFilter().filter(query);
                else
                    mAdapter.getFilter().filter("");
                return false;
            }
        });


        //code is for putting custom search button given
        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        Drawable dr = getResources().getDrawable(R.drawable.search);
        Bitmap bitmap1 = ((BitmapDrawable) dr).getBitmap();
        Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap1, 50, 50, true));
        v.setImageDrawable(d);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

}
