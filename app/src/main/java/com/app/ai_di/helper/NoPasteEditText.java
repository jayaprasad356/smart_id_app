package com.app.ai_di.helper;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
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
        // Disable long-click to prevent text selection or context menu
        setLongClickable(false);
        setTextIsSelectable(false);

        // Disable the context menu by setting an empty ActionMode.Callback
        setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false; // Prevents the action mode from being created
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
        // Prevent paste action by checking if the item ID matches paste
        if (id == android.R.id.paste || id == android.R.id.pasteAsPlainText) {
            return false;
        }
        return super.onTextContextMenuItem(id);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu) {
        // Prevents the context menu from appearing
    }
}
