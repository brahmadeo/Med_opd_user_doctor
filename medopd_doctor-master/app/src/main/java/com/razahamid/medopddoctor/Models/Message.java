package com.razahamid.medopddoctor.Models;

import com.google.firebase.database.DataSnapshot;
import com.razahamid.medopddoctor.ExtraFiles.FirebaseRef;

import java.io.Serializable;

public class Message implements Serializable{
	public long id;
	public DataSnapshot dataSnapshot;
	private boolean FromMe =false;

	public Message(long id, DataSnapshot dataSnapshot, boolean fromMe) {
		this.id = id;
		this.dataSnapshot = dataSnapshot;
		FromMe = fromMe;
	}

	public boolean isFromMe() {
		return FromMe;
	}

	public boolean hasPrescription(){
		try{
			FirebaseRef ref = new FirebaseRef();
			 return  dataSnapshot.hasChild(ref.MessageType) && dataSnapshot.child(ref.MessageType).getValue(String.class).equals(ref.Prescriptions);
		}catch (Exception ex){
			ex.printStackTrace();
			return false;
		}
	}
}