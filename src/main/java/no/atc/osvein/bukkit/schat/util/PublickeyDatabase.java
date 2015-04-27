package no.atc.osvein.bukkit.schat.util;

import java.security.PublicKey;
import java.util.Map;

import org.apache.sshd.server.PublickeyAuthenticator;
import org.apache.sshd.server.session.ServerSession;

/**
 * Provides base functionality for using a database for public key authentication
 * @author osvein <osvein@users.noreply.github.com>
 *
 */
public abstract class PublickeyDatabase implements PublickeyAuthenticator {
    /**
     * Gets all {@linkplain java.security.PublicKey public keys} associated with a user
     * @param username the username of the user whose {@linkplain java.security.PublicKey public keys} are to be returned
     * @return all {@linkplain java.security.PublicKey public keys} associated with a user
     */
    protected abstract Map<String, PublicKey> getAllPublicKeys(String username);
    
    /**
     * Associates a {@linkplain java.security.PublicKey public key} with a user
     * @param username the username of the user with which the {@linkplain java.security.PublicKey public key} is to be associated
     * @param identifier an object to be used to identify the {@linkplain java.security.PublicKey public key} to be associated with the user
     * @param key the {@linkplain java.security.PublicKey public key} to be associated with the user
     * @throws UnsupportedOperationException if the operation is not supported
     */
    public abstract void addPublicKey(String username, String identifier, PublicKey key);
    
    /**
     * Dissociates a {@linkplain java.security.PublicKey public key} from a user
     * @param username the username of the user with which the {@linkplain java.security.PublicKey public key} is to be dissociated
     * @param identifier the object used to identify the {@linkplain java.security.PublicKey public key} to be dissociated with the user
     * @throws UnsupportedOperationException if the operation is not supported
     */
    public abstract void removePublicKey(String username, String identifier);
    
    /**
     * Dissociates all associated {@linkplain java.security.PublicKey public keys} from a user
     * @param username the username of the user from which whose {@linkplain java.security.PublicKey public keys} are to be dissociated
     */
    public void clearPublicKeys(String username) {
	for (String identifier : this.getAllPublicKeys(username).keySet()) {
	    this.removePublicKey(username, identifier);
	}
    }
    
    @Override
    public boolean authenticate(String username, PublicKey key, ServerSession session) {
	for (PublicKey correctKey : this.getAllPublicKeys(username).values()) {
	    if (key.equals(correctKey))
		return true;
	}
	return false;
    }
}
