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

import org.apache.sshd.server.Command;
import org.apache.sshd.server.Environment;
import org.apache.sshd.server.ExitCallback;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * @author osvein <osvein@users.noreply.github.com>
 *
 */
public class SChatShell extends BukkitRunnable implements Command {
    private final Plugin plugin;
    private final long delay = 0L;
    private final long period = 1L;
    
    private BufferedReader in;
    private BufferedWriter out;
    private BufferedWriter err;
    private ExitCallback exit;
    
    private Environment env;
    
    public SChatShell(Plugin plugin) {
	this.plugin = plugin;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
	try {
	    if (!this.in.ready()) return;
	    String line = this.in.readLine();
	    if (line == null) return;
	    this.plugin.getServer().broadcastMessage("<" + env.getEnv().get(Environment.ENV_USER) + "> " + line);
	    this.out.write(line);
	    this.out.newLine();
	    this.out.flush();
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
	this.runTaskTimer(this.plugin, delay, period);
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
