/*  
 *  CPFix, Minecraft bukkit plugin
 *  (c)2013-2014, fromgate, fromgate@gmail.com
 *  http://dev.bukkit.org/server-mods/cpfix/
 *    
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

import java.io.BufferedReader;

import sun.misc.SharedSecrets;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;


public class CPFix extends JavaPlugin {
    static CPFix instance;
	CPFUtil u;
	CPFListener listener;
	BookListener listenerBooks;
	boolean useBookEvents;

	// Конфигурация
	boolean versionCheck = true;
	String language = "russian";
	boolean languageSave=false;
	String systemCodePage = "UTF8";
	String charWrong = "ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿ¸¨";
	String charFixed   = "АБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдежзийклмнопрстуфхцчшщъыьэюяёЁ";
	boolean fixChat = true;
	boolean fixCmd = true;
	boolean fixSign = true;
	boolean fixBooks = true;
	boolean fixNames = true;
	boolean informPlayer = true;

	// Output recoding
	boolean consoleRecode=true;
	String consoleCodePage = "CP866";
	boolean logRecode=true;
	String logCodePage = "CP1251";

	// Input recoding
	boolean inputRecode = false;
	String inputConsoleCodePage = "CP866";

	// Whitelist characters
	boolean whitelistEnable = true;
	String whitelistChars =" !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_¸abcdefghijklmnopqrstuvwxyz{|}~АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЫЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
	String whitelistReplaceChar = "_";

	@Override
	public void onEnable() {
	    instance = this;
		loadCfg();
		saveCfg();
		u = new CPFUtil(this, versionCheck, languageSave, language, "cpfix", "CPFix", "cpfix", "&b[&3CPFix&b]&f ");
		listener = new CPFListener (this);
		getCommand("cpfix").setExecutor(u);
		getServer().getPluginManager().registerEvents(listener, this);
		useBookEvents = isBookEventsImplemented();//BookVersion.isBookEventsImplemented();
		if (useBookEvents){
			listenerBooks = new BookListener();
			getServer().getPluginManager().registerEvents(listenerBooks,this);
		}
		setConsoleAndLogCodePage();
		

		try {
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
		} catch (IOException e) {
		}		
	}

	public void saveCfg() {
		getConfig().set("general.check-updates", versionCheck);
		getConfig().set("general.language",language);
		getConfig().set("general.language-save",languageSave);
		getConfig().set("white-list.enable",whitelistEnable);
		getConfig().set("white-list.replace",whitelistReplaceChar);
		getConfig().set("code-page.chat-fix-enable", fixChat);
		getConfig().set("code-page.command-fix-enable", fixCmd);
		getConfig().set("code-page.sign-fix-enable", fixSign);
		getConfig().set("code-page.book-fix-enable", fixBooks);
		getConfig().set("code-page.lore-fix-enable", fixNames);
		getConfig().set("code-page.inform-player", informPlayer);
		getConfig().set("output-recode.console.enable",consoleRecode);
		getConfig().set("output-recode.console.code-page",consoleCodePage);
		getConfig().set("output-recode.server-log.enable",logRecode);
		getConfig().set("output-recode.server-log.code-page",logCodePage);
		getConfig().set("input-recode.enable",inputRecode);
		getConfig().set("input-recode.code-page",inputConsoleCodePage);
		saveConfig();
		saveCharFile();
	}

	public void loadCharFile(){
		File f = new File (this.getDataFolder()+File.separator+"characters.txt");
		if (f.exists()){
			try {
				BufferedReader bfr = new BufferedReader(new InputStreamReader (new FileInputStream (f),"UTF8"));
				String ln = bfr.readLine(); 
				if (!ln.isEmpty()) charWrong = ln;
				ln = bfr.readLine(); //second line
				if (!ln.isEmpty()) charFixed = ln;
				ln = bfr.readLine(); // third line
				if (!ln.isEmpty()) whitelistChars  = ln;
				bfr.close();
			} catch (Exception e) {
			}
			return;
		}
	}

	public void saveCharFile(){
		File f = new File (this.getDataFolder()+File.separator+"characters.txt");
		if (f.exists()) f.delete();
		try {
			f.createNewFile();
			BufferedWriter bwr = new BufferedWriter (new OutputStreamWriter (new FileOutputStream (f), "UTF8"));
			bwr.write(charWrong+"\n");
			bwr.write(charFixed+"\n");
			bwr.write(whitelistChars+"\n");
			bwr.flush();
			bwr.close();
		} catch (Exception e) {
		}
	}

	public void loadCfg() {
		versionCheck = getConfig().getBoolean("general.check-updates", true);
		language = getConfig().getString("general.language","russian");
		languageSave=getConfig().getBoolean("general.language-save",false);
		fixChat = getConfig().getBoolean("code-page.chat-fix-enable", true);
		fixCmd = getConfig().getBoolean("code-page.command-fix-enable", true);
		fixSign = getConfig().getBoolean("code-page.sign-fix-enable", true);
		fixBooks = getConfig().getBoolean("code-page.book-fix-enable", true);
		fixNames = getConfig().getBoolean("code-page.lore-fix-enable", false);
		informPlayer = getConfig().getBoolean("code-page.inform-player", true);
		consoleRecode=getConfig().getBoolean("output-recode.console.enable",false);
		consoleCodePage = getConfig().getString("output-recode.console.code-page",getSystemConsoleCodepage());
		logRecode=getConfig().getBoolean("output-recode.server-log.enable",false);
		logCodePage=getConfig().getString("output-recode.server-log.code-page",getSystemConsoleCodepage());
		inputRecode=getConfig().getBoolean("input-recode.enable",false);
		inputConsoleCodePage=getConfig().getString("input-recode.code-page",getSystemConsoleCodepage());
		whitelistEnable = getConfig().getBoolean("white-list.enable",true);
		whitelistReplaceChar =getConfig().getString("white-list.replace","_");
		loadCharFile();
	}

	public String recodeText (String str){
		String nstr = str;
		if (!str.isEmpty()){
			for (int i = 0; i<charWrong.length();i++)
				nstr = nstr.replace(charWrong.charAt(i), charFixed.charAt(i));
		}
		return nstr;
	}

	public String refilterText (String str){
		if (str.isEmpty()) return str;
		String nstr = str;
		for (int i = 0; i<str.length();i++)
			if (!whitelistChars.contains(String.valueOf(str.charAt(i))))
				nstr = nstr.replace(String.valueOf(str.charAt(i)), whitelistReplaceChar);
		return nstr;
	}

	public void informMessage(CommandSender sender){
		if (sender instanceof Player){
			Player p = (Player) sender;
			if (p.hasMetadata("CPFix-informed")) return;
			u.printMSG(p, "msg_wrongcp",'c');
			p.setMetadata("CPFix-informed", new FixedMetadataValue (this, true));
		}
	}

	public void fixItemNameAndLore (ItemStack item){
		ItemMeta im = item.getItemMeta();
		if (im.hasDisplayName())
			im.setDisplayName(recodeText(im.getDisplayName()));
		if (im.hasLore()){
			List<String> il = im.getLore();
			if (il.size()>0)
				for (int i = 0; i<il.size();i++)
					il.set(i, recodeText(il.get(i)));
		}
		item.setItemMeta(im);
	}

	public void fixBook (ItemStack bookItem){
		if (bookItem == null) return;
		if ((bookItem.getType()!=Material.BOOK_AND_QUILL)&&(bookItem.getType()!=Material.WRITTEN_BOOK)) return;
		bookItem.setItemMeta(fixBook ((BookMeta)bookItem.getItemMeta()));
	}
	
	public BookMeta fixBook (BookMeta bookMeta){
		if (bookMeta.hasAuthor()) bookMeta.setAuthor(recodeText(bookMeta.getAuthor()));
		if (bookMeta.hasTitle()) bookMeta.setTitle(recodeText (bookMeta.getTitle()));
		if (bookMeta.hasPages()){
			List<String> pages = recodeList(bookMeta.getPages());
			bookMeta.setPages(pages);
		}
		return bookMeta;
	}

	public void fixSign (Sign sign){
		for (int i = 0; i<4; i++)
			if (!sign.getLine(i).isEmpty())
				sign.setLine(i, recodeText(sign.getLine(i)));
		sign.update();
	}

	public List<String> recodeList(List<String> lines){
		List<String> ln = new ArrayList<String>();
		if (!lines.isEmpty())
			for (int i = 0; i<lines.size();i++)
				ln.add(recodeText(lines.get(i)));
		return ln;
	}

	// Определение кодировки системной консоли
	public String getSystemConsoleCodepage(){
		
		if (System.console() != null) systemCodePage = SharedSecrets.getJavaIOAccess().charset().name();
		return systemCodePage;
	}

	// Определение кодировки консоли сервера (по идее работает, только если выставлена принудительно)
	public String getServerConsoleCodepage(){
		Logger log = Logger.getLogger("Minecraft");
		Handler[] hs = log.getParent().getHandlers();
		try {
			for (Handler h : hs)
				if ((h instanceof ConsoleHandler)&&(h.getEncoding() != null)) return h.getEncoding();
		} catch (Exception e) {
		}
		return u.getMSGnc("unknown");
	}

	// Определение кодировки журнального файла (по идее работает, только если выставлена принудительно)
	public String getLogCodepage(){
		Logger log = Logger.getLogger("Minecraft");
		Handler[] hs = log.getParent().getHandlers();
		try {
			for (Handler h : hs)
				if ((h instanceof FileHandler)&&(h.getEncoding() != null)) return h.getEncoding();
		} catch (Exception e) {
		}
		return u.getMSGnc("unknown");
	}

	public boolean setConsoleAndLogCodePage(){
		Logger log = Logger.getLogger("Minecraft");
		Handler[] hs = log.getParent().getHandlers();
		if (!(logRecode||consoleRecode)) return false;
		if (hs.length==0) return false;
		try {
			for (Handler h : hs){
				if (logRecode&&(h instanceof FileHandler)) h.setEncoding(logCodePage);
				else if (consoleRecode&&(h instanceof ConsoleHandler)) h.setEncoding(consoleCodePage);
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public String recodeToUTF8(String str, String cp){
		try {
			return new String (str.getBytes(),cp);
		} catch (Exception e) {
		}
		return str;
	}

	public void autoConfig(){
		String cp = getSystemConsoleCodepage();
		consoleRecode = true;
		consoleCodePage = cp;
		inputRecode = true;
		inputConsoleCodePage = cp;
		if (!logRecode){ 
			logCodePage = cp;
			logRecode=true;
		}
		saveCfg();
	}
	
	public boolean isBookEventsImplemented(){
		try {
			if (Class.forName("org.bukkit.event.player.PlayerEditBookEvent") != null) {
				return true;
			}
		} catch (ClassNotFoundException e) {
		}
		return false;
	}

	public static CPFix getPlugin() {
		return instance;
	}
}
