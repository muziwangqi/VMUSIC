package com.soling.custom.view.tabflow;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.soling.R;

public class TagFlowLayout extends FlowLayout {

    private OnItemClickListener onItemClickListener;

    public TagFlowLayout(Context context) {
        this(context, null);
    }

    public TagFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TagFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTabAdapter(TagAdapter adapter) {
        for (int i = 0; i < adapter.getCount(); i++) {
            View view = adapter.getView(this, i, adapter.getItem(i));
            final TagView viewContainer = new TagView(getContext());
            LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = 90;
            viewContainer.setLayoutParams(layoutParams);
            view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
            viewContainer.addView(view);
            addView(viewContainer);
            view.setClickable(false);
            final int position = i;
            viewContainer.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(viewContainer, position);
                    }
                }
            });
            viewContainer.setBackgroundResource(R.drawable.selector_tabflow_music_search_hot);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    interface OnItemClickListener {
        void onItemClick(View item, int position);
    }

}
