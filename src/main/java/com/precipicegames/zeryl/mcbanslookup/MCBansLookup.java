package com.precipicegames.zeryl.mcbanslookup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;
import org.json.JSONObject;

/**
 *
 * @author Zeryl
 */
public class MCBansLookup extends JavaPlugin {

    String perms = "com.precipicegames.mcbanlookup";
    private String url = "http://72.10.39.172/v2/";
    public FileConfiguration config;


    public void onEnable() {
        PluginManager pm = getServer().getPluginManager();
        PluginDescriptionFile pdf = this.getDescription();
        
        config = getConfig();
        
        if(!config.isString("apikey"))
            System.out.println(pdf.getName() + ": API Key not set, plugin will not function properly.");
        else
            System.out.println(pdf.getName() + " is now enabled.");        
    }

    public void onDisable() {
        PluginDescriptionFile pdf = this.getDescription();
        System.out.println(pdf.getName() + " is now disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if(!config.isString("apikey")) {
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("mclookup")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (player.hasPermission(perms)) {
                    if (args.length > 0) {
                        String name = args[0];
                        playerLookup(name, player);
                    } else {
                        player.sendMessage("Must include a name (/mclookup [name]).");
                    }
                } else {
                    player.sendMessage("Cry for me Bukkit, cry!");
                }
            }
        }
        return true;
    }

    public void playerLookup(String name, Player player) {
        try {
            String data = URLEncoder.encode("player", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
            data = data + "&" + URLEncoder.encode("admin", "UTF-8") + "=" + URLEncoder.encode(player.getDisplayName(), "UTF-8");
            data = data + "&" + URLEncoder.encode("exec", "UTF-8") + "=" + URLEncoder.encode("playerLookup", "UTF-8");
            URL abc = new URL(this.url + config.getString("apikey"));

            URLConnection conn = abc.openConnection();
            conn.setDoOutput(true);

            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

            wr.write(data);
            wr.flush();

            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String line;

            while ((line = rd.readLine()) != null) {
                JSONObject json = new JSONObject(line);

                if (json.getJSONArray("global").length() > 0) {
                    int j = 0;
                    for (int i = 0; i < json.getJSONArray("global").length(); i++) {
                        j++;
                        player.sendMessage(ChatColor.WHITE + "[MCBans]: " + ChatColor.RED + json.getJSONArray("global").getString(i));
                        if(j == 3) {
                            break;
                        }
                    }
                } else {
                    player.sendMessage("No information available.");
                }
            }
        } catch (Exception ex) {
            player.sendMessage("Something failed during lookup: ");
            player.sendMessage(ex.toString());
        }
    }
}