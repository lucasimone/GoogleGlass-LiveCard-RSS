package it.telecomitalia.gg.rss;

import it.telecomitalia.gg.rss.datamodel.RSSEvent;
import it.telecomitalia.gg.rss.utils.ResponseListener;

import java.util.List;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.LiveCard.PublishMode;

public class LiveCardService extends Service implements ResponseListener {

	private static final String LIVE_CARD_TAG = "LiveCardDemo";

	private LiveCard mLiveCard;
	private RemoteViews mLiveCardView;
	private TextView title, descr, time;
	

	private final Handler mHandler = new Handler();
	private final UpdateLiveCardRunnable mUpdateLiveCardRunnable = new UpdateLiveCardRunnable();
	private static final long DELAY_MILLIS = 3000;

	@Override
	public void onCreate() {
		super.onCreate();
		//new RSSAsyncTask(this).execute("news");
		Log.d("LUCA", "oncreate service");
	
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d("LUCA", "onstartcmd service");
		if (mLiveCard == null) {
			Log.d("LUCA", "onstartcmd service 1");
			// Get an instance of a live card
			mLiveCard = new LiveCard(this, LIVE_CARD_TAG);
			Log.d("LUCA", "onstartcmd service 2");
			// Inflate a layout into a remote view
			mLiveCardView = new RemoteViews(getPackageName(), R.layout.rss_news);
			
			Log.d("LUCA", "onstartcmd service 3");
			// Set up the live card's action with a pending intent
			// to show a menu when tapped
			Intent menuIntent = new Intent(this, MenuActivity.class);
			menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			mLiveCard.setAction(PendingIntent.getActivity(this, 0, menuIntent, 0));
			Log.d("LUCA", "onstartcmd service 4");
			// Publish the live card
			mLiveCard.publish(PublishMode.REVEAL);
			Log.d("LUCA", "onstartcmd service 5");
			// Queue the update text runnable
			mHandler.post(mUpdateLiveCardRunnable);
			Log.d("LUCA", "onstartcmd service 6");
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		if (mLiveCard != null && mLiveCard.isPublished()) {
			// Stop the handler from queuing more Runnable jobs
			mUpdateLiveCardRunnable.setStop(true);

			mLiveCard.unpublish();
			mLiveCard = null;
		}
		super.onDestroy();
	}

	/**
	 * Runnable that updates live card contents
	 */
	private class UpdateLiveCardRunnable implements Runnable {

		private boolean mIsStopped = false;

		/*
		 * Updates the card with a fake score every 30 seconds as a
		 * demonstration. You also probably want to display something useful in
		 * your live card.
		 * 
		 * If you are executing a long running task to get data to update a live
		 * card(e.g, making a web call), do this in another thread or AsyncTask.
		 */
		public void run() {
			if (!isStopped()) {
				Log.d("LUCA", "run service 1");
//				// Generate fake points.
				if (expoNews!=null){
					if (expoNews.size() == index) index = 0;
					mLiveCardView.setTextViewText(R.id.newsTitle, expoNews.get(index).name);
					mLiveCardView.setTextViewText(R.id.newsDescr, expoNews.get(index).description);
					mLiveCardView.setTextViewText(R.id.newsTimestamp, expoNews.get(index).timestamp);
				} else  {
					mLiveCardView.setTextViewText(R.id.newsTitle, "fake title");
					Log.d("LUCA", "run service 1.5");
				}
				Log.d("LUCA", "run service 2");
				
				//				// Update the remote view with the new scores.
//				mLiveCardView.setTextViewText(R.id.home_score_text_view,
//						String.valueOf(homeScore));
//				mLiveCardView.setTextViewText(R.id.away_score_text_view,
//						String.valueOf(awayScore));
//
//				// Always call setViews() to update the live card's RemoteViews.
//				mLiveCard.setViews(mLiveCardView);
//
//				// Queue another score update in 30 seconds.
				mHandler.postDelayed(mUpdateLiveCardRunnable, DELAY_MILLIS);
				Log.d("LUCA", "run service 3");
			}
		}

		public boolean isStopped() {
			return mIsStopped;
		}

		public void setStop(boolean isStopped) {
			this.mIsStopped = isStopped;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		/*
		 * If you need to set up interprocess communication (activity to a
		 * service, for instance), return a binder object so that the client can
		 * receive and modify data in this service.
		 * 
		 * A typical use is to give a menu activity access to a binder object if
		 * it is trying to change a setting that is managed by the live card
		 * service. The menu activity in this sample does not require any of
		 * these capabilities, so this just returns null.
		 */
		return null;
	}

	
	
	// EXPO NEWS read
	List<RSSEvent> expoNews = null;
	int index = 0;
	
	
	@Override
	public void onSuccess(List<RSSEvent> result) {
		expoNews = result;
		index = 0;
	}

	@Override
	public void onFailure() {
		// TODO Auto-generated method stub
		
	}
}
