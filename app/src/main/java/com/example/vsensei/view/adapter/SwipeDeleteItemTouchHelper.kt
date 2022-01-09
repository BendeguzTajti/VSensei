package com.example.vsensei.view.adapter

import android.graphics.Canvas
import android.view.View
import androidx.core.view.marginEnd
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.vsensei.R
import kotlin.math.absoluteValue

class SwipeDeleteItemTouchHelper(
    dragDirs: Int,
    swipeDirs: Int,
    val onSwiped: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return true
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        val foreGroundView = viewHolder?.itemView?.findViewById<View>(R.id.item_container)
        getDefaultUIUtil().onSelected(foreGroundView)
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val backgroundView = viewHolder?.itemView?.findViewById<View>(R.id.delete_item_container)
        val foreGroundView = viewHolder?.itemView?.findViewById<View>(R.id.item_container)
        val backgroundViewWidth = (backgroundView?.width?.minus(foreGroundView?.marginEnd ?: 0))?.toFloat() ?: 0f
        val swipeMaxWidth = if (dX.absoluteValue <= backgroundViewWidth) dX else -backgroundViewWidth
        getDefaultUIUtil().onDrawOver(c, recyclerView, foreGroundView, swipeMaxWidth, dY, actionState, isCurrentlyActive)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foreGroundView = viewHolder.itemView.findViewById<View>(R.id.item_container)
        getDefaultUIUtil().clearView(foreGroundView)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val backgroundView = viewHolder.itemView.findViewById<View>(R.id.delete_item_container)
        val foreGroundView = viewHolder.itemView.findViewById<View>(R.id.item_container)
        val backgroundViewWidth = (backgroundView.width - foreGroundView.marginEnd).toFloat()
        val swipeMaxWidth = if (dX.absoluteValue <= backgroundViewWidth) dX else -backgroundViewWidth
        getDefaultUIUtil().onDraw(c, recyclerView, foreGroundView, swipeMaxWidth, dY, actionState, isCurrentlyActive)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        onSwiped(viewHolder.adapterPosition)
    }
}