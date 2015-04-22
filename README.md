# SecureChat
Secure remote chat on Bukkit via SSH

## Required libraries
- [Bukkit](http://bukkit.org/) or [Spigot-API](http://www.spigotmc.org/) *(compile-time only)*: `spigot-1.8.3.jar` recommended
- [Apache Mina SSHD](https://mina.apache.org/sshd-project/): `sshd-core-0.14.0.jar` recommended

## Using the binary
### Installing into CraftBukkit/Spigot
The binary for the latest release can be found [here][release_latest] along some release notes. Older releases and pre-releases can be found [here][releases].

[release_latest]: ../../releases/latest
[releases]: ../../releases

## Using the source code
### Cloning in Eclipse IDE with EGit
The source code can be cloned in [Eclipse IDE][eclipse_ide] with [EGit][egit] using the method described [here][egit_clone].

After cloning, some required libraries must be added to the build path. The build path is already configured to look for libraries in the `lib/` directory inside the project directory. For the required libraries, see [Required libraries](#required-libraries)

[eclipse_ide]: http://www.eclipse.org/ide/
[egit]: http://www.eclipse.org/egit/
[egit_clone]: http://www.vogella.com/tutorials/EclipseGit/article.html#clone_respository
