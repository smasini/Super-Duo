package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
    public static final String ACTION_DATA_PREV = "app.nanodegree.masini.simone.footballscores.ACTION_DATA_PREV";

    private long dateInTimes;
    private String date = "";

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = generateRemoteViews(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_DATA_UPDATED.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview_scores_widget);
        }else if(ACTION_DATA_NEXT.equals(intent.getAction())){
            Bundle extra = intent.getBundleExtra(context.getString(R.string.extra_bundle_action_key));
            dateInTimes = extra.getLong(context.getString(R.string.extra_long_date_in_mills_key), 0);
            changeDate(1, context);
            updateWidget(context);
        }else if(ACTION_DATA_PREV.equals(intent.getAction())){
            Bundle extra = intent.getBundleExtra(context.getString(R.string.extra_bundle_action_key));
            dateInTimes = extra.getLong(context.getString(R.string.extra_long_date_in_mills_key), 0);
            changeDate(-1, context);
            updateWidget(context);
        }
    }

    public RemoteViews generateRemoteViews(Context context, int appWidgetId){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_week);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            setRemoteAdapter(context, views);
        } else {
            setRemoteAdapterV11(context, views);
        }

        views.setTextViewText(R.id.title_widget_week, Utilies.getDayName(context, dateInTimes));
        views.setEmptyView(R.id.listview_scores_widget, R.id.widget_empty);

        Bundle extras = new Bundle();
        extras.putLong(context.getString(R.string.extra_long_date_in_mills_key), dateInTimes);

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(context.getString(R.string.extra_current_fragment_key), dateInTimes);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId + 1, intent, 0);
        views.setOnClickPendingIntent(R.id.widget, pendingIntent);

        Intent intentNext = new Intent(WeekScoresWidgetProvider.ACTION_DATA_NEXT)
                .putExtra(context.getString(R.string.extra_bundle_action_key), extras);
        PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, appWidgetId, intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_next, pendingIntentNext);

        Intent intentPrev = new Intent(WeekScoresWidgetProvider.ACTION_DATA_PREV)
                .putExtra(context.getString(R.string.extra_bundle_action_key), extras);
        PendingIntent pendingIntentPrev = PendingIntent.getBroadcast(context, appWidgetId, intentPrev, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.btn_prev, pendingIntentPrev);

        return views;
    }

    public void updateWidget(Context context){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));

        for (int appWidgetId : appWidgetIds) {
            appWidgetManager.updateAppWidget(appWidgetId, null);
            RemoteViews views = generateRemoteViews(context, appWidgetId);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.listview_scores_widget);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        Intent intent = new Intent(context, ScoresWidgetRemoteViewService.class);
        intent.putExtra(context.getString(R.string.extra_date_widget), getStringDate(context));
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.listview_scores_widget, intent);
    }

    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        Intent intent = new Intent(context, ScoresWidgetRemoteViewService.class);
        intent.putExtra(context.getString(R.string.extra_date_widget), getStringDate(context));
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(0, R.id.listview_scores_widget,intent);
    }

    public String getStringDate(Context context){
        if(date.equals("")){
            dateInTimes = System.currentTimeMillis();
            getDate(dateInTimes, context);
        }
        return date;
    }

    public void changeDate(int i, Context context){
        dateInTimes = dateInTimes+((i)*86400000);
        getDate(dateInTimes, context);
        getStringDate(context);
    }

    public void getDate(long dateInTimes, Context context){
        Date date = new Date(dateInTimes);
        this.dateInTimes = date.getTime();
        SimpleDateFormat mformat = new SimpleDateFormat(context.getString(R.string.format_date_only));
        this.date = mformat.format(date);
    }
}
