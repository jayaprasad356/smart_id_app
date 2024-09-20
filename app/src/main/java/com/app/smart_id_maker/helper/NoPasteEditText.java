package com.app.smart_id_maker.helper;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
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
        // Set an InputFilter to block pasted content
        setFilters(new InputFilter[] { new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // If the source is empty, it's a delete action, allow it
                if (source.length() == 0) return null;

                // Return an empty string to block any pasted input
                return "";
            }
        }});
    }
}