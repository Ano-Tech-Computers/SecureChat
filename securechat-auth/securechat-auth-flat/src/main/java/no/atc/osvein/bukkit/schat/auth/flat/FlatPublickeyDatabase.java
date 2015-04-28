package no.atc.osvein.bukkit.schat.auth.flat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

import no.atc.osvein.bukkit.schat.util.PublickeyDatabase;

public class FlatPublickeyDatabase extends PublickeyDatabase {
    public static final String DIRNAME_KEY = "keys";
    public static final String GLOB_PUBLICKEY = "*.pub";
    
    private final Path root;
    
    public FlatPublickeyDatabase(Path root) {
	this.root = root;
    }
    
    public Path getDatabaseRoot() {
	return this.root;
    }
    
    @Override
    protected Map<String, PublicKey> getAllPublicKeys(String username) {
	Map<String, PublicKey> keymap = new HashMap<String, PublicKey>();
	
	Path dir = this.getDatabaseRoot().resolve(username).resolve(DIRNAME_KEY);
	
	try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir, GLOB_PUBLICKEY)) {
	    for (Path file : dirStream) {
		ObjectInputStream fileStream = new ObjectInputStream(Files.newInputStream(file));
		PublicKey key = (PublicKey) fileStream.readObject();
		keymap.put(file.getFileName().toString(), key);
	    }
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
	catch (ClassNotFoundException | ClassCastException e) {
	    // TODO Log a warning
	    e.printStackTrace();
	}
	
	return keymap;
    }

    @Override
    public void addPublicKey(String username, String identifier, PublicKey key) {
	Path file = this.getDatabaseRoot().resolve(username).resolve(DIRNAME_KEY).resolve(identifier);
	
	try (ObjectOutputStream fileStream = new ObjectOutputStream(Files.newOutputStream(file))) {
	    fileStream.writeObject(key);
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void removePublicKey(String username, String identifier) {
	Path file = this.getDatabaseRoot().resolve(username).resolve(DIRNAME_KEY).resolve(identifier);
	
	try {
	    Files.deleteIfExists(file);
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
