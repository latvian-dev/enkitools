package com.enkigaming.enkimods;

import java.util.UUID;

import latmod.core.*;
import net.minecraft.nbt.*;

public class Mailbox
{
	public static final String TAG = "EnkiMail";
	
	public static final FastMap<Integer, Mail> mailMap = new FastMap<Integer, Mail>();
	private static int nextID = 0;
	
	public static class Mail implements Comparable<Mail>
	{
		public final int mailID;
		public LMPlayer from;
		public LMPlayer to;
		public String message;
		
		public Mail(int id) { mailID = id; }
		
		public Mail(int id, LMPlayer f, LMPlayer t, String msg, long s)
		{
			this(id);
			from = f;
			to = t;
			message = msg;
		}
		
		public void readFromNBT(NBTTagCompound tag)
		{
			from = LMPlayer.getPlayer(tag.getString("From"));
			to = LMPlayer.getPlayer(tag.getString("To"));
			message = tag.getString("Msg");
		}
		
		public void writeToNBT(NBTTagCompound tag)
		{
			tag.setString("From", from.username);
			tag.setString("To", to.username);
			tag.setString("Msg", message);
		}
		
		public int hashCode()
		{ return mailID; }
		
		public boolean equals(Object o)
		{ return (o != null && (o == this || mailID == o.hashCode())); }
		
		public int compareTo(Mail o)
		{ return Long.compare(mailID, o.mailID); }
	}
	
	public static Mail sendMail(LMPlayer from, LMPlayer to, String message)
	{
		Mail m = new Mail(++nextID);
		m.from = from;
		m.to = to;
		m.message = message;
		mailMap.put(m.mailID, m);
		
		if(to.isOnline())
		{
			int c = Mailbox.getMailFor(to.uuid).size();
			if(c > 0) EnkiModsEventHandler.instance.printIncomingMail(to.getPlayerMP(), c);
		}
		
		return m;
	}
	
	public static FastList<Mail> getMailFor(UUID id)
	{
		FastList<Mail> l = new FastList<Mail>();
		
		for(int i = 0; i < mailMap.size(); i++)
		{
			Mail m = mailMap.values.get(i);
			if(m.to.equals(id)) l.add(m);
		}
		
		return l;
	}
	
	public static Mail getFromID(int id)
	{ return mailMap.values.getObj(id); }
	
	public static boolean deleteMail(int id)
	{
		boolean b = mailMap.keys.contains(id);
		if(b) mailMap.remove(id); return b;
	}
	
	public static void readFromNBT(NBTTagCompound tag)
	{
		nextID = tag.getInteger(TAG + "NextID");
		
		mailMap.clear();
		
		NBTTagList list = (NBTTagList)tag.getTag(TAG);
		
		if(list != null && list.tagCount() > 0)
		{
			for(int i = 0; i < list.tagCount(); i++)
			{
				NBTTagCompound tag1 = list.getCompoundTagAt(i);
				
				int id = tag1.getInteger("ID");
				Mail m = new Mail(id);
				m.readFromNBT(tag1);
				mailMap.put(id, m);
			}
		}
	}
	
	public static void writeToNBT(NBTTagCompound tag)
	{
		tag.setInteger(TAG + "NextID", nextID);
		
		NBTTagList list = new NBTTagList();
		
		for(int i = 0; i < mailMap.size(); i++)
		{
			Mail m = mailMap.values.get(i);
			
			NBTTagCompound tag1 = new NBTTagCompound();
			
			tag1.setInteger("ID", m.mailID);
			m.writeToNBT(tag1);
			list.appendTag(tag1);
		}
		
		tag.setTag(TAG, list);
	}
}