package com.workfort.linkpreview.util

import android.content.Context

/**
 * ****************************************************************************
 * Created by : arhan on 30 Jun, 2020 at 14:19.
 * Email : ashik.pstu.cse@gmail.com
 *
 * This class is for:
 * 1.
 * 2.
 * 3.
 *
 * Last edited by : arhan on 2020/06/30 at 14:19.
 *
 * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>
 * ****************************************************************************
 */

object Helper {
    fun dpToPx(context: Context, dp: Int): Float {
        return (dp * context.resources.displayMetrics.density)
    }
}