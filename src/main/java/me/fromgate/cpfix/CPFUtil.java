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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;



public class CPFUtil extends FGUtilCore implements CommandExecutor {
	CPFix plg;

	public CPFUtil(CPFix plugin, boolean vcheck, boolean savelng, String language, String devbukkitname, String version_name, String plgcmd, String px){
		super (plugin, savelng, language, plgcmd,devbukkitname);
		this.initUpdateChecker(version_name, "53402", devbukkitname, vcheck);
		this.plg = plugin;
		FillMSG();
		if (savelng) this.SaveMSG();
	}

	public void PrintCfg(CommandSender p){
		printMsg(p, "&6&lCPFix "+des.getVersion()+" &r&6| "+getMSG("msg_config",'6'));
		printMSG(p, "cfg_cpfrom");
		printMsg(p, "&6"+plg.charWrong);
		printMSG(p, "cfg_cpto");
		printMsg(p, "&6"+plg.charFixed);
		printMSG(p, "cfg_fixchatcmd",EnDis(plg.fixChat),EnDis(plg.fixCmd));
		printMSG(p, "cfg_fixnsignbook",EnDis(plg.fixSign),EnDis(plg.fixBooks));
		printMSG(p, "cfg_fixnamelore",EnDis(plg.fixNames));
		printMSG(p, "cfg_inform",EnDis(plg.informPlayer));
		printMSG(p, "cfg_syscodepage", plg.getSystemConsoleCodepage());
		printMSG(p, "cfg_outconsole", EnDis(plg.consoleRecode),plg.getServerConsoleCodepage());
		printMSG(p, "cfg_outlogfile", EnDis(plg.logRecode),plg.getLogCodepage());
		printMSG(p, "cfg_inconsole", EnDis(plg.inputRecode),plg.inputConsoleCodePage);
		printMSG(p, "cfg_wlstate",EnDis(plg.whitelistEnable),plg.whitelistReplaceChar);
		printMSG(p, "cfg_whitelist",'a','6',plg.whitelistChars);
		if (p instanceof Player) {
			Player pp = (Player) p;
			printEnDis (p, "cfg_permsign",pp.hasPermission("cpfix.sign"));
		}
	}

	public void FillMSG(){
		msg.clear();
		addMSG ("msg_config", "Конфигурация");
		addMSG ("msg_outdated", "Версия %1% устарела!");
		addMSG ("msg_pleasedownload", "Пожалуйста обновите плагина до новой (%1%) версии:");
		addMSG ("enabled", "вкл.");
		msg.put("enabled", ChatColor.DARK_GREEN+msg.get("enabled"));
		addMSG ("disabled", "выкл.");
		msg.put("disabled", ChatColor.RED+msg.get("disabled"));
		addMSG ("msg_wrongcp", "Введенная строка содержит символы неверной кодировки. Рекомендуем обновить файлы библиотеки LWJGL в Вашем клиенте Minecraft.");
		addMSG ("msg_reloaded", "Настройки CPFix успешно перезагружены");
		addMSG ("cfg_cpfrom","Неправильные символы:");
		addMSG ("cfg_cpto","Правильные символы:");
		addMSG ("cfg_fixchatcmd","Перекодировка текста в чате: %1% в командах: %2%");
		addMSG ("cfg_fixnsignbook","Перекодировка текста на табличках: %1% в книгах: %2%");
		addMSG ("cfg_fixnamelore","Перекодировка текста в названиях и описаниях предметов: %1%");
		addMSG ("cfg_inform","Информирование игроков о неверной версии LWJGL: %1%");
		addMSG ("cfg_permsign","Исправление текста на табличках по клику (персонально)");
		addMSG ("unknown", "не определена");
		addMSG ("cfg_syscodepage", "Системная кодировка консоли: %1%");
		addMSG ("cfg_outconsole", "Перекодирование вывода в консоль: %1% Кодировка: %2%");
		addMSG ("cfg_outlogfile", "Перекодирование журнального файла: %1% Кодировка: %2%");
		addMSG ("cfg_inconsole", "Перекодирование ввода в консоли: %1% Кодировка: %2%");
		addMSG ("msg_autoconfig", "Произведена автоматическая настройка под кодировку консоли %1%. Необходимо перезапустить сервер!");
		addMSG ("cfg_wlstate", "Фильтр символов: %1% Символ для замены: %2%");
		addMSG ("cfg_whitelist", "Разрешенные символы: %1%");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if ((sender instanceof Player)&&(!sender.hasPermission("cpfix.config"))) return false;
		if (args.length == 0){
			PrintCfg(sender);
		} else if ((args.length==1)&&(args[0].equalsIgnoreCase("reload"))){
			plg.reloadConfig();
			plg.loadCfg();
			printMSG(sender, "msg_reloaded");
		} else if ((args.length==1)&&(args[0].equalsIgnoreCase("auto"))){
			plg.autoConfig();
			printMSG(sender, "msg_autoconfig",'e','6',plg.getSystemConsoleCodepage());
		} else return false;
		return true;
	}
}
