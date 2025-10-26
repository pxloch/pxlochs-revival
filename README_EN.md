# 💀 Hardcore Revival Plugin

> [Türkçe](README.md) | **English**

A dynamic revival system and automatic world reset plugin for Minecraft hardcore servers.

[![Minecraft Version](https://img.shields.io/badge/Minecraft-1.20.1-brightgreen.svg)](https://www.spigotmc.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)

## ✨ Features

### 🎮 Revival System
- **3 Revival Rights** - Each player can be revived 3 times
- **Spectator Mode** - Dead players become spectators until revived
- **Command-Based** - Use `/revive <player>` to bring teammates back to life

### 💎 Dynamic Cost System
Revival cost adapts to game progression:

| Stage | Cost | Trigger |
|-------|------|---------|
| **Early Game** | 20 Iron Ingots | Default starting cost |
| **Mid Game** | 5 Diamonds | Unlocked when someone gets "Diamonds!" achievement |
| **Late Game** | 5+ Diamonds | +1 diamond every 2 in-game nights |

### 🌍 World Reset
- **Automatic Reset** - When both players permanently die (0 revival rights)
- **Dual Death Reset** - When both players die simultaneously
- **Complete Wipe** - Deletes Overworld, Nether, and End dimensions
- **Fresh Start** - Server shuts down for manual restart with clean worlds

### 🌐 Multi-Language Support
- Default languages: **Turkish** and **English**
- Easily add new languages through config file
- All game messages are customizable

### 📊 Progress Tracking
- Tracks nights passed since diamond achievement
- Saves player revival rights across server restarts
- Persistent configuration system

## 📦 Installation

### Requirements
- **Minecraft Server**: 1.20.1 (Paper/Spigot/Bukkit)
- **Java**: 17 or higher
- **Maven**: For building from source

### Quick Setup

1. **Download or Build**
   ```bash
   git clone https://github.com/pxloch/pxlochs-revival.git
   cd pxlochs-revival
   mvn clean package
   ```

2. **Install Plugin**
   - Copy `target/PxlochsRevival-1.0-SNAPSHOT.jar` to your server's `plugins/` folder
   - Restart or reload your server

3. **Configure** (Optional)
   - Plugin auto-generates `plugins/PxlochsRevival/config.yml`
   - Customize language settings and messages as desired

## 🎯 Usage

### Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/revive <player>` | Revive a dead player | Default |
| `/lives` | Check revival rights and current costs | Default |

**Command Aliases:**
- `/revive` → `/rev`, `/resurrect`
- `/lives` → `/liv`

### Gameplay Flow

```
Player Dies
    ↓
Has Revival Rights?
    ↓ Yes                    ↓ No
Becomes Spectator      Permanent Death
    ↓                          ↓
Teammate Uses          World Reset
/revive Command        10 Second Countdown
    ↓                          ↓
Has Required Items?    Server Shuts Down
    ↓ Yes                    
Player Revived!         
-1 Revival Right
```

### Example Scenarios

#### Scenario 1: Early Game Revival
```
Player1 dies → Player2 uses /revive Player1
Cost: 20 Iron Ingots
Player1 revives with 2/3 rights remaining
```

#### Scenario 2: Diamond Achievement
```
Player2 gets "Diamonds!" achievement
→ Revival cost changes: 20 Iron → 5 Diamonds
→ Night counter starts
→ Broadcast message to all players
```

#### Scenario 3: Progressive Difficulty
```
Night 1-2: 5 Diamonds
Night 3-4: 6 Diamonds (broadcast: "2 Nights Passed! Cost increased")
Night 5-6: 7 Diamonds
And so on...
```

#### Scenario 4: Permanent Death
```
Player1 dies (0 rights left)
→ Becomes permanent spectator
→ 10 second countdown begins
→ Worlds deleted (/world, /world_nether, /world_the_end)
→ Server shuts down
→ Admin restarts server for fresh start
```

## ⚙️ Configuration

Plugin automatically creates `plugins/PxlochsRevival/config.yml`.

### Changing Language

```yaml
settings:
  language: tr  # Change to 'en' for English
```

### Adding New Language

1. Open `config.yml`
2. Add a new language under `messages:`:

```yaml
messages:
  tr:
    PLAYER_DIED: "&e{player} &cöldü!"
    # ... other messages
  
  en:
    PLAYER_DIED: "&e{player} &cdied!"
    # ... other messages
  
  de:  # New language (German example)
    PLAYER_DIED: "&e{player} &cist gestorben!"
    PERMANENT_DEATH: "&c&l{player} IST PERMANENT GESTORBEN!"
    # ... translate all messages
```

3. Change `settings.language` to your new language code:
```yaml
settings:
  language: de
```

### Message Customization

Color codes:
- `&0-&9, &a-&f` = Colors
- `&l` = Bold
- `&n` = Underline
- `&o` = Italic

Placeholders:
- `{player}` = Player name
- `{cost}` = Cost amount
- `{lives}` = Lives remaining
- `{seconds}` = Seconds

### Configuration Reset
To reset plugin state:
1. Stop the server
2. Delete `plugins/PxlochsRevival/config.yml`
3. Start the server (recreates with default settings)

## 🔧 Building from Source

### Prerequisites
- Java 17 JDK
- Maven 3.6+

### Build Steps
```bash
# Clone repository
git clone https://github.com/pxloch/pxlochs-revival.git
cd pxlochs-revival

# Build plugin
mvn clean package

# Output location
target/PxlochsRevival-1.0-SNAPSHOT.jar
```

### Development Setup
```bash
# Install dependencies
mvn clean install

# Run tests (if available)
mvn test

# Generate JavaDocs
mvn javadoc:javadoc
```

## 📝 Project Structure

```
pxlochs-revival/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/pxloch/pxlochsrevival/
│       │       └── PxlochsRevival.java
│       └── resources/
│           ├── plugin.yml
│           └── config.yml
├── pom.xml
├── README.md
├── README_EN.md
└── LICENSE
```

## 🐛 Known Issues & Limitations

- **Pterodactyl Compatibility**: On some hosting panels, world deletion might fail due to file permissions. The plugin will shut down the server, and worlds should be manually deleted via the file manager.
- **Night Counter**: Requires at least one player to be online for night tracking to function.
- **Achievement Detection**: Only detects vanilla Minecraft "Diamonds!" achievement.

## 🤝 Contributing

Contributions are welcome! Feel free to submit a Pull Request.

### How to Contribute
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Contribution Ideas
- [ ] Customizable maximum revival rights count
- [ ] Support for more than 2 players
- [ ] Custom advancement tracking
- [ ] Totem of Undying integration
- [ ] Revival sound effects
- [ ] Particle effects
- [ ] Database support for large servers
- [ ] More language support
- [ ] Discord webhook notifications

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👥 Authors

- **Pxloch** - *Initial development* - [GitHub](https://github.com/pxloch)

## 🙏 Acknowledgments

- Inspired by hardcore Minecraft gameplay
- Built for Paper/Spigot 1.20.1

## 📞 Support

- **Issues**: [GitHub Issues](https://github.com/pxloch/pxlochs-revival/issues)
- **Discussions**: [GitHub Discussions](https://github.com/pxloch/pxlochs-revival/discussions)

## 📊 Statistics

![GitHub stars](https://img.shields.io/github/stars/pxloch/pxlochs-revival?style=social)
![GitHub forks](https://img.shields.io/github/forks/pxloch/pxlochs-revival?style=social)
![GitHub issues](https://img.shields.io/github/issues/pxloch/pxlochs-revival)
![GitHub pull requests](https://img.shields.io/github/issues-pr/pxloch/pxlochs-revival)

---

Made with ❤️ for the Minecraft community