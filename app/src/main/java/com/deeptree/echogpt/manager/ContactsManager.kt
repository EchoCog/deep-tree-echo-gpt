package com.deeptree.echogpt.manager

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.ContactsContract
import androidx.activity.result.ActivityResultLauncher

class ContactsManager(private val context: Context) {
    
    fun openContacts(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
        launcher.launch(intent)
    }
    
    fun getAllContacts(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phone = it.getString(phoneIndex)
                contacts.add(Contact(name, phone))
            }
        }
        
        return contacts
    }
    
    fun searchContacts(query: String): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf("%$query%")
        
        val cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            selection,
            selectionArgs,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            
            while (it.moveToNext()) {
                val name = it.getString(nameIndex)
                val phone = it.getString(phoneIndex)
                contacts.add(Contact(name, phone))
            }
        }
        
        return contacts
    }
    
    data class Contact(val name: String, val phone: String)
}