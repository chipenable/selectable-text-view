package ru.chipenable.selectabletextview;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * Created by Pashgan on 06.06.2016.
 */
public class SelectableTextView extends TextView {

    private static final String TAG = SelectableTextView.class.getName();
    private static final int DEFAULT_COLOR = Color.RED;
    private static final Character[] START_PATTERN = {'.', ',', ' ', '('};
    private static final Character[] END_PATTERN = {'.', ',', ' ', ')'};

    private OnWordClick mWordClickListener;
    private OnWordDoubleClick mWordDoubleClickListener;
    private OnWordLongPress mOnWordLongPressListener;
    private GestureDetector mGestureDetector;

    private boolean mEnableSpan = false;
    private int mColor = DEFAULT_COLOR;

    private SelectedWord selectedWord;

    public interface OnWordClick {
        void onWordClick(int position, String word);
    }

    public interface OnWordDoubleClick extends OnWordClick{
        void onWordDoubleClick(int position, String word);
    }

    public interface OnWordLongPress extends OnWordClick{
        void onWordLongPress(int position, String word);
    }

    /** constructors */

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

    /** setters */

    public void setOnWordClickListener(OnWordClick listener) {
        mWordClickListener = listener;
    }

    public void setOnWordDoubleClickListener(OnWordDoubleClick listener) {
        mWordDoubleClickListener = listener;
    }

    public void setOnWordLongPressListener(OnWordLongPress listener) {
        mOnWordLongPressListener = listener;
    }

    public void enableSelectWord(boolean enable){
        mEnableSpan = enable;
    }

    public void setSelectColor(int color){
        mColor = color;
    }

    /** methods to save state */

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.start = selectedWord.start;
        ss.end = selectedWord.end;
        ss.word = selectedWord.word;
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        selectedWord = new SelectedWord(ss.start, ss.end, ss.word);
        setSpan(selectedWord);
    }

    static class SavedState extends BaseSavedState{
        int start;
        int end;
        String word;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.start = in.readInt();
            this.end = in.readInt();
            this.word = in.readString();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.start);
            out.writeInt(this.end);
            out.writeString(this.word);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

    }

    /** handling of touch events */

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            if (mWordClickListener != null) {
                SelectedWord p = findSelectedWord(e);
                if (p != null) {
                    setSpan(p);
                    mWordClickListener.onWordClick(p.start, p.word);
                }
            }
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mWordDoubleClickListener != null) {
                SelectedWord p = findSelectedWord(e);
                if (p != null) {
                    setSpan(p);
                    mWordDoubleClickListener.onWordDoubleClick(p.start, p.word);
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mOnWordLongPressListener != null) {
                SelectedWord p = findSelectedWord(e);
                if (p != null) {
                    setSpan(p);
                    mOnWordLongPressListener.onWordLongPress(p.start, p.word);
                }
            }
            super.onLongPress(e);
        }
    }

    /** util classes and methods */

    private class SelectedWord {
        int start;
        int end;
        String word;

        SelectedWord(int start, int end, String word) {
            this.start = start;
            this.end = end;
            this.word = word;
        }
    }

    private SelectedWord findSelectedWord(MotionEvent e) {

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
        for (Character character : START_PATTERN) {
            int position = text.lastIndexOf(character, start);
            if (position != -1 && position > wordStart) {
                wordStart = position;
            }
        }

        if (wordStart != 0) {
            wordStart++;
        }

        int wordEnd = text.length();
        for (Character character : END_PATTERN) {
            int end = text.indexOf(character, start);
            if (end != -1 && end < wordEnd) {
                wordEnd = end;
            }
        }

        if (wordStart >= wordEnd) {
            return null;
        }

        String word = getText().subSequence(wordStart, wordEnd).toString();
        selectedWord = new SelectedWord(wordStart, wordEnd, word);
        return selectedWord;
    }

    private void setSpan(SelectedWord p) {
        if (mEnableSpan && p != null) {
            SpannableStringBuilder ssb = new SpannableStringBuilder(getText());
            CharacterStyle span = new ForegroundColorSpan(mColor);
            ssb.clearSpans();
            ssb.setSpan(span, p.start, p.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            setText(ssb);
        }
    }


}