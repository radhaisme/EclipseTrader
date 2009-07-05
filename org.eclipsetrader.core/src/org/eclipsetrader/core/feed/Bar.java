/*
 * Copyright (c) 2004-2009 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package org.eclipsetrader.core.feed;

import java.io.Serializable;
import java.util.Date;

/**
 * Default implementation of the <code>IBar</code> interface.
 *
 * @since 1.0
 * @see org.eclipsetrader.core.feed.IBar
 */
public class Bar implements IBar, Serializable {
	private static final long serialVersionUID = -2696239765800738013L;

	private Date date;
	private TimeSpan timeSpan;
	private Double open;
	private Double high;
	private Double low;
	private Double close;
	private Long volume;

	public Bar(Date date, TimeSpan timeSpan, Double open, Double high, Double low, Double close, Long volume) {
		this.date = date;
		this.timeSpan = timeSpan;
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IOHLC#getDate()
	 */
	public Date getDate() {
		return date;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IOHLC#getTimeSpan()
	 */
	public TimeSpan getTimeSpan() {
		return timeSpan;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IOHLC#getOpen()
	 */
	public Double getOpen() {
		return open;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IOHLC#getHigh()
	 */
	public Double getHigh() {
		return high;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IOHLC#getLow()
	 */
	public Double getLow() {
		return low;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IOHLC#getClose()
	 */
	public Double getClose() {
		return close;
	}

	/* (non-Javadoc)
	 * @see org.eclipsetrader.core.feed.IOHLC#getVolume()
	 */
	public Long getVolume() {
		return volume;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof IBar))
			return false;
		IBar other = (IBar) obj;
		return (getDate() == other.getDate() || (getDate() != null && getDate().equals(other.getDate()))) &&
		       (getTimeSpan() == other.getTimeSpan() || (getTimeSpan() != null && getTimeSpan().equals(other.getTimeSpan()))) &&
		       (getOpen() == other.getOpen() || (getOpen() != null && getOpen().equals(other.getOpen()))) &&
		       (getHigh() == other.getHigh() || (getHigh() != null && getHigh().equals(other.getHigh()))) &&
		       (getLow() == other.getLow() || (getLow() != null && getLow().equals(other.getLow()))) &&
		       (getClose() == other.getClose() || (getClose() != null && getClose().equals(other.getClose()))) &&
		       (getVolume() == other.getVolume() || (getVolume() != null && getVolume().equals(other.getVolume())));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (3 * (date != null ? date.hashCode() : 0)) +
		       (7 * (timeSpan != null ? timeSpan.hashCode() : 0)) + (11 * (open != null ? open.hashCode() : 0)) +
		       (13 * (high != null ? high.hashCode() : 0)) + (17 * (low != null ? low.hashCode() : 0)) +
		       (19 * (close != null ? close.hashCode() : 0)) + (23 * (volume != null ? volume.hashCode() : 0));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "[" +
		       date + "TS=" + timeSpan.toString() + " O=" + open + " H=" + high + " L=" + low + " C=" + close + " V=" + volume +
		       "]";
	}
}
