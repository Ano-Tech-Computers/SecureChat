/*
 * Copyright (C) 2015  Oskar Sveinsen
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package no.atc.osvein.bukkit.schat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.sshd.SshServer;
import org.apache.sshd.common.Factory;
import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class SecureChat extends JavaPlugin {
    public static final String PREFIX = "[SChat] $b";
    
    public static final String FNAME_WELCOME = "welcome.txt";
    
    public static final Charset CHARSET_UNIVERSAL = StandardCharsets.UTF_8;
    
    private final SshServer sshd = SshServer.setUpDefaultServer();
    
    @Override
    public void onEnable() {
	// copy embedded resources to data directory
	this.saveDefaultConfig();
	this.saveResource(FNAME_WELCOME, false);
	
	// read configuration
	int port = this.getConfig().getInt("port");
	
	// read embedded welcome banner for display on server
	BufferedReader welcomeReader = new BufferedReader(this.getTextResource(FNAME_WELCOME));
	try {
	    String line;
	    while ((line = welcomeReader.readLine()) != null)
		this.getLogger().info(line);
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
	
	// set up SSH server
	this.sshd.setPort(port);
	this.sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(new File(this.getDataFolder(), "hostkey.ser").getPath()));
	this.sshd.setShellFactory(new Factory<org.apache.sshd.server.Command>() {
	    @Override
	    public org.apache.sshd.server.Command create() {
		return new SChatShell(SecureChat.this);
	    }
	});
	
	// read server-specific welcome banner for display on client
	Path welcomePath = this.getDataFolder().toPath().resolve(FNAME_WELCOME);
	String welcome;
	try {
	    welcome = new String(Files.readAllBytes(welcomePath), CHARSET_UNIVERSAL);
	    this.sshd.getProperties().put(SshServer.WELCOME_BANNER, welcome);
	}
	catch (IOException e) {
	    this.getLogger().warning("Failed to load welcome banner from file '" + welcomePath + "'.");
	    e.printStackTrace();
	}
	
	// start SSH server
	try {
	    sshd.start();
	}
	catch (IOException e) {
	    this.getLogger().severe("Failed to start SSH server on port " + sshd.getPort());
	    e.printStackTrace();
	    this.setEnabled(false);
	    return;
	}
    }
    
    @Override
    public void onDisable() {
	// stop SSH server if running
	if (!sshd.isClosed()) {
	    this.getLogger().info("Stopping SSH server...");
	    try {
		sshd.stop();
	    }
	    catch (InterruptedException e) {
		this.getLogger().warning("Failed to stop SSH server; forcing...");
		e.printStackTrace();
		
		// force stop
		try {
		    sshd.stop(true);
		}
		catch (InterruptedException e1) {
		    this.getLogger().severe("Failed to force stop SSH server.");
		    e1.printStackTrace();
		}
	    }
	}
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
	if (cmd.getName().equalsIgnoreCase("schat")) {
	    if (args.length <= 0) {
		// TODO
	    }
	}
	
	return false;
    }
    
    public void setPasswordAuthenticator(PasswordAuthenticator passwordAuthenticator) {
	this.sshd.setPasswordAuthenticator(passwordAuthenticator);
    }
    
    public void setPublickeyAuthenticator(PublickeyAuthenticator publickeyAuthenticator) {
	this.sshd.setPublickeyAuthenticator(publickeyAuthenticator);
    }
}
