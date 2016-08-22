package ru.chipenable.selectabletextview;

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
        //mSelectableTextView.setOnWordLongPressListener(this);
        mSelectedWord = (TextView)findViewById(R.id.selected_word_tv);
    }

    @Override
    public void onWordClick(String word) {
        mSelectedWord.setText(word);
    }

    @Override
    public void onWordDoubleClick(String word) {
        mSelectedWord.setText(word);
    }

    @Override
    public void onWordLongPress(String word) {
        mSelectedWord.setText(word);
    }
}
