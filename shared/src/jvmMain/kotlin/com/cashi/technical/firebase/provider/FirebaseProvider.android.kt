package com.cashi.technical.firebase.provider

import com.cashi.technical.firebase.FirebaseDataSource
import com.cashi.technical.model.Payment
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

actual object FirebaseProvider {
    actual fun getDataSource(): FirebaseDataSource = AndroidFirebaseDataSource()
}

class AndroidFirebaseDataSource : FirebaseDataSource{

    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("payments")

    override suspend fun addPayment(payment: Payment) {
        collection.add(payment).await()
    }

    override fun getPayments(): Flow<List<Payment>> = callbackFlow{
       val listener = collection.addSnapshotListener { snapshot, error ->
           if(error != null){
               return@addSnapshotListener
           }

           if(snapshot != null){
               val payments = snapshot.documents.mapNotNull {
                   it.toObject(Payment::class.java)
               }.filterNot { it.recipientEmail.isBlank() }

               trySend(payments)
           }
       }
        awaitClose { listener.remove() }
    }
}