package no.atc.osvein.bukkit.schat.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.sshd.server.PasswordAuthenticator;
import org.apache.sshd.server.session.ServerSession;

/**
 * Provides base functionality for using a database for password authentication
 * @author osvein <osvein@users.noreply.github.com>
 *
 */
public abstract class PasswordDatabase implements PasswordAuthenticator {
    /**
     * A secure {@link javax.crypto.SecretKeyFactory} algorithm
     * @see javax.crypto.SecretKeyFactory#getInstance(String)
     */
    public static final String SECURE_ALGORITHM = "PBKDF2WithHmacSHA1";
    /**
     * A secure {@link javax.crypto.SecretKeyFactory} salt length in bytes
     * @see javax.crypto.spec.PBEKeySpec
     */
    public static final int SECURE_SALTLENGTH = 16;
    /**
     * A secure {@link javax.crypto.SecretKeyFactory} iteration count
     * @see javax.crypto.spec.PBEKeySpec
     */
    public static final int SECURE_ITERATIONCOUNT = 65536;
    /**
     * A secure {@link javax.crypto.SecretKey} length in bytes
     * @see javax.crypto.spec.PBEKeySpec
     */
    public static final int SECURE_KEYLENGTH = 128;
    
    private final SecretKeyFactory encoder;
    private final int saltLength;
    private final Random saltGenerator;
    private final int iterationCount;
    private final int keyLength;
    
    /**
     * Constructs a new password database
     * @param encoder the {@link javax.crypto.SecretKeyFactory} to be used for encoding passwords
     * @param saltLength the length of salts in bytes
     * @param saltGenerator the {@link java.util.Random} to be used for generating salts
     * @param iterationCount the iteration count
     * @param keyLength the key length
     * @see javax.crypto.spec.PBEKeySpec
     */
    public PasswordDatabase(SecretKeyFactory encoder, int saltLength, Random saltGenerator, int iterationCount, int keyLength) {
	this.encoder = encoder;
	this.saltLength = saltLength;
	this.saltGenerator = saltGenerator;
	this.iterationCount = iterationCount;
	this.keyLength = keyLength;
    }
    
    /**
     * Constructs a new secure password database
     * The encoder is specified by {@link no.atc.osvein.bukkit.schat.util.PasswordDatabase#SECURE_ALGORITHM SECURE_ALGORITHM}
     * The salt length is specified by {@link no.atc.osvein.bukkit.schat.util.PasswordDatabase#SECURE_SALTLENGTH SECURE_SALTLENGTH}
     * The salt generator is a {@link java.security.SecureRandom#SecureRandom() SecureRandom}
     * The iteration count is specified by {@link no.atc.osvein.bukkit.schat.util.PasswordDatabase#SECURE_ITERATIONCOUNT SECURE_ITERATIONCOUNT}
     * The key length is specified by {@link no.atc.osvein.bukkit.schat.util.PasswordDatabase#SECURE_KEYLENGTH SECURE_KEYLENGTH}
     * @throws NoSuchAlgorithmException see {@link javax.crypto.SecretKeyFactory#getInstance(String)}
     */
    public PasswordDatabase() throws NoSuchAlgorithmException {
	this(SecretKeyFactory.getInstance(SECURE_ALGORITHM), SECURE_SALTLENGTH, new SecureRandom(), SECURE_ITERATIONCOUNT, SECURE_KEYLENGTH);
    }

    /**
     * Gets the {@link javax.crypto.SecretKeyFactory} to be used for encoding passwords
     * @return the {@link javax.crypto.SecretKeyFactory} to be used for encoding passwords
     */
    public SecretKeyFactory getEncoder() {
	return this.encoder;
    }
    
    /**
     * Gets the length of salts in bytes
     * @return the length of salts in bytes
     * @see javax.crypto.spec.PBEKeySpec
     */
    public int getSaltLength() {
	return this.saltLength;
    }
    
    /**
     * Gets the the iteration count
     * @return the iteration count
     * @see javax.crypto.spec.PBEKeySpec#getIterationCount()
     */
    public int getIterationCount() {
	return this.iterationCount;
    }
    
    /**
     * Gets the key length
     * @return the key length
     * @see javax.crypto.spec.PBEKeySpec#getKeyLength()
     */
    public int getKeyLength() {
	return this.keyLength;
    }
    
    /**
     * Sets a password for a user
     * @param username the username of the user for which the password is set
     * @param password the password to set for the user
     */
    public void setPassword(String username, String password) {
	byte[] salt = new byte[this.getSaltLength()];
	this.getSaltGenerator().nextBytes(salt);
	this.setSalt(username, salt);
	this.setPassword(username, this.encodePassword(username, password));
    }
    
    @Override
    public boolean authenticate(String username, String password, ServerSession session) {
	return this.encodePassword(username, password).equals(this.getPassword(username));
    }
    
    /**
     * Gets the {@link java.util.Random} to be used for generating salts
     * @return the {@link java.util.Random} to be used for generating salts
     */
    protected Random getSaltGenerator() {
	return this.saltGenerator;
    }
    
    /**
     * Gets the encoded password set for a user
     * @param username the username of the user whose encoded password is returned
     * @return the encoded password set for the user
     */
    protected abstract byte[] getPassword(String username);
    
    /**
     * Sets a password for a user
     * @param username the username of the user for which the password is set
     * @param password the encoded password to set for the user
     */
    protected abstract void setPassword(String username, byte[] password);
    
    protected abstract byte[] getSalt(String username);
    
    protected abstract void setSalt(String username, byte[] salt);
    
    /**
     * Encodes a password for a user
     * @param username the username of the user for which the password is encoded
     * @param password the password to encode
     * @return the encoded password
     */
    protected byte[] encodePassword(String username, String password) {
	byte[] salt = this.getSalt(username);
	PBEKeySpec keySpec = null;
	try {
	    keySpec = new PBEKeySpec(password.toCharArray(), salt, this.getIterationCount(), this.getKeyLength());
	    SecretKey key = this.getEncoder().generateSecret(keySpec);
	    return key.getEncoded();
	}
	catch (InvalidKeySpecException e) {
	    throw new IllegalArgumentException(e);
	}
	finally {
	    if (keySpec != null)
		keySpec.clearPassword();
	}
    }
}
