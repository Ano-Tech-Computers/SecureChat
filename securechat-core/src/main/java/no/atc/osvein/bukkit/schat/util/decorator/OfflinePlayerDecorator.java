package no.atc.osvein.bukkit.schat.util.decorator;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public abstract class OfflinePlayerDecorator implements OfflinePlayer {
    protected final OfflinePlayer decoratedOfflinePlayer;
    
    public OfflinePlayerDecorator(OfflinePlayer decoratedOfflinePlayer) {
	this.decoratedOfflinePlayer = decoratedOfflinePlayer;
    }
    
    @Override
    public boolean isOp() {
	// TODO Auto-generated method stub
	return false;
    }
    
    @Override
    public void setOp(boolean arg0) {
	// TODO Auto-generated method stub
	
    }
    
    @Override
    public Map<String, Object> serialize() {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public Location getBedSpawnLocation() {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public long getFirstPlayed() {
	// TODO Auto-generated method stub
	return 0;
    }
    
    @Override
    public long getLastPlayed() {
	// TODO Auto-generated method stub
	return 0;
    }
    
    @Override
    public String getName() {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public Player getPlayer() {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public UUID getUniqueId() {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public boolean hasPlayedBefore() {
	// TODO Auto-generated method stub
	return false;
    }
    
    @Override
    public boolean isBanned() {
	// TODO Auto-generated method stub
	return false;
    }
    
    @Override
    public boolean isOnline() {
	// TODO Auto-generated method stub
	return false;
    }
    
    @Override
    public boolean isWhitelisted() {
	// TODO Auto-generated method stub
	return false;
    }
    
    @Override
    public void setBanned(boolean arg0) {
	// TODO Auto-generated method stub
	
    }
    
    @Override
    public void setWhitelisted(boolean arg0) {
	// TODO Auto-generated method stub
	
    }
    
}
