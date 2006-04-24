/*
 * Copyright (c) 2004-2006 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Stefan S. Stratigakos - original qtstalker code
 *     Marco Maccaferri - initial API and implementation
 */

package net.sourceforge.eclipsetrader.charts.indicators;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.PlotLine;
import net.sourceforge.eclipsetrader.charts.Settings;
import net.sourceforge.eclipsetrader.core.db.BarData;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * Lines
 */
public class Lines extends IndicatorPlugin
{
    public static final String DEFAULT_LABEL = "CLOSE";
    public static final int DEFAULT_LINETYPE = PlotLine.LINE;
    public static final RGB DEFAULT_COLOR = new RGB(0, 0, 192);
    public static final int DEFAULT_INPUT = BarData.CLOSE;
    private String label = DEFAULT_LABEL;
    private int input = DEFAULT_INPUT;
    private int lineType = DEFAULT_LINETYPE;
    private Color color = new Color(null, DEFAULT_COLOR);

    public Lines()
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.charts.IndicatorPlugin#calculate()
     */
    public void calculate()
    {
        PlotLine line = new PlotLine(getBarData(), input);
        line.setLabel(label);
        line.setType(PlotLine.LINE);
        line.setColor(color);
        line.setScaleFlag(true);
        getOutput().add(line);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.charts.IndicatorPlugin#setParameters(net.sourceforge.eclipsetrader.charts.Settings)
     */
    public void setParameters(Settings settings)
    {
        label = settings.getString("label", label);
        lineType = settings.getInteger("lineType", lineType).intValue();
        color = settings.getColor("color", color);
        input = settings.getInteger("input", input).intValue();
    }
}
