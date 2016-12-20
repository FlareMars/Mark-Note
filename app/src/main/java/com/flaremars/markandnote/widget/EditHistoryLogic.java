package com.flaremars.markandnote.widget;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.flaremars.markandnote.bean.EditHistory;

import java.util.Stack;

/**
 * Created by FlareMars on 2016/11/13.
 */
public class EditHistoryLogic implements TextWatcher {

    private static final String TAG = "EditHistoryLogic";

    private Stack<EditHistory> redoHistories;
    private Stack<EditHistory> undoHistories;
    private boolean addRedo = false;
    private boolean clear = true;

    private EditText targetEditText;

    public EditHistoryLogic(EditText target) {
        this.targetEditText = target;
        init();
    }

    private void init() {
        undoHistories = new Stack<>();
        redoHistories = new Stack<>();
        targetEditText.addTextChangedListener(this);
    }

    public boolean canRedo() {
        return redoHistories.size() > 0;
    }

    public boolean canUndo() {
        return undoHistories.size() > 0;
    }

    public String redo() {
        if (!canRedo()) {
            return targetEditText.getText().toString();
        }

        EditHistory history = redoHistories.pop();
        clear = false;
        return doContentChange(history);
    }

    public String undo() {
        if (!canUndo()) {
            return targetEditText.getText().toString();
        }

        EditHistory history = undoHistories.pop();
        addRedo = true;
        return doContentChange(history);
    }

    private String doContentChange(EditHistory history) {
        Editable editable = targetEditText.getEditableText();
        if (history.getAfter() > 0) {
            editable.delete(history.getStart(), history.getStart() + history.getAfter());
            targetEditText.setSelection(history.getStart());
        }
        if (history.getCount() > 0) {
            editable.insert(history.getStart(), history.getContent().substring(history.getStart(), history.getStart() + history.getCount()));
            targetEditText.setSelection(history.getStart() + history.getCount());
        }
        return editable.toString();
    }

    public void clearRedoHistory() {
        redoHistories.clear();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//        Log.e(TAG, "beforeTextChanged s = " + s + " start = " + start + " count = " + count + " after = " + after);
        if (addRedo) {
            redoHistories.push(new EditHistory(s.toString(), count, start, after));
            addRedo = false;
        } else {
            if (clear) {
                clearRedoHistory();
            }
            undoHistories.push(new EditHistory(s.toString(), count, start, after));
            clear = true;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable s) {
        // do nothing
    }
}
