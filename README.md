# PixelAddons

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![CodeQL Badge](https://github.com/Foulest/PixelAddons/actions/workflows/codeql.yml/badge.svg)](https://github.com/Foulest/PixelAddons/actions/workflows/codeql.yml)
[![Downloads](https://img.shields.io/github/downloads/Foulest/PixelAddons/total.svg)](https://github.com/Foulest/PixelAddons/releases)
[![JitPack Badge](https://jitpack.io/v/Foulest/PixelAddons.svg)](https://jitpack.io/#Foulest/PixelAddons)

**PixelAddons** is a server-side Spigot plugin that improves the Pixelmon Reforged 1.12.2 experience.

All commands, features, and messages are extensively configurable.

## Features

- **Pokemon Stat Panels:** After every catch, a broadcast message containing a **hoverable stat panel** for the newly
  acquired Pokemon is displayed in chat, showing the Pokemon's gender, level, ability, nature, Hidden Power type, EVs,
  and IVs. This panel can be shown off using `/show <slot>`, and you can view it again using `/stats <slot>`. You can
  even view other players Pokemon using `/stats <slot> <player>` if you want to. Here's an example of a stat panel:

> ![Stat Panel Image](https://i.imgur.com/DcmbssN.png)

- **Event Broadcasts**: The stat panels above are embedded into broadcast messages which get sent after every catch,
  starter pick, egg hatch, and fossil revival. Additionally, when Pokemon use the **Pickup** ability, a message is sent
  to the player telling them what item their Pokemon found. When your Pokemon gain EVs after fights, an EV gain message
  is displayed as well. The color of the Pokemon's name in the broadcast changes whether the Pokemon is normal, shiny,
  and legendary as well. Here's an example of the post-catch broadcast messages:

> ![Broadcast Image](https://i.imgur.com/rXqzPWK.png)

- **End Battle Command:** PixelAddons overwrites the Pixelmon Reforged `/endbattle` command. This command was previously
  locked behind a hard to find permission and didn't always work. Now, there is no permission needed to end any glitched
  battle without having to close your game.

- **Hatch Command:** PixelAddons includes a `/hatch <slot>` command that allows you to instantly hatch eggs for a
  configurable price. This drastically improves the egg hatching experience if players can use their in-game money they
  earned to expedite the process. The command prompts the users to confirm the egg hatch by having them run the command
  twice to prevent accidental hatches.

- **Hunt Reroll Command:** PixelAddons integrates with
  **[PixelHunt Remastered](https://pixelmonmod.com/wiki/PixelHunt_Remastered)** and adds a command that allows
  players to re-roll the active hunt using `/reroll` (executes `/hunt reload` on the backend.) The re-roll needs all
  online members to vote for the re-roll by running the command. Once all online players agree to re-roll the hunt, the
  hunt is re-rolled. If only one player is online, a vote is not needed and the hunt is re-rolled. A configurable
  cooldown between re-rolls is available to limit the abuse of the command.

- **Commands on First Join:** When players first join the server, PixelAddons can execute a list of commands on their
  behalf. This is primarily used to give players items on first join, such as Poke Balls, tools, food, etc. This feature
  is present in Essentials, but for servers not using Essentials, it exists in PixelAddons as well.

- **Custom Per-Player Rates:** PixelAddons supports setting custom hidden ability, boss, shiny, and pokerus rates
  per-player in any world. These are all disabled by default and only work for players with a specific permission.

## Dependencies

- **[CatServer (1.12.2)](https://catmc.org/)**
- **[Pixelmon Reforged](https://reforged.gg)**
- **[PixelHunt Remastered](https://pixelmonmod.com/wiki/PixelHunt_Remastered)** *(optional)*
- **[AtomDev](https://github.com/josephworks/AtomMC)** *(only in project)*

## Compiling

1. Clone the repository.
2. Open a command prompt/terminal to the repository directory.
3. Run `gradlew shadowJar` on Windows, or `./gradlew shadowJar` on macOS or Linux.
4. The built `PixelAddons-X.X.X.jar` file will be in the `build/libs` folder.

## Download and Run

1. Download the latest version from the [releases page](https://github.com/Foulest/PixelAddons/releases) or compile it
   yourself.
2. Make sure your server is running CatServer on version 1.12.2 and Pixelmon Reforged.
3. Place the `PixelAddons-X.X.X.jar` file in your server's `plugins` folder.
4. Start or restart your server.

## Getting Help

For support or queries, please open an issue in the [Issues section](https://github.com/Foulest/PixelAddons/issues).
