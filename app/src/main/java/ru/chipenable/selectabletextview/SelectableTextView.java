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

    public interface OnWordClick{
        void onWordClick(String word);
    }

    public interface OnWordDoubleClick{
        void onWordDoubleClick(String word);
    }

    public interface OnWordLongPress{
        void onWordLongPress(String word);
    }

    private static final String TAG = SelectableTextView.class.getName();

    private OnWordClick mWordClickListener;
    private OnWordDoubleClick mWordDoubleClickListener;
    private OnWordLongPress mOnWordLongPressListener;
    private GestureDetector mGestureDetector;

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

    public void setOnWordClickListener(OnWordClick listener){
        mWordClickListener = listener;
    }

    public void setOnWordDoubleClickListener(OnWordDoubleClick listener){
        mWordDoubleClickListener = listener;
    }

    public void setOnWordLongPressListener(OnWordLongPress listener){
        mOnWordLongPressListener = listener;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }



    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            Position p = findSelectedWord(e);
            if (p != null){
                String selectedWord = getText().subSequence(p.start, p.end).toString();
                if (mWordClickListener != null){
                    mWordClickListener.onWordClick(selectedWord);
                }
            }

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            Position p = findSelectedWord(e);
            if (p != null){
                CharSequence text = getText();
                String selectedWord = text.subSequence(p.start, p.end).toString();

                SpannableStringBuilder ssb = new SpannableStringBuilder(text);
                CharacterStyle span = new ForegroundColorSpan(Color.RED);
                ssb.clearSpans();
                ssb.setSpan(span, p.start, p.end, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
                setText(ssb);

                if (mWordDoubleClickListener != null){
                    mWordDoubleClickListener.onWordDoubleClick(selectedWord);
                }
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Position p = findSelectedWord(e);
            if (p != null){
                String selectedWord = getText().subSequence(p.start, p.end).toString();
                Log.d(TAG, "Selected text: " + selectedWord);
                if (mOnWordLongPressListener != null){
                    mOnWordLongPressListener.onWordLongPress(selectedWord);
                }
            }
            super.onLongPress(e);
        }
    }

    private class Position{
        public int start;
        public int end;

        Position(int s, int e){
            start = s;
            end = e;
        }
    }

    private Position findSelectedWord(MotionEvent e){

        String text = getText().toString();
        int start = getOffsetForPosition(e.getX(), e.getY());

        if ((start >= text.length()) || (start < 0)){
            return null;
        }

        Character ch = text.charAt(start);
        if ((ch.compareTo(' ') == 0) || (ch.compareTo('\n') == 0)){
            return null;
        }

        int wordStart = 0;
        Character[] startPattern = {'.', ',', ' ', '('};
        for(Character character: startPattern){
            int position = text.lastIndexOf(character, start);
            Log.d(TAG, character.toString() + " " + Integer.toString(position));
            if (position != -1 && position > wordStart){
                wordStart = position;
            }
        }

        if (wordStart != 0){
            wordStart++;
        }

        int wordEnd = text.length();
        Character[] endPattern = {'.', ',', ' ', ')'};
        for(Character character: endPattern){
            int end = text.indexOf(character, start);
            if (end != -1 && end < wordEnd){
                wordEnd = end;
            }
        }

        //int endWord = text.indexOf(' ', start);
        /*if (wordEnd == -1){
            wordEnd = text.length();
        }*/

        if (wordStart >= wordEnd) {
            return null;
        }

        return new Position(wordStart, wordEnd);
    }


}
