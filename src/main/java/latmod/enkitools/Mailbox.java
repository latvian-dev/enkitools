package latmod.enkitools;

import java.util.UUID;

import latmod.core.LMPlayer;
import latmod.core.util.*;
import net.minecraft.nbt.NBTTagCompound;

public class Mailbox
{
	public static final FastMap<Integer, Mail> mailMap = new FastMap<Integer, Mail>();
	public static int nextID = 0;
	
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
			from = LMPlayer.getPlayer(tag.getInteger("From"));
			to = LMPlayer.getPlayer(tag.getInteger("To"));
			message = tag.getString("Msg");
		}
		
		public void writeToNBT(NBTTagCompound tag)
		{
			tag.setInteger("From", from.playerID);
			tag.setInteger("To", to.playerID);
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
			if(c > 0) EnkiToolsEventHandler.instance.printIncomingMail(to.getPlayerMP(), c);
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
}