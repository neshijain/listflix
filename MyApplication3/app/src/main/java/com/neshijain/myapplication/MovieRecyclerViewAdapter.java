package com.neshijain.myapplication;

import android.content.Context;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created for recycler view
 */

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.MyViewHolder>
        implements Filterable {

    private Context mContext ;
    private List<Movie> mData ;
    private List<Movie> movieListFiltered;
    String searchText;
    public MovieRecyclerViewAdapter(Context mContext, List<Movie> mData) {
        this.mContext = mContext;
        this.mData = mData;
        this.searchText = "";
        this.mData = mData;
        this.movieListFiltered = mData;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view ;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_movie,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.tv_movie_title.setText(movieListFiltered.get(position).getTitle());
        holder.img_movie_thumbnail.setImageResource(mData.get(position).getThumbnail());
        String title = movieListFiltered.get(position).getTitle();
        holder.tv_movie_title.setText(Html.fromHtml(title));
        if(searchText.length()>0){
            // to display searched character in different color
            String  s1 = title.toLowerCase(Locale.US);
            String  s2 = searchText.toLowerCase(Locale.US);
            int index =  s1.indexOf(s2);
            SpannableStringBuilder sb = new SpannableStringBuilder(title);
                while(index>=0){
                sb = new SpannableStringBuilder(title);
                ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(255, 255, 0)); //specify color here
                sb.setSpan(fcs, index, searchText.length()+index, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                s1 = title.toLowerCase(Locale.US);
                s2 = searchText.toLowerCase(Locale.US);
                index = s1.indexOf(s2,index+1);
            }
            holder.tv_movie_title.setText(sb);

        }else{
            holder.tv_movie_title.setText(Html.fromHtml(title));
        }
    }

    @Override
    public int getItemCount() {
        return movieListFiltered.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_movie_title;
        ImageView img_movie_thumbnail;
        CardView cardView ;

        public MyViewHolder(View itemView) {
            super(itemView);
            tv_movie_title = (TextView) itemView.findViewById(R.id.movie_title_id) ;
            img_movie_thumbnail = (ImageView) itemView.findViewById(R.id.movie_img_id);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }


    public int getItemsCount() {
        return movieListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                searchText = charSequence.toString();
                if (charString.isEmpty()) {
                    movieListFiltered = mData;
                } else {
                    List<Movie> filteredList = new ArrayList<>();
                    for (Movie row : mData) {
                        if (row.getTitle().toLowerCase().contains(charString.toLowerCase()) ) {
                            filteredList.add(row);
                        }
                    }
                    movieListFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = movieListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                movieListFiltered = (ArrayList<Movie>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

}
