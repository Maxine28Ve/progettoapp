package com.registro.registromars

import android.provider.BaseColumns

object DBContract {

    /* Inner class that defines the table contents */
    class CategoryEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "users"
            val COLUMN_CATEGORY = "category"
        }
    }
}
