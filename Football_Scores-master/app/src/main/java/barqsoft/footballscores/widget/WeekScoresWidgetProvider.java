package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Project: Football_Scores-master
 * Package: barqsoft.footballscores.widget
 * Created by Simone Masini on 19/08/2015 at 16.11.
 */
public class WeekScoresWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_DATA_UPDATED = "app.nanodegree.masini.simone.footballscores.ACTION_DATA_UPDATED";
    public static final String ACTION_DATA_NEXT = "app.nanodegree.masini.simone.footballscores.ACTION_DATA_NEXT";

    private long dateInTimes;
    private String date = "";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_week);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Set up the collection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context, views);
            } else {
                setRemoteAdapterV11(context, views);
            }

            views.setTextViewText(R.id.title_widget_week, Utilies.getDayName(context, dateInTimes));

//            this.sendBroadcast(dataUpdatedIntent);

            Intent intentNext = new Intent(WeekScoresWidgetProvider.ACTION_DATA_NEXT)
                    .setPackage(context.getPackageName());
            PendingIntent pendingIntentNext = PendingIntent.getActivity(context, 0, intentNext, 0);
            views.setOnClickPendingIntent(R.id.btn_next, pendingIntentNext);

           /* boolean useDetailActivity = context.getResources()
                    .getBoolean(R.bool.use_detail_activity);
            Intent clickIntentTemplate = useDetailActivity
                    ? new Intent(context, DetailActivity.class)
                    : new Intent(context, MainActivity.class);
            PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);*/

            views.setEmptyView(R.id.listview_scores_widget, R.id.widget_empty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_DATA_UPDATED.equals(intent.getAction())) {
            updateWidget(context);
        }else if(ACTION_DATA_NEXT.equals(intent.getAction())){
            date = changeDate(1);
            updateWidget(context);
        }
    }

    public void updateWidget(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview_scores_widget);
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        Intent intent = new Intent(context, TodayScoresWidgetRemoteViewService.class);
        intent.putExtra("date_widget", getDate());
        views.setRemoteAdapter(R.id.listview_scores_widget, intent);
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        Intent intent = new Intent(context, TodayScoresWidgetRemoteViewService.class);
        intent.putExtra("date_widget", getDate());
        views.setRemoteAdapter(0, R.id.listview_scores_widget,intent);
    }

    public String getDate(){
        if(date.equals("")){
            dateInTimes = System.currentTimeMillis();
            Date date = new Date(dateInTimes);
            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
            this.date = mformat.format(date);
        }
        return date;
    }

    public String changeDate(int i){
        dateInTimes = dateInTimes+((i)*86400000);
        Date date = new Date(dateInTimes);
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
        return mformat.format(date);
    }
}
