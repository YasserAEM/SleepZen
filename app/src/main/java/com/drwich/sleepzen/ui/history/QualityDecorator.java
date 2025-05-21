// app/src/main/java/com/drwich/sleepzen/ui/history/QualityDecorator.java
package com.drwich.sleepzen.ui.history;

import android.graphics.Color;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

/**
 * Draws a colored dot beneath the date to indicate sleep quality:
 *   • quality == 2 → green
 *   • quality == 1 → yellow
 *   • quality == 0 → red
 */
public class QualityDecorator implements DayViewDecorator {
    private final CalendarDay date;
    private final int color;

    public QualityDecorator(CalendarDay date, int quality) {
        this.date = date;
        // map quality to a color
        switch (quality) {
            case 2:  color = Color.parseColor("#4CAF50"); break; // green
            case 1:  color = Color.parseColor("#FFEB3B"); break; // yellow
            default: color = Color.parseColor("#F44336"); break; // red
        }
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(date);
    }

    @Override
    public void decorate(DayViewFacade view) {
        // DotSpan(radius, color)
        view.addSpan(new DotSpan(8, color));
    }
}
