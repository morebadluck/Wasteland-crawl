# Custom Font Support

The Wasteland mod supports custom fonts for a better roguelike aesthetic.

## About Fonts in Minecraft

Minecraft uses a flexible font system where you can define custom fonts via JSON configuration files. Fonts can use:
- **TrueType fonts** (.ttf files)
- **Bitmap fonts** (PNG sprite sheets)
- **Unicode providers** for better international support

## Recommended Fonts for Roguelike Feel

For a DCSS-inspired experience, we recommend monospace fonts:

### Option 1: Inconsolata (Recommended)
- Clean, readable monospace font
- Free and open source
- Download: https://fonts.google.com/specimen/Inconsolata

### Option 2: Source Code Pro
- Adobe's monospace programming font
- Excellent readability
- Download: https://fonts.google.com/specimen/Source+Code+Pro

### Option 3: JetBrains Mono
- Modern monospace with ligatures
- Great for terminals and UIs
- Download: https://www.jetbrains.com/lp/mono/

### Option 4: IBM Plex Mono
- IBM's monospace font
- Retro computing aesthetic
- Download: https://fonts.google.com/specimen/IBM+Plex+Mono

## How to Install a Custom Font

1. **Download your chosen font** (get the .ttf file)

2. **Place the font file** in:
   ```
   src/main/resources/assets/wasteland/font/
   ```
   Example: `wasteland_mono.ttf`

3. **Create a font definition** (already configured):

   The mod includes a pre-configured `wasteland_mono.json` that will use any font you place in the font directory.

4. **Use the font in your screens** by referencing `wasteland:wasteland_mono`

## Current Font Configuration

The mod is set up to use a custom monospace font for:
- Equipment screen
- Character sheet
- Skill screens
- Combat UI
- Item tooltips (optional)

## Font Not Showing?

If the custom font doesn't appear:
1. Make sure the .ttf file is in the correct directory
2. Check that the filename matches what's in `wasteland_mono.json`
3. Restart Minecraft completely (not just reload resources)
4. Check logs for font loading errors

## Using Default Minecraft Font

If you prefer the default Minecraft font, simply don't add a .ttf file. The mod will fall back to Minecraft's built-in font automatically.

## Advanced: Creating Your Own Font Definition

You can create custom font definitions in:
```
src/main/resources/assets/wasteland/font/
```

Format:
```json
{
  "providers": [
    {
      "type": "ttf",
      "file": "wasteland:font/your_font.ttf",
      "shift": [0.0, 0.0],
      "size": 11.0,
      "oversample": 2.0
    }
  ]
}
```

Adjust `size` for larger/smaller text (default: 11.0)
Adjust `oversample` for smoother rendering (default: 2.0)

## License Notes

- Most Google Fonts are open source (OFL license)
- JetBrains Mono is Apache 2.0
- IBM Plex is SIL Open Font License
- Always check font licenses before redistribution
