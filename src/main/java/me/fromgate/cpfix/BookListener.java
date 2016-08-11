package me.fromgate.cpfix;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;

public class BookListener implements Listener {
	
	CPFix plg = CPFix.getPlugin();
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onBookEdit (PlayerEditBookEvent event){
		if (plg.fixBooks) event.setNewBookMeta(plg.fixBook(event.getNewBookMeta()));
	}


}
