package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Project: Football_Scores-master
 * Package: barqsoft.footballscores.widget
 * Created by Simone Masini on 19/08/2015 at 15.06.
 */
public class ScoresWidgetRemoteViewService extends RemoteViewsService{

    public static final int COL_HOME = 3;
    public static final int COL_AWAY = 4;
    public static final int COL_HOME_GOALS = 6;
    public static final int COL_AWAY_GOALS = 7;
    public static final int COL_ID = 8;
    public static final int COL_MATCHTIME = 2;

    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(final Intent intent) {

        return new RemoteViewsService.RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();

                String[] fragmentdate = new String[1];
                fragmentdate[0] = intent.getStringExtra(getApplicationContext().getString(R.string.extra_date_widget));
                data = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(),
                        null,
                        null,
                        fragmentdate,
                        null);

                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.scores_list_item_widget);

                //prendo i dati da data e li inserisco nel layout

                String homeName = data.getString(COL_HOME);
                int resHome = Utilies.getTeamCrestByTeamName(data.getString(COL_HOME));
                int resAway = Utilies.getTeamCrestByTeamName(data.getString(COL_AWAY));
                String awayName = data.getString(COL_AWAY);
                String scores = Utilies.getScores(data.getInt(COL_HOME_GOALS), data.getInt(COL_AWAY_GOALS));
                String date = data.getString(COL_MATCHTIME);

                views.setImageViewResource(R.id.home_crest, resHome);
                views.setTextViewText(R.id.home_name, homeName);
                views.setTextViewText(R.id.score_textview, scores);
                views.setTextViewText(R.id.data_textview, date);
                views.setImageViewResource(R.id.away_crest, resAway);
                views.setTextViewText(R.id.away_name, awayName);

                setRemoteContentDescription(views, R.id.home_crest, getString(R.string.logo) + " " + homeName);
                setRemoteContentDescription(views, R.id.home_name, homeName);
                setRemoteContentDescription(views, R.id.away_crest, getString(R.string.logo) + " " + awayName);
                setRemoteContentDescription(views, R.id.away_name, awayName);
                setRemoteContentDescription(views, R.id.score_textview, scores);
                setRemoteContentDescription(views, R.id.data_textview, date);

                return views;
            }


            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, int res, String description) {
                views.setContentDescription(res, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.scores_list_item_widget);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(COL_ID);
                return position;
            }



            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
