package com.yelprestaurant.utils;

import android.content.Context;
import android.graphics.Typeface;

public class TypefaceUtils {

    private static Typeface Exo2RegularFont;

    private static Typeface AssistantSemiBold;
    private static Typeface AssistantBold;
    private static Typeface AssistantRegular;

    public static Typeface getExo2RegularFont(Context context) {

        if (Exo2RegularFont == null)
            Exo2RegularFont = Typeface.createFromAsset(context.getAssets(), "fonts/Exo2.0-Regular.otf");

        return Exo2RegularFont;
    }

    public static Typeface getAssistantSemiBoldFont(Context context) {

        if (AssistantSemiBold == null)
            AssistantSemiBold = Typeface.createFromAsset(context.getAssets(), "fonts/Assistant-SemiBold.ttf");

        return AssistantSemiBold;
    }

    public static Typeface getAssistantBoldFont(Context context) {

        if (AssistantBold == null)
            AssistantBold = Typeface.createFromAsset(context.getAssets(), "fonts/Assistant-Bold.ttf");

        return AssistantBold;
    }

    public static Typeface getAssistantRegularFont(Context context) {

        if (AssistantRegular == null)
            AssistantRegular = Typeface.createFromAsset(context.getAssets(), "fonts/Assistant-Regular.ttf");

        return AssistantRegular;
    }
}