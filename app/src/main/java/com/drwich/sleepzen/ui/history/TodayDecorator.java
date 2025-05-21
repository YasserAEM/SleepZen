package com.drwich.sleepzen.ui.history;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.drwich.sleepzen.R;

/**
 * Draws a ring (or background) around today’s date.
 */
public class TodayDecorator implements DayViewDecorator {
    private final CalendarDay today = CalendarDay.today();
    private final Drawable highlightDrawable;

    public TodayDecorator(Context context) {
        // use a drawable resource; e.g. a circle outline
        highlightDrawable = ContextCompat.getDrawable(
                context, R.drawable.bg_today_circle
        );
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return day.equals(today);
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(highlightDrawable);
    }
}
