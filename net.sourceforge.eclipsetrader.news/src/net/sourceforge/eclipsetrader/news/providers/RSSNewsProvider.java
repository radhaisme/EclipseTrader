/*
 * Copyright (c) 2004-2006 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package net.sourceforge.eclipsetrader.news.providers;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import net.sourceforge.eclipsetrader.core.CorePlugin;
import net.sourceforge.eclipsetrader.core.INewsProvider;
import net.sourceforge.eclipsetrader.core.db.NewsItem;
import net.sourceforge.eclipsetrader.core.db.Security;
import net.sourceforge.eclipsetrader.news.NewsPlugin;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;

public class RSSNewsProvider implements Runnable, INewsProvider
{
    private Thread thread;
    private boolean stopping = false;
    private FeedFetcherCache feedInfoCache = HashMapFeedInfoCache.getInstance();
    private FeedFetcher fetcher = new HttpURLFeedFetcher(feedInfoCache);

    public RSSNewsProvider()
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.news.INewsProvider#subscribe(net.sourceforge.eclipsetrader.core.db.Security)
     */
    public void subscribe(Security security)
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.news.INewsProvider#unSubscribe(net.sourceforge.eclipsetrader.core.db.Security)
     */
    public void unSubscribe(Security security)
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.news.INewsProvider#start()
     */
    public void start()
    {
        if (thread == null)
        {
            stopping = false;
            thread = new Thread(this);
            thread.start();
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.news.INewsProvider#stop()
     */
    public void stop()
    {
        stopping = true;
        if (thread != null)
        {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = null;
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.news.INewsProvider#snapshot()
     */
    public void snapshot()
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.news.INewsProvider#snapshot(net.sourceforge.eclipsetrader.core.db.Security)
     */
    public void snapshot(Security security)
    {
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#run()
     */
    public void run()
    {
        long nextRun = System.currentTimeMillis() + 2 * 1000;

        while(!stopping)
        {
            if (System.currentTimeMillis() >= nextRun)
            {
                update();
                int interval = NewsPlugin.getDefault().getPreferenceStore().getInt(NewsPlugin.PREFS_UPDATE_INTERVAL);
                nextRun = System.currentTimeMillis() + interval * 60 * 1000;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
        
        thread = null;
    }

    private void update()
    {
        try {
        } catch(Exception e) {
            CorePlugin.logException(e);
        }
    }

    private void update(URL feedUrl)
    {
        try {
            SyndFeed feed = fetcher.retrieveFeed(feedUrl);
            for (Iterator iter = feed.getEntries().iterator(); iter.hasNext(); )
            {
                SyndEntry entry = (SyndEntry) iter.next();
                
                NewsItem news = new NewsItem();
                Date entryDate = entry.getPublishedDate();
                if (entry.getUpdatedDate() != null)
                    entryDate = entry.getUpdatedDate();
                if (entryDate != null)
                {
                    Calendar date = Calendar.getInstance();
                    date.setTime(entryDate);
                    date.set(Calendar.SECOND, 0);
                    news.setDate(date.getTime());
                }
                news.setTitle(entry.getTitle());
                news.setUrl(entry.getLink());
                CorePlugin.getRepository().save(news);
            }
        } catch(Exception e) {
            CorePlugin.logException(e);
        }
    }
}