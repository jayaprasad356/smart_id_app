package com.gmwapp.slv_aidi.helper;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.appcompat.widget.AppCompatEditText;

public class NoPasteEditText extends AppCompatEditText {

    public NoPasteEditText(Context context) {
        super(context);
        init();
    }

    public NoPasteEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NoPasteEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLongClickable(false);
        setTextIsSelectable(false);

        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        if (id == android.R.id.paste || id == android.R.id.pasteAsPlainText) {
            return true;
        }
        return super.onTextContextMenuItem(id);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu) {
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_NULL; // Disable system keyboard
        return null;
    }
}
