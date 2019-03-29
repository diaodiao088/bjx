package com.bjxapp.worker.ui.expandablelayout;

import android.view.View;

/**
 * Created by mertsimsek on 28/07/2017.
 */

public interface ExpandCollapseListener<P> {

    interface ExpandListener<P> {
        void onExpanded(int parentIndex, P parent, View view);
    }

    interface CollapseListener<P> {
        void onCollapsed(int parentIndex, P parent, View view);
    }
}
