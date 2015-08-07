package com.musar.services;

import java.util.ArrayList;


import com.musar.Database.Contact;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.telephony.TelephonyManager;
import android.util.Base64;

public class ContactsManager  {

	/** get the contacts from the user to recommend from them
	 * @param args
	 */
	Context service;
	ArrayList<Contact>contact=new ArrayList<Contact>();
	public ArrayList<Integer>images=new ArrayList<Integer>();
	public ContactsManager(Context service){this.service=service;}
	
	// // getting users phone
		public String getUsersphone(){
			  @SuppressWarnings("static-access")
			TelephonyManager telMgr = (TelephonyManager)service.getSystemService(service.TELEPHONY_SERVICE);
			  String phone_number=telMgr.getLine1Number();
	            System.out.println("Mobile Number : "+ telMgr.getLine1Number());
	            System.out.println("Sim Serial Number : "+ telMgr.getSimSerialNumber());
	           
	            return phone_number;
		}
		//-----------------------
		public ArrayList<Contact> GetContacts(Context context) throws Exception{
			System.out.println("contactttttttttttttttttttttttttttttttttttttttttttttt");
			    String contactNumber = null;
			    //int contactNumberType = Phone.TYPE_MOBILE;
			    String nameOfContact = null;
			    if (true) {
			        ContentResolver cr = context.getContentResolver();
			        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,null, null, null);
			        if (cur.getCount() > 0) {
			            while (cur.moveToNext()) {
			                String id = cur.getString(cur.getColumnIndex(BaseColumns._ID));
			                nameOfContact = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
			                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = ?", new String[] { id },null);
			                          
			                while (phones.moveToNext()) {
			                  contactNumber = phones.getString(phones.getColumnIndex(Phone.NUMBER));
			                  int photo_id=phones.getInt(phones.getColumnIndex(Phone.PHOTO_ID));
			                  images.add(photo_id);
			                  Contact c=new Contact(nameOfContact,contactNumber);
			                  contact.add(c);
			                  }
			                  phones.close();
			                }

			            }
			        }// end of contact name cursor
			        cur.close();

			    }
			    return contact;
			}


}
