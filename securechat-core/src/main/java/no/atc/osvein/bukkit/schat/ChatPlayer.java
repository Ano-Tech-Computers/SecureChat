/*
 * Copyright (c) 2015 osvein
 */
package no.atc.osvein.bukkit.schat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import no.atc.osvein.bukkit.schat.util.decorator.PlayerDecorator;

import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author osvein <osvein@users.noreply.github.com>
 *
 */
public class ChatPlayer extends PlayerDecorator {
    private final ChatShell shell = new ChatShell();
    
    private final SecureChat schat;
    
    public ChatPlayer(Player decoratedPlayer, SecureChat securechat) {
	super(decoratedPlayer);
	this.schat = securechat;
    }
    
    public ChatShell getShell() {
	return this.shell;
    }
    
    public SecureChat getSecureChat() {
	return this.schat;
    }
    
    @Override
    public Player getPlayer() {
	return this;
    }
    
    @Override
    public boolean isOnline() {
	return this.getSecureChat().getUsers().contains(this);
    }
    
    @Override
    public InetSocketAddress getAddress() {
	return this.getShell().env.
    }
    
    
    
    
    
    
    
    
    
    
    public class ChatShell extends BukkitRunnable implements Command {
	private final long delay = 0L;
	private final long period = 1L;
	
	public BufferedReader in;
	public BufferedWriter out;
	public BufferedWriter err;
	public ExitCallback exit;
	
	public Environment env;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
	    try {
		// avoid blocking
		if (!this.in.ready()) return;
		
		// reflect to output stream
		int c;
		while ((c = this.in.read()) != -1) {
		    this.out.write(c);
		}
		this.out.flush();
		
		// chat if line is complete
		String line = this.in.readLine();
		if (line != null) {
		    ChatPlayer.this.chat(line);
		}
	    }
	    catch (IOException e) {
		e.printStackTrace();
	    }
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sshd.server.Command#start(org.apache.sshd.server.Environment)
	 */
	@Override
	public void start(Environment env) throws IOException {
	    this.env = env;
	    this.runTaskTimer(ChatPlayer.this.getSecureChat(), delay, period);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sshd.server.Command#destroy()
	 */
	@Override
	public void destroy() {
	    this.cancel();
	    this.env = null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sshd.server.Command#setInputStream(java.io.InputStream)
	 */
	@Override
	public void setInputStream(InputStream in) {
	    this.in = new BufferedReader(new InputStreamReader(in));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sshd.server.Command#setOutputStream(java.io.OutputStream)
	 */
	@Override
	public void setOutputStream(OutputStream out) {
	    this.out = new BufferedWriter(new OutputStreamWriter(out));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sshd.server.Command#setErrorStream(java.io.OutputStream)
	 */
	@Override
	public void setErrorStream(OutputStream err) {
	    this.err = new BufferedWriter(new OutputStreamWriter(err));
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.sshd.server.Command#setExitCallback(org.apache.sshd.server.ExitCallback)
	 */
	@Override
	public void setExitCallback(ExitCallback callback) {
	    this.exit = callback;
	}
	
    }
    
}
