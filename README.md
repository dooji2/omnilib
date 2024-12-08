<div align="center">
    <img src="https://cdn.modrinth.com/data/cached_images/42f674ee76d1a9ead331f83c808276cbe0ce2371_0.webp" alt="Omnilib Logo" width="256" />
    <h1>Omnilib</h1>
    <p>
        A framework for exploring new possibilities in Minecraft modding<br>
        Built for Fabric 1.20.1, 1.20.4, 1.21 and 1.21.1
    </p>
    <p> 
        <strong> 
            For players: <a href="#requirements-and-compatibility">Check Requirements and Compatibility</a> 
        </strong> 
    </p>
    <p>
        <a href="https://github.com/dooji2/Omnilib">
            <img src="https://img.shields.io/github/languages/top/dooji2/omnilib?color=4B8BBE&style=for-the-badge" alt="Top Language" />
        </a>
        <a href="https://discord.gg/UPmnyM9YcY">
            <img src="https://img.shields.io/discord/1153370502539255808?color=5865F2&label=Discord&logo=discord&style=for-the-badge" alt="Join Discord" />
        </a>
        <a href="https://github.com/dooji2/Omnilib/actions">
            <img src="https://img.shields.io/github/actions/workflow/status/dooji2/omnilib/build.yml?style=for-the-badge" alt="GitHub Actions Status" />
        </a>
        <a href="https://github.com/dooji2/Omnilib/issues">
            <img src="https://img.shields.io/github/issues/dooji2/omnilib?color=yellow&style=for-the-badge" alt="GitHub Issues" />
        </a>
        <a href="https://jitpack.io/#dooji2/omnilib/">
            <img src="https://img.shields.io/jitpack/v/github/dooji2/omnilib?style=for-the-badge" alt="JitPack Version" />
        </a>
    </p>
</div>

## Features

- **OmniToast**: Customizable toast notifications with optional textures and item icons.
- **OmniTooltip**: Tooltips with custom icons, backgrounds, and multi-line text support.
- **OmniText**: Text rendering with support for embedded icons and Markdown formatting.
- **OmniPopup**: Dynamic, scrollable suggestion popups.

## Usage

### OmniToast
Create and display a fully customizable toast notification:
```java
OmnilibClient.showToast(
    Text.literal("A toast!"),
    Text.literal("A very amazing description."),
    5000, // Duration in milliseconds
    0xFFFFFF, // Title color (white)
    0xAAAAAA, // Description color (gray)
    Identifier.of("omnilib", "textures/gui/toast_background.png"), // Background texture
    null, // Icon texture (optional)
    new ItemStack(Items.DIAMOND), // Icon item (optional)
    16, // Icon size
    160, // Toast width
    32  // Toast height
);
```

### OmniTooltip
Create and render a tooltip:
```java
OmnilibClient.showTooltip(
    context,
    this.textRenderer,
    "Omnilib Tooltip",
    List.of(new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.EMERALD)), // Item stacks to display
    List.of(
        Text.literal("This is a customizable tooltip."),
        Text.literal("It supports multiple lines and icons.")
    ),
    0x800000FF, // Background color (ARGB)
    Identifier.of("omnilib", "textures/gui/tooltip_background.png"), // Custom background texture (optional)
    0xFFFFFF, // Text color (white)
    null, // Custom icon texture (optional)
    mouseX + 10, // X position
    mouseY + 10  // Y position
);
```

### OmniText
Create styled text using Markdown:
```java
OmniText styledText = OmnilibClient.createOmniText("**Bold** and _Italic_ text with __Markdown__.");

OmniText styledTextWithIcon = OmnilibClient.createOmniText(
    "- Proceed with trade",
    Identifier.of("minecraft", "textures/item/emerald.png"), // Icon texture
    16, // Icon width in pixels
    16  // Icon height in pixels
);
```

### OmniPopup
OmniPopup is a dynamic UI component for displaying a scrollable list of selectable options.

> [!NOTE]
> OmniPopup is a more extensive feature and will be fully documented in a future update, following the first release of **Omnilib**. Stay tuned for detailed usage guides and examples!

## Developer Documentation
The core class `OmnilibClient` includes **Javadoc-style documentation** for all major methods and features, such as:

- `showToast()` for creating custom toast notifications.
- `showTooltip()` for rendering tooltips with custom text and icons.
- `createOmniText()` for creating styled text with Markdown support and optional icons.

For detailed method descriptions and parameter explanations, refer to the **source code** in the [`OmnilibClient`](https://github.com/dooji2/Omnilib/blob/main/src/client/java/com/dooji/omnilib/OmnilibClient.java) class.

## Getting Started

### Installation
1. Download the mod JAR from the [Releases](https://github.com/dooji2/Omnilib/releases) page.
2. Place the JAR file in your Minecraft `mods` folder.
3. Launch Minecraft with Fabric 1.20.1, 1.20.4, 1.21 or 1.21.1 installed (**Fabric API is required!**).

### Requirements and Compatibility
- **Omnilib Dependency**: If a mod requires Omnilib, make sure to download the correct version and place it in your `mods` folder.
- **Compatibility**: If you are on Fabric, **Fabric API** is required. If you are on (Neo)Forge, **Sinytra Connector** and **Forgified Fabric API** are required.

### Build From Source
1. Clone the repository: `git clone https://github.com/dooji2/Omnilib.git`
2. Navigate to the project directory: `cd Omnilib`
3. Build the project: `./gradlew build`
4. Add the generated JAR from `build/libs` to your `mods` folder.

## Support

For help or feedback, join the community on Discord

<a href="https://discord.gg/UPmnyM9YcY">
    <img src="https://img.shields.io/discord/1153370502539255808?color=5865F2&label=Discord&logo=discord&style=for-the-badge" alt="Join Discord" />
</a>

## Credits

- [Dooji](https://github.com/dooji2) - The (only) developer
- Fabric – The modding platform that makes this possible

<p align="center">
    Let’s explore the future of Minecraft modding with <strong>Omnilib</strong>!<br>
    <em>> there's actually nothing futuristic or revolutionary about this but it just fits into the theme <</em>
</p>
