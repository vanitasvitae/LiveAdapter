package com.cilenco.liveadapter.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.AttrRes;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;

import com.cilenco.liveadapter.R;

public class UiUtils {

    /**
     * helper to get the system default selectable background inclusive an active state
     *
     * @param ctx            the context
     * @param animate        true if you want to fade over the states (only animates if API newer than Build.VERSION_CODES.HONEYCOMB)
     * @return the StateListDrawable
     */
    public static StateListDrawable getSelectableBackground(Context ctx, boolean animate) {
        StateListDrawable states = new StateListDrawable();

        int selectedItemColor = getThemeResource(ctx, R.attr.selectedItemColor);
        int systemBackground = getThemeResource(ctx, R.attr.selectableItemBackground);

        ColorDrawable clrActive = new ColorDrawable(Color.RED);
        states.addState(new int[]{android.R.attr.state_selected}, clrActive);
        states.addState(new int[]{}, ContextCompat.getDrawable(ctx, systemBackground));

        if (animate) { //if possible we enable animating across states
            int duration = ctx.getResources().getInteger(android.R.integer.config_shortAnimTime);
            states.setEnterFadeDuration(duration);
            states.setExitFadeDuration(duration);
        }

        return states;
    }

    private static int getThemeResource(Context ctx, @AttrRes int attrResource) {
        TypedValue outValue = new TypedValue();// Used as output
        ctx.getTheme().resolveAttribute(attrResource, outValue, true);

        return outValue.resourceId;
    }
}
