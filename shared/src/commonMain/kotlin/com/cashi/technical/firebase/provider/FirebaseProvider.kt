package com.cashi.technical.firebase.provider

import com.cashi.technical.firebase.FirebaseDataSource

/**
Created By: Pratham
 */
expect object FirebaseProvider {
    fun getDataSource() : FirebaseDataSource
}