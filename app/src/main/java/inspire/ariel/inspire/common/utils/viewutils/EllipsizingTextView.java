package inspire.ariel.inspire.common.utils.viewutils;

/*
 * Copyright (C) 2011 Micah Hainline
 * Copyright (C) 2012 Triposo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;

import inspire.ariel.inspire.common.constants.AppNumbers;
import inspire.ariel.inspire.common.constants.AppStrings;

public class EllipsizingTextView extends android.support.v7.widget.AppCompatTextView {
    private static final String ELLIPSIS = "\u2026";
    private static final Pattern DEFAULT_END_PUNCTUATION = Pattern.compile(AppStrings.REGEX_ELIPSIZING_TEXT_VIEW_DEFAULT_END_PUNCTUATION, Pattern.DOTALL);

    public interface EllipsizeListener {
        void ellipsizeStateChanged(boolean ellipsized);
    }

    private final List<EllipsizeListener> ellipsizeListeners = new ArrayList<>();
    private boolean isEllipsized;
    private boolean isStale;
    private boolean programmaticChange;
    private CharSequence fullText;
    private int maxLines;
    private float lineSpacingMultiplier = 1.0f;
    private float lineAdditionalVerticalPadding = 0.0f;

    /**
     * The end punctuation which will be removed when appending #ELLIPSIS.
     */
    private Pattern endPunctuationPattern;

    public EllipsizingTextView(Context context) {
        this(context, null);
    }

    public EllipsizingTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EllipsizingTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        super.setEllipsize(null);
        TypedArray a = context.obtainStyledAttributes(attrs, new int[]{android.R.attr.maxLines});
        setMaxLines(a.getInt(0, Integer.MAX_VALUE));
        a.recycle();
        setEndPunctuationPattern(DEFAULT_END_PUNCTUATION);
    }

    public void setEndPunctuationPattern(Pattern pattern) {
        this.endPunctuationPattern = pattern;
    }

    public void addEllipsizeListener(EllipsizeListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        ellipsizeListeners.add(listener);
    }

    public void removeEllipsizeListener(EllipsizeListener listener) {
        ellipsizeListeners.remove(listener);
    }

    public boolean isEllipsized() {
        return isEllipsized;
    }

    @Override
    public void setMaxLines(int maxLines) {
        super.setMaxLines(maxLines);
        this.maxLines = maxLines;
        isStale = true;
    }

    @SuppressLint("Override")
    public int getMaxLines() {
        return maxLines;
    }

    public boolean ellipsizingLastFullyVisibleLine() {
        return maxLines == Integer.MAX_VALUE;
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        this.lineAdditionalVerticalPadding = add;
        this.lineSpacingMultiplier = mult;
        super.setLineSpacing(add, mult);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int before, int after) {
        super.onTextChanged(text, start, before, after);
        if (!programmaticChange) {
            fullText = text;
            isStale = true;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (ellipsizingLastFullyVisibleLine()) {
            isStale = true;
        }
    }

    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        if (ellipsizingLastFullyVisibleLine()) {
            isStale = true;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isStale) {
            resetText();
        }
        super.onDraw(canvas);
    }

    private void resetText() {
        CharSequence workingText = fullText;
        boolean ellipsized = false;
        Layout layout = createWorkingLayout(workingText);
        int linesCount = getLinesCount();
        if (layout.getLineCount() > linesCount) {
            // We have more lines of text than we are allowed to display.
            workingText = fullText.subSequence(0, layout.getLineEnd(linesCount - 1));
            while (createWorkingLayout(workingText + ELLIPSIS).getLineCount() > linesCount) {
                int lastSpace = workingText.toString().lastIndexOf(' ');
                if (lastSpace == -1) {
                    break;
                }
                workingText = workingText.subSequence(0, lastSpace);
            }
            // We should do this in the loop above, but it's cheaper this way.
            if (workingText instanceof Spannable) {
                SpannableStringBuilder builder = new SpannableStringBuilder(workingText);
                Matcher matcher = endPunctuationPattern.matcher(workingText);
                if (matcher.find()) {
                    builder.replace(matcher.start(), workingText.length(), ELLIPSIS);
                }
                workingText = builder;
            } else {
                workingText = endPunctuationPattern.matcher(workingText).replaceFirst("");
                workingText = workingText + ELLIPSIS;
            }

            ellipsized = true;
        }
        if (!workingText.equals(getText())) {
            programmaticChange = true;
            try {
                setText(workingText);
            } finally {
                programmaticChange = false;
            }
        }
        isStale = false;
        if (ellipsized != isEllipsized) {
            isEllipsized = ellipsized;
            for (EllipsizeListener listener : ellipsizeListeners) {
                listener.ellipsizeStateChanged(ellipsized);
            }
        }
    }


    /**
     * Get how many lines of text we are allowed to display.
     */
    private int getLinesCount() {
        if (ellipsizingLastFullyVisibleLine()) {
            int fullyVisibleLinesCount = getFullyVisibleLinesCount();
            if (fullyVisibleLinesCount == -1) {
                return 1;
            } else {
                return fullyVisibleLinesCount;
            }
        } else {
            return maxLines;
        }
    }

    /**
     * Get how many lines of text we can display so their full height is visible.
     */
    private int getFullyVisibleLinesCount() {
        Layout layout = createWorkingLayout("");
        int height = getHeight() - getPaddingTop() - getPaddingBottom();
        int lineHeight = layout.getLineBottom(AppNumbers.FIRST_LINE);
        if (lineHeight == AppNumbers.NO_HEIGHT) {
            return height / AppNumbers.ELLIPSIZING_TV_DEFAULT_LINE_HEIGHT_DIVIDER;
        }
        return height / lineHeight;
    }

    private Layout createWorkingLayout(CharSequence workingText) {
        return new StaticLayout(workingText, getPaint(),
                getWidth() - getPaddingLeft() - getPaddingRight(),
                Alignment.ALIGN_NORMAL, lineSpacingMultiplier,
                lineAdditionalVerticalPadding, false /* includepad */);
    }

    @Override
    public void setEllipsize(TruncateAt where) {
        // Ellipsize settings are not respected
    }
}


/**
 * OnSavedInstanceState (listeners are not saved)
 */
   /* @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(AppStrings.SAVED_STATE_SUPER_STATE, super.onSaveInstanceState());
        bundle.putBoolean(AppStrings.SAVED_STATE_IS_ELLIPSIZED, isEllipsized);
        bundle.putBoolean(AppStrings.SAVED_STATE_IS_STALE, isStale);
        bundle.putBoolean(AppStrings.SAVED_STATE_PROGRAMMATIC_CHANGE, programmaticChange);
        bundle.putCharSequence(AppStrings.SAVED_STATE_FULL_TEXT, fullText);
        bundle.putInt(AppStrings.SAVED_STATE_MAX_LINES, maxLines);
        bundle.putFloat(AppStrings.SAVED_STATE_LINE_SPACING_MULTIPLIER, lineSpacingMultiplier);
        bundle.putFloat(AppStrings.SAVED_STATE_LINE_ADDITIONAL_VERTICAL_PADDING, lineAdditionalVerticalPadding);
        return bundle;
    }

    //Todo: Listeners are not being saved. Check if bug is created
    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) { // implicit null check
            Bundle bundle = (Bundle) state;
            this.isEllipsized = bundle.getBoolean(AppStrings.SAVED_STATE_IS_ELLIPSIZED);
            this.isStale = bundle.getBoolean(AppStrings.SAVED_STATE_IS_STALE);
            this.programmaticChange = bundle.getBoolean(AppStrings.SAVED_STATE_PROGRAMMATIC_CHANGE);
            this.fullText = bundle.getCharSequence(AppStrings.SAVED_STATE_FULL_TEXT);
            this.maxLines = bundle.getInt(AppStrings.SAVED_STATE_MAX_LINES);
            this.lineSpacingMultiplier = bundle.getInt(AppStrings.SAVED_STATE_LINE_SPACING_MULTIPLIER);
            this.lineAdditionalVerticalPadding = bundle.getInt(AppStrings.SAVED_STATE_LINE_ADDITIONAL_VERTICAL_PADDING);
            state = bundle.getParcelable("superState");
        }
        super.onRestoreInstanceState(state);
    }
*/
