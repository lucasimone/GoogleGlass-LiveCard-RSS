package it.telecomitalia.gg.rss.utils;

import it.telecomitalia.gg.rss.datamodel.RSSEvent;

import java.util.List;

public interface ResponseListener {
	
	public void onSuccess(List<RSSEvent> result);
	
	public void onFailure();
}
