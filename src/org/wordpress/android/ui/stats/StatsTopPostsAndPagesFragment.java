package org.wordpress.android.ui.stats;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.wordpress.android.R;
import org.wordpress.android.datasets.StatsTopPostsAndPagesTable;
import org.wordpress.android.providers.StatsContentProvider;
import org.wordpress.android.util.WPLinkMovementMethod;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Fragment for top posts and pages stats. Has two pages, for Today's and Yesterday's stats.
 */
public class StatsTopPostsAndPagesFragment extends StatsAbsPagedViewFragment {
    
    private static final Uri STATS_TOP_POSTS_AND_PAGES_URI = StatsContentProvider.STATS_TOP_POSTS_AND_PAGES_URI;
    private static final StatsTimeframe[] TIMEFRAMES = new StatsTimeframe[] { StatsTimeframe.TODAY, StatsTimeframe.YESTERDAY };

    public static final String TAG = StatsTopPostsAndPagesFragment.class.getSimpleName();

    @Override
    protected FragmentStatePagerAdapter getAdapter() {
        return new CustomPagerAdapter(getChildFragmentManager());
    }

    private class CustomPagerAdapter extends FragmentStatePagerAdapter {

        public CustomPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return getFragment(position);
        }

        @Override
        public int getCount() {
            return TIMEFRAMES.length;
        }
        
        @Override
        public CharSequence getPageTitle(int position) {
            return TIMEFRAMES[position].getLabel(); 
        }

    }

    @Override
    protected Fragment getFragment(int position) {
        int entryLabelResId = R.string.stats_entry_posts_and_pages;
        int totalsLabelResId = R.string.stats_totals_views;
        int emptyLabelResId = R.string.stats_empty_top_posts;
        
        Uri uri = Uri.parse(STATS_TOP_POSTS_AND_PAGES_URI.toString() + "?timeframe=" + TIMEFRAMES[position].name());
        
        StatsCursorFragment fragment = StatsCursorFragment.newInstance(uri, entryLabelResId, totalsLabelResId, emptyLabelResId);
        fragment.setListAdapter(new CustomCursorAdapter(getActivity(), null));
        return fragment;
    }

    private static class ViewHolder {
        private final TextView entryTextView;
        private final TextView totalsTextView;

        ViewHolder(View view) {
            entryTextView = (TextView) view.findViewById(R.id.stats_list_cell_entry);
            totalsTextView = (TextView) view.findViewById(R.id.stats_list_cell_total);
        }
    }
    public class CustomCursorAdapter extends CursorAdapter {
        private final DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance(Locale.getDefault());
        private final LayoutInflater inflater;

        public CustomCursorAdapter(Context context, Cursor c) {
            super(context, c, true);
            inflater = LayoutInflater.from(context);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            final ViewHolder holder = (ViewHolder) view.getTag();

            String entry = cursor.getString(cursor.getColumnIndex(StatsTopPostsAndPagesTable.Columns.TITLE));
            String url = cursor.getString(cursor.getColumnIndex(StatsTopPostsAndPagesTable.Columns.URL));
            int total = cursor.getInt(cursor.getColumnIndex(StatsTopPostsAndPagesTable.Columns.VIEWS));

            // entries
            if (url != null && url.length() > 0) {
                Spanned link = Html.fromHtml("<a href=\"" + url + "\">" + entry + "</a>");
                holder.entryTextView.setText(link);
                holder.entryTextView.setMovementMethod(WPLinkMovementMethod.getInstance());
            } else {
                holder.entryTextView.setText(entry);
            }
            
            // totals
            holder.totalsTextView.setText(formatter.format(total));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup root) {
            View view = inflater.inflate(R.layout.stats_list_cell, root, false);
            view.setTag(new ViewHolder(view));
            return view;
        }
    }

    @Override
    public String getTitle() {
        return getString(R.string.stats_view_top_posts_and_pages);
    }

    @Override
    protected String[] getTabTitles() {
        return StatsTimeframe.toStringArray(TIMEFRAMES);
    }
    
}
