package ru.chipenable.selectabletextview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SelectableTextView.OnWordClick,
        SelectableTextView.OnWordDoubleClick, SelectableTextView.OnWordLongPress{

    private TextView mSelectedWord;
    private SelectableTextView mSelectableTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSelectableTextView = (SelectableTextView)findViewById(R.id.selectable_tv);
        //mSelectableTextView.setOnWordClickListener(this);
        mSelectableTextView.setOnWordDoubleClickListener(this);
        mSelectableTextView.setOnWordLongPressListener(this);
        mSelectableTextView.enableSelectWord(true);
        mSelectableTextView.setSelectColor(Color.RED);
        mSelectedWord = (TextView)findViewById(R.id.selected_word_tv);
    }

    @Override
    public void onWordClick(int position, String word) {
        mSelectedWord.setText(word);
    }

    @Override
    public void onWordDoubleClick(int position, String word) {
        mSelectedWord.setText(word);
    }

    @Override
    public void onWordLongPress(int position, String word) {
        mSelectedWord.setText(word);
    }
}
