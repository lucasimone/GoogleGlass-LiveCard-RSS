package it.telecomitalia.gg.rss.utils;

import it.telecomitalia.gg.rss.datamodel.RSSEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.os.AsyncTask;

public class RSSAsyncTask  extends AsyncTask<String, Double, Boolean>{

	public static final String RSS_URL = "https://news.google.it/news/feeds?output=rss";
	
	
	List<RSSEvent> news;
    
    ResponseListener listener;
    
	public RSSAsyncTask(ResponseListener listener) {
		this.listener = listener;
	}

	private String giornale;
	
	@Override
	protected Boolean doInBackground(String... params) {
		
		// Initializing instance variables
		news = new ArrayList<RSSEvent>();
	
		try {
		    URL url = new URL(RSS_URL);
		 
		    XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		    factory.setNamespaceAware(false);
		    XmlPullParser xpp = factory.newPullParser();
		 
		        // We will get the XML from an input stream
		    xpp.setInput(getInputStream(url), "UTF_8");
		 
		        /* We will parse the XML content looking for the "<title>" tag which appears inside the "<item>" tag.
		         * However, we should take in consideration that the rss feed name also is enclosed in a "<title>" tag.
		         * As we know, every feed begins with these lines: "<channel><title>Feed_Name</title>...."
		         * so we should skip the "<title>" tag which is a child of "<channel>" tag,
		         * and take in consideration only "<title>" tag which is a child of "<item>"
		         *
		         * In order to achieve this, we will make use of a boolean variable.
		         */
		    boolean insideItem = false;
		 
		        // Returns the type of current event: START_TAG, END_TAG, etc..
		    int eventType = xpp.getEventType();
		    RSSEvent evt = null;
		    while (eventType != XmlPullParser.END_DOCUMENT) {
		        if (eventType == XmlPullParser.START_TAG) {
		 
		        	
		            if (xpp.getName().equalsIgnoreCase("item")) {
		            	evt = new RSSEvent();
		                insideItem = true;
		            } else if (xpp.getName().equalsIgnoreCase("title")) {
		                if (insideItem)
		                    evt.name = xpp.nextText(); //extract the headline
		            } 
		            else if (xpp.getName().equalsIgnoreCase("description")) {
		                if (insideItem)
		                    evt.description = xpp.nextText().replace("<br/>", "").replace("<b>", ""); //extract the link of article
		            }
		            else if (xpp.getName().equalsIgnoreCase("link")) {
		                if (insideItem)
		                    evt.link = xpp.nextText(); //extract the link of article
		            }
		            else if (xpp.getName().equalsIgnoreCase("pubDate")) {
		                if (insideItem)
		                    evt.timestamp = xpp.nextText(); //extract the link of article
		            }
		        }
		        else if(eventType==XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
		            insideItem=false;
		            news.add(evt);
		        }
		 
		        eventType = xpp.next(); //move to next element
		    }
		 
		} catch (MalformedURLException e) {
		    e.printStackTrace();
		} catch (XmlPullParserException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		return (news.size()>0);
		
	}
	
	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		if (result){
			
			listener.onSuccess(news);
		}
		else{
			
			
		}
	
	}
	
	
	public InputStream getInputStream(URL url) {
		   try {
		       return url.openConnection().getInputStream();
		   } catch (IOException e) {
		       return null;
		     }
		}

}
