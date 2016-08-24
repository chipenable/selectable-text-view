package ru.chipenable.selectabletextview;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Pashgan on 06.06.2016.
 */
public class SelectableTextView extends TextView {

    private static final String TAG = SelectableTextView.class.getName();
    private static final int DEFAULT_COLOR = Color.RED;
    private OnWordClick mWordClickListener;
    private OnWordDoubleClick mWordDoubleClickListener;
    private OnWordLongPress mOnWordLongPressListener;
    private GestureDetector mGestureDetector;
    private boolean mEnableSpan = false;
    private int mColor = DEFAULT_COLOR;

    public interface OnWordClick {
        void onWordClick(int position, String word);
    }

    public interface OnWordDoubleClick {
        void onWordDoubleClick(int position, String word);
    }

    public interface OnWordLongPress {
        void onWordLongPress(int position, String word);
    }

    public SelectableTextView(Context context) {
        super(context);
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    public SelectableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    public SelectableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, new GestureListener());
    }

    public void setOnWordClickListener(OnWordClick listener) {
        mWordClickListener = listener;
    }

    public void setOnWordDoubleClickListener(OnWordDoubleClick listener) {
        mWordDoubleClickListener = listener;
    }

    public void setOnWordLongPressListener(OnWordLongPress listener) {
        mOnWordLongPressListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            if (mWordClickListener != null) {
                Position p = findSelectedWord(e);
                if (p != null) {
                    setSpan(p);
                    String selectedWord = getText().subSequence(p.start, p.end).toString();
                    mWordClickListener.onWordClick(p.start, selectedWord);
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mWordDoubleClickListener != null) {
                Position p = findSelectedWord(e);
                if (p != null) {
                    setSpan(p);
                    String selectedWord = getText().subSequence(p.start, p.end).toString();
                    mWordDoubleClickListener.onWordDoubleClick(p.start, selectedWord);
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mOnWordLongPressListener != null) {
                Position p = findSelectedWord(e);
                if (p != null) {
                    setSpan(p);
                    String selectedWord = getText().subSequence(p.start, p.end).toString();
                    mOnWordLongPressListener.onWordLongPress(p.start, selectedWord);
                }
            }
            super.onLongPress(e);
        }
    }

    private class Position {
        public int start;
        public int end;

        Position(int s, int e) {
            start = s;
            end = e;
        }
    }

    private Position findSelectedWord(MotionEvent e) {

        String text = getText().toString();
        int start = getOffsetForPosition(e.getX(), e.getY());

        if ((start >= text.length()) || (start < 0)) {
            return null;
        }

        Character ch = text.charAt(start);
        if ((ch.compareTo(' ') == 0) || (ch.compareTo('\n') == 0)) {
            return null;
        }

        int wordStart = 0;
        Character[] startPattern = {'.', ',', ' ', '('};
        for (Character character : startPattern) {
            int position = text.lastIndexOf(character, start);
            Log.d(TAG, character.toString() + " " + Integer.toString(position));
            if (position != -1 && position > wordStart) {
                wordStart = position;
            }
        }

        if (wordStart != 0) {
            wordStart++;
        }

        int wordEnd = text.length();
        Character[] endPattern = {'.', ',', ' ', ')'};
        for (Character character : endPattern) {
            int end = text.indexOf(character, start);
            if (end != -1 && end < wordEnd) {
                wordEnd = end;
            }
        }

        if (wordStart >= wordEnd) {
            return null;
        }

        return new Position(wordStart, wordEnd);
    }

    private void setSpan(Position p) {
        if (mEnableSpan && p != null) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(getText());
            CharacterStyle span = new ForegroundColorSpan(mColor);
            ssb.clearSpans();
            ssb.setSpan(span, p.start, p.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            setText(ssb);
        }
    }

    public void enableSelectWord(boolean enable){
        mEnableSpan = enable;
    }

    public void setSelectColor(int color){
        mColor = color;
    }
}
