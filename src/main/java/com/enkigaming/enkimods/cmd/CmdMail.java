package com.enkigaming.enkimods.cmd;

import latmod.core.*;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.*;
import net.minecraft.util.*;

import com.enkigaming.enkimods.*;
import com.enkigaming.enkimods.Mailbox.Mail;

public class CmdMail extends CmdEnki
{
	public CmdMail()
	{ super("mail"); }
	
	public void printHelp(ICommandSender ics)
	{
		LatCoreMC.printChat(ics, "/mail check");
		LatCoreMC.printChat(ics, "/mail send <player> <message...>");
		LatCoreMC.printChat(ics, "/mail read <ID>");
		LatCoreMC.printChat(ics, "/mail delete <ID>");
	}
	
	public String[] getTabStrings(ICommandSender ics, String args[], int i)
	{
		if(i == 0) return new String[] { "check", "send", "read", "delete" };
		return super.getTabStrings(ics, args, i);
	}
	
	public NameType getUsername(String[] args, int i)
	{ return (i == 1 && isArg(args, 0, "send")) ? NameType.LM_OFF : NameType.NONE; }
	
	public String onCommand(ICommandSender ics, String[] args)
	{
		if(args.length >= 1)
		{
			if(args[0].equalsIgnoreCase("send"))
			{
				if(args.length > 2)
				{
					LMPlayer to = LMPlayer.getPlayer(args[1]);
					
					if(to == null) throw new PlayerNotFoundException();
					
					LMPlayer from = LMPlayer.getPlayer(ics);
					if(from == null) throw new PlayerNotFoundException();
					
					String[] s1 = new String[args.length - 2];
					System.arraycopy(args, 2, s1, 0, s1.length);
					String message = LatCore.unsplit(s1, " ");
					
					Mail m = Mailbox.sendMail(from, to, message);
					
					return FINE + "Mail #" + m.mailID + " sent to " + to.getDisplayName() + "!";
				}
				else printHelp(ics);
			}
			
			if(!(ics instanceof EntityPlayer))
				throw new PlayerNotFoundException();
			
			EntityPlayer ep = (EntityPlayer)ics;
			
			if(args[0].equalsIgnoreCase("check"))
			{
				FastList<Mail> mail = Mailbox.getMailFor(ep.getUniqueID());
				if(mail.size() > 0)
				{
					mail.sort(null);
					
					for(int i = 0; i < mail.size(); i++)
					{
						Mail m = mail.get(i);
						
						IChatComponent line = new ChatComponentText("[ #" + m.mailID + " ] From " + LMPlayer.getPlayer(m.from).getDisplayName());
						line.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Open Message #" + m.mailID)));
						line.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mail read " + m.mailID));
						ep.addChatMessage(line);
					}
				}
				else return FINE + "The Mailbox is empty";
			}
			else if(args[0].equalsIgnoreCase("read"))
			{
				int id = parseInt(ics, args[1]);
				
				Mail m = Mailbox.getFromID(id);
				
				if(m == null) return "Invalid mail ID!";
				else
				{
					LMPlayer fromPlayer = LMPlayer.getPlayer(m.from);
					
					LatCoreMC.printChat(ics, "#" + m.mailID + " from " + fromPlayer.getDisplayName() + ":");
					LatCoreMC.printChat(ics, "");
					LatCoreMC.printChat(ics, m.message);
					LatCoreMC.printChat(ics, "");
					
					IChatComponent buttonDelete = new ChatComponentText("[Delete]");
					buttonDelete.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Delete this message")));
					buttonDelete.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mail delete " + id));
					buttonDelete.getChatStyle().setColor(EnumChatFormatting.GOLD);
					
					IChatComponent buttonReply = new ChatComponentText("[Reply]");
					buttonReply.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Replay " + fromPlayer.getDisplayName())));
					buttonReply.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, LatCoreMC.removeFormatting("/mail send " + fromPlayer.getDisplayName() + " ")));
					buttonReply.getChatStyle().setColor(EnumChatFormatting.GOLD);
					
					ics.addChatMessage(new ChatComponentText("[ - ").appendSibling(buttonDelete).appendSibling(new ChatComponentText(" - ")).appendSibling(buttonReply).appendSibling(new ChatComponentText(" - ]")));
					return null;
				}
			}
			else if(args[0].equalsIgnoreCase("delete"))
			{
				int id = parseInt(ics, args[1]);
				
				if(Mailbox.deleteMail(id))
					return FINE + "Mail #" + id + " deleted!";
				else
					return "Invalid mail ID!";
			}
		}
		else printHelp(ics);
		return null;
	}
}