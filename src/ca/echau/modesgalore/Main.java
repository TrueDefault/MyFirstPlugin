package ca.echau.modesgalore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import ca.echau.modesgalore.Commands.*;
import ca.echau.modesgalore.Commands.Modes.*;
import ca.echau.modesgalore.Listeners.*;
import ca.echau.modesgalore.Runnables.*;

public class Main extends JavaPlugin {
	private final FileConfiguration config = this.getConfig();

	@Override
	public void onEnable() {
		//Registers Commands
		this.getCommand("cointoss").setExecutor(new CointossCommand());
		this.getCommand("damage").setExecutor(new DamageCommand());
		this.getCommand("ef").setExecutor(new EnableFartsCommand(this));
		this.getCommand("ep").setExecutor(new EnablePoopingCommand(this));
		this.getCommand("enablefarts").setExecutor(new EnableFartsCommand(this));
		this.getCommand("enablepooping").setExecutor(new EnablePoopingCommand(this));
		this.getCommand("exarrows").setExecutor(new ExArrowsCommand(this));
		this.getCommand("feed").setExecutor(new FeedCommand());
		this.getCommand("givedirt").setExecutor(new GiveDirtCommand(this));
		this.getCommand("heal").setExecutor(new HealCommand());
		this.getCommand("launchmode").setExecutor(new LaunchModeCommand(this));
		this.getCommand("lm").setExecutor(new LaunchModeCommand(this));
		this.getCommand("meme").setExecutor(new MemeCommand(this));
		this.getCommand("modes").setExecutor(new ModesCommand(this));
		this.getCommand("orelogs").setExecutor(new OreLogsCommand());
		this.getCommand("rainbowarmor").setExecutor(new RainbowArmorCommand(this));
		this.getCommand("rarmor").setExecutor(new RainbowArmorCommand(this));
		this.getCommand("recarea").setExecutor(new RectangleAreaCommand());
		this.getCommand("rectanglearea").setExecutor(new RectangleAreaCommand());
		this.getCommand("reverse").setExecutor(new ReverseCommand());
		this.getCommand("rteleport").setExecutor(new RTeleportCommand());
		this.getCommand("rtp").setExecutor(new RTeleportCommand());
		this.getCommand("setfartdmg").setExecutor(new SetFartDmgCommand(this));
		this.getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
		this.getCommand("spawn").setExecutor(new SpawnCommand(this));
		this.getCommand("tpl").setExecutor(new TPLightningCommand());
		this.getCommand("tplightning").setExecutor(new TPLightningCommand());
		this.getCommand("walkonsmoke").setExecutor(new WalkOnSmokeCommand(this));
		this.getCommand("wos").setExecutor(new WalkOnSmokeCommand(this));

		//Registers Listeners
		this.getServer().getPluginManager().registerEvents(new AntiExplosionOfEntities(this), this);
		this.getServer().getPluginManager().registerEvents(new ExplosiveArrows(this), this);
		this.getServer().getPluginManager().registerEvents(new FartOnSneak(this), this);
		this.getServer().getPluginManager().registerEvents(new LaunchYourselfWithBow(this), this);
		this.getServer().getPluginManager().registerEvents(new LoginMessage(this), this);
		this.getServer().getPluginManager().registerEvents(new MineOreMessage(), this);
		this.getServer().getPluginManager().registerEvents(new PoopOnSneak(this), this);
		this.getServer().getPluginManager().registerEvents(new Respawn(this), this);

		//Runnables
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new RainbowArmor(this), 0L, 2L);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new WalkOnSmoke(this), 0L, 1L);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new ClearOreLogs(), 60 * 60 * 20L, 60 * 60 * 20L);
		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new OreLogsAlmostCleared(), 55 * 60 * 20L, 55 * 60 * 20L);

		//Config
		try {
			this.loadConfig();
			this.saveConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}

		//Creates memes.txt
		this.createMemeFile();
	}

	@Override
	public void onDisable() {}

	//Creates config if one does not exist already.
	public void loadConfig() {
		final File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			this.saveConfig();
			this.configSetDefaults();
			Bukkit.getLogger().info("[ModesGalore] Config file not found! Creating config.");
		} else {
			this.configSetDefaults();
			this.saveConfig();
			Bukkit.getLogger().info("[ModesGalore] Enabling config.");
		}
	}

	//Sets default values of config if they do not exist already.
	public void configSetDefaults() {
		if (!config.contains("DropDirtOnFullInventory")) {
			config.set("DropDirtOnFullInventory", false);
		}
		if (!config.contains("AntiExplosionOfEntities")) {
			config.set("AntiExplosionOfEntities", false);
		}
		if (!config.contains("FartsDamageInHearts")) {
			config.set("FartsDamageInHearts", 5);
		}
	}

	/*
	 * For /meme command
	 * This method checks to see if the memes.txt file can be created. If yes, then it creates the file.
	 * In addition, the method adds the default lines to the file (see writeMemeFileDefaults() below)
	 * 
	 * If the file cannot be created (i.e. it already exists), then the method makes sure the file
	 * contains the default lines. The method does this by reading through the entire file, storing
	 * all of its contents in a list, changing the top 8 lines to the default lines, 
	 * then finally using the list to rewrite the entire file line by line.
	 * 
	 * This is done because if the default lines are changed in a future update, users will automatically
	 * have their memes.txt files updated. Keep in mind that the existence of these default lines are
	 * not crucial to the /meme command running successfully -- the /meme command only starts storing
	 * the contents of a random line to a String object starting from line 8 (i.e. it will never store 
	 * and output a "meme" from the default lines).
	 */
	public void createMemeFile() {
		final File memeFile = new File(getDataFolder(), "memes.txt");
		try {
			if (memeFile.createNewFile()) {
				Bukkit.getLogger().info("[ModesGalore] File memes.txt not found! Creating memes.txt");
				this.writeMemeFileDefaults();
			} else {
				Bukkit.getLogger().info("[ModesGalore] File memes.txt found!");
				Bukkit.getLogger().info("[ModesGalore] Writing defaults to memes.txt");
				try (
					BufferedReader lineCounter = new BufferedReader(new FileReader(memeFile));
					BufferedReader reader = new BufferedReader(new FileReader(memeFile))
				) {
					//Represents the number of lines in the memes.txt file, including the default lines.
					int numLines = 0;
					
					//Reads through all the lines and counts them.
					while (lineCounter.readLine() != null) {
						numLines++;
					}
					
					/* 
					 * If there are less than 8 lines in the file, all of the default lines are
					 * written. The memes.txt file then looks like it was just created, with no
					 * memes. This is done because the /meme command will not work anyway if there
					 * are less than 8 lines -- the memes must start at line 8.
					 */
					if (numLines < 8) {
						this.writeMemeFileDefaults();
					} else {
						//List of all the lines in the memes.txt file.
						final String[] fileLines = new String[numLines];
						
						//Re-reads all of the lines with a second reader
						//Adds each line's contents to the fileLines list.
						for (int i = 0; i < numLines; i++) {
							String line = reader.readLine();
							fileLines[i] = line;
						}

						//Sets the default lines of the file.
						fileLines[0] = "**********IMPORTANT DO NOT DELETE, YOU WILL LOSE MEMES IF YOU DO**********";
						fileLines[1] = "Add your memes starting from line #8.";
						fileLines[2] = "Each line is a meme. Do not put any blank lines in between.";
						fileLines[3] = "If you want multi-line memes, insert the characters \"\\n\" where you want the new line to start.";
						fileLines[4] = "Example: What\'s 1 + 1? \\n2.";
						fileLines[5] = "**********IMPORTANT DO NOT DELETE, YOU WILL LOSE MEMES IF YOU DO**********";
						fileLines[6] = "";

						//Note: BufferedWriter deletes the existing contents of the file.
						//Do not declare it along with the readers in the try-with-resources statement.
						try (BufferedWriter writer = new BufferedWriter(new FileWriter(memeFile))) {
							int lineNumber = 1;
							for (final String s : fileLines) {
								if (lineNumber == 1) {
									writer.write(s);
									lineNumber++;
								} else {
									if (lineNumber < 8) {
										writer.newLine();
										writer.write(s);
										lineNumber++;
									} else {
										/*
										 * Removes empty lines starting from line 8
										 *   (i.e. where the memes start).
										 * If there is a empty line, do not add to the line number.
										 * This makes sure lineNumber is an accurate representation
										 * of what line number the method is printing to.
										 */
										if (!s.equals("")) {
											writer.newLine();
											writer.write(s);
											lineNumber++;
										}
									}
								}
							}
						}
						Bukkit.getLogger().info("[ModesGalore] Successfully wrote defaults to memes.txt!");
					}
				} catch (IOException e) {
					Bukkit.getLogger().severe("[ModesGalore] Failed to write to memes.txt");
					e.printStackTrace();
				}
			}	
		} catch (IOException e) {
			Bukkit.getLogger().severe("[ModesGalore] Failed to create memes.txt. See error below.");
			e.printStackTrace();
		}
	}

	//Writes default lines in memes.txt file.
	//These lines are written at the top of the file.
	public void writeMemeFileDefaults() {
		final File memeFile = new File(getDataFolder(), "memes.txt");
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(memeFile))) {
			writer.write("**********IMPORTANT DO NOT DELETE, YOU WILL LOSE MEMES IF YOU DO**********");
			writer.newLine();
			writer.write("Add your memes starting from line #8.");
			writer.newLine();
			writer.write("Each line is a meme. Do not put any blank lines in between.");
			writer.newLine();
			writer.write("If you want multi-line memes, insert the characters \"\\n\" where you want the new line to start.");
			writer.newLine();
			writer.write("Example: What\'s 1 + 1? \\n2.");
			writer.newLine();
			writer.write("**********IMPORTANT DO NOT DELETE, YOU WILL LOSE MEMES IF YOU DO**********");
			writer.newLine();
			writer.newLine();	//creates empty line at line 7.
			Bukkit.getLogger().info("[ModesGalore] Successfully wrote defaults to memes.txt!");
		} catch (IOException e) {
			Bukkit.getLogger().severe("[ModesGalore] Failed to write defaults to memes.txt");
			e.printStackTrace();
		}
	}
}
