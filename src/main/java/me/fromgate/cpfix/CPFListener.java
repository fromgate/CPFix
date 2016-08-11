/*  
 *  CPFix, Minecraft bukkit plugin
 *  (c)2013, fromgate, fromgate@gmail.com
 *  http://dev.bukkit.org/server-mods/cpfix/
 *   * 
 *  This file is part of CPFix.
 *  
 *  CPFix is free software: you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CPFix is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CPFix.  If not, see <http://www.gnorg/licenses/>.
 * 
 */
package me.fromgate.cpfix;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class CPFListener implements Listener {
	CPFix plg;
	CPFUtil u;

	public CPFListener (CPFix plg){
		this.plg = plg;
		this.u = plg.u;
	}

	@EventHandler(priority=EventPriority.NORMAL)
	public void onJoin (PlayerJoinEvent event){
		if (event.getPlayer().hasMetadata("CPFix-informed")) event.getPlayer().removeMetadata("CPFix-informed", plg);
		u.updateMsg(event.getPlayer());
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onRecodeChat (AsyncPlayerChatEvent event){
		String str = event.getMessage();
		if (plg.fixChat) {
			str = plg.recodeText(str);
			if (!str.equals(event.getMessage())&&plg.informPlayer)	plg.informMessage (event.getPlayer());
		}
		if (plg.whitelistEnable) str = plg.refilterText(str);
		event.setMessage(str);
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onRecodeCmd (PlayerCommandPreprocessEvent event){
		String str = event.getMessage();
		if (plg.fixCmd) {
			str = plg.recodeText(str);
			if (!str.equals(event.getMessage())&&plg.informPlayer)	plg.informMessage (event.getPlayer());
		}
		if (plg.whitelistEnable) str = plg.refilterText(str);
		event.setMessage(str);
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onRecodeConsoleCmd(ServerCommandEvent event) {
		if (plg.inputRecode)
			event.setCommand(plg.recodeToUTF8(event.getCommand(), plg.inputConsoleCodePage));
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onSignChange (SignChangeEvent event){
		if (!plg.fixSign) return;
		for (int i = 0; i<4;i++)
			if (!event.getLine(i).isEmpty()) {
				String str = plg.recodeText(event.getLine(i));
				if (!str.equals(event.getLine(i))){
					if (plg.informPlayer) plg.informMessage (event.getPlayer());
					if (plg.fixSign) event.setLine(i,str);
				}
			}
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onItemPickup (PlayerPickupItemEvent event){
		if (plg.fixNames) plg.fixItemNameAndLore(event.getItem().getItemStack());
		if (!plg.useBookEvents&&plg.fixBooks) plg.fixBook(event.getItem().getItemStack());
	}

    @SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.LOWEST)
	public void onItemInteract (PlayerInteractEvent event){
		if ((event.getPlayer().getItemInHand()==null)||(event.getPlayer().getItemInHand().getType()==Material.AIR)) return;
		if (plg.fixNames) plg.fixItemNameAndLore(event.getPlayer().getItemInHand());
		if (!plg.useBookEvents&&plg.fixBooks) plg.fixBook(event.getPlayer().getItemInHand());
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onSignInteract (PlayerInteractEvent event){
		if (!plg.fixSign) return;
		if ((event.getAction() != Action.LEFT_CLICK_BLOCK)&&(event.getAction()!=Action.RIGHT_CLICK_BLOCK)) return;
		if (!event.getPlayer().hasPermission("cpfix.sign")) return;		
		if ((event.getClickedBlock().getType() != Material.WALL_SIGN)&&(event.getClickedBlock().getType() != Material.SIGN_POST)) return;
		Sign sign = (Sign) event.getClickedBlock().getState();
		plg.fixSign(sign);
	}
}