#!/usr/bin/env python3
"""
DCSS Vault to Minecraft JSON Exporter

Converts Dungeon Crawl Stone Soup vault files (.des) to Minecraft-compatible JSON format.
"""

import json
import re
import sys
from pathlib import Path

# Mapping of DCSS tile symbols to Minecraft blocks
TILE_MAPPING = {
    'x': {
        'type': 'wall',
        'block': 'minecraft:stone_bricks',
        'description': 'Stone brick walls (wasteland ruins)'
    },
    '.': {
        'type': 'floor',
        'block': 'minecraft:gray_concrete',
        'description': 'Gray concrete floor (wasteland ground)'
    },
    '+': {
        'type': 'door',
        'block': 'minecraft:oak_door',
        'description': 'Wooden door (ruined entrance)'
    },
    '@': {
        'type': 'entry',
        'block': 'minecraft:gray_concrete',
        'description': 'Entry point (floor)'
    },
    ' ': {
        'type': 'void',
        'block': 'minecraft:air',
        'description': 'Air (outside vault boundaries)'
    },
    # Monster spawn points (treat as floor for now)
    '1': {'type': 'spawn', 'block': 'minecraft:gray_concrete', 'description': 'Monster spawn 1'},
    '2': {'type': 'spawn', 'block': 'minecraft:gray_concrete', 'description': 'Monster spawn 2'},
    '3': {'type': 'spawn', 'block': 'minecraft:gray_concrete', 'description': 'Monster spawn 3'},
    '4': {'type': 'spawn', 'block': 'minecraft:gray_concrete', 'description': 'Monster spawn 4'},
    '5': {'type': 'spawn', 'block': 'minecraft:gray_concrete', 'description': 'Monster spawn 5'},
    # Items (treat as floor for now)
    'd': {'type': 'item', 'block': 'minecraft:gray_concrete', 'description': 'Item spawn'},
    'e': {'type': 'item', 'block': 'minecraft:gray_concrete', 'description': 'Item spawn'},
    'f': {'type': 'item', 'block': 'minecraft:gray_concrete', 'description': 'Item spawn'},
}

def parse_des_file(file_path):
    """Parse a .des file and extract all vaults."""
    with open(file_path, 'r') as f:
        content = f.read()

    vaults = []

    # Find all NAME...ENDMAP blocks
    vault_pattern = re.compile(
        r'NAME:\s*(\w+).*?MAP\n(.*?)ENDMAP',
        re.DOTALL
    )

    for match in vault_pattern.finditer(content):
        name = match.group(1)
        map_data = match.group(2)

        # Extract map lines
        map_lines = [line.rstrip() for line in map_data.split('\n') if line.strip()]

        if map_lines:
            vaults.append({
                'name': name,
                'map': map_lines
            })

    return vaults

def calculate_dimensions(map_lines):
    """Calculate width and depth of a vault map."""
    depth = len(map_lines)
    width = max(len(line) for line in map_lines)
    return width, depth

def normalize_map(map_lines):
    """Normalize map lines to have consistent width."""
    width, _ = calculate_dimensions(map_lines)
    normalized = []

    for line in map_lines:
        # Pad with spaces to match width
        padded = line + ' ' * (width - len(line))
        normalized.append(padded)

    return normalized

def find_spawn_point(map_lines, width, depth):
    """Find a suitable spawn point (center of floor space)."""
    # Try to find center floor tile
    center_x = width // 2
    center_z = depth // 2

    # Check if center is floor
    if center_z < len(map_lines) and center_x < len(map_lines[center_z]):
        tile = map_lines[center_z][center_x]
        if tile in ['.', '@', '1', '2', '3', '4', '5', 'd', 'e', 'f']:
            return {'x': center_x, 'y': 1, 'z': center_z}

    # Fallback: find first floor tile
    for z, line in enumerate(map_lines):
        for x, tile in enumerate(line):
            if tile in ['.', '@']:
                return {'x': x, 'y': 1, 'z': z}

    # Ultimate fallback: center
    return {'x': center_x, 'y': 1, 'z': center_z}

def find_features(map_lines):
    """Extract features like doors, spawn points, items."""
    features = []

    for z, line in enumerate(map_lines):
        for x, tile in enumerate(line):
            if tile == '+':
                # Door feature
                features.append({
                    'type': 'door',
                    'x': x,
                    'y': 0,  # Ground level
                    'z': z,
                    'block': 'minecraft:oak_door'
                })
            elif tile in ['1', '2', '3', '4', '5']:
                # Monster spawn (could add armor stand marker)
                features.append({
                    'type': 'monster_spawn',
                    'x': x,
                    'y': 1,
                    'z': z,
                    'block': 'minecraft:red_wool',  # Visual marker
                    'monster_tier': tile
                })
            elif tile in ['d', 'e', 'f']:
                # Item spawn (could add chest)
                features.append({
                    'type': 'item_spawn',
                    'x': x,
                    'y': 1,
                    'z': z,
                    'block': 'minecraft:chest'
                })

    return features

def vault_to_json(vault):
    """Convert a single vault to Minecraft JSON format."""
    map_lines = normalize_map(vault['map'])
    width, depth = calculate_dimensions(map_lines)

    # Build legend from unique tiles in map
    unique_tiles = set()
    for line in map_lines:
        unique_tiles.update(line)

    legend = {}
    for tile in unique_tiles:
        if tile in TILE_MAPPING:
            legend[tile] = TILE_MAPPING[tile]
        else:
            # Unknown tile, treat as floor
            legend[tile] = {
                'type': 'floor',
                'block': 'minecraft:gray_concrete',
                'description': f'Unknown tile: {tile}'
            }

    spawn_point = find_spawn_point(map_lines, width, depth)
    features = find_features(map_lines)

    return {
        'name': vault['name'],
        'version': '1.0',
        'description': f'DCSS wasteland vault: {vault["name"]}',
        'width': width,
        'depth': depth,
        'height': 3,  # Standard 3-block height
        'map': map_lines,
        'legend': legend,
        'features': features,
        'spawn_point': spawn_point
    }

def export_vaults(des_file_path, output_dir):
    """Export all vaults from a .des file to individual JSON files."""
    print(f"═══════════════════════════════════════════════════════")
    print(f"  DCSS Vault Exporter")
    print(f"  Input: {des_file_path}")
    print(f"  Output: {output_dir}")
    print(f"═══════════════════════════════════════════════════════")

    vaults = parse_des_file(des_file_path)
    print(f"  Found {len(vaults)} vaults in {Path(des_file_path).name}")

    output_path = Path(output_dir)
    output_path.mkdir(parents=True, exist_ok=True)

    for vault in vaults:
        vault_json = vault_to_json(vault)

        # Write to JSON file
        output_file = output_path / f"{vault['name']}.json"
        with open(output_file, 'w') as f:
            json.dump(vault_json, f, indent=2)

        print(f"  ✓ Exported: {vault['name']}.json ({vault_json['width']}x{vault_json['depth']})")

    print(f"═══════════════════════════════════════════════════════")
    print(f"  Export complete! {len(vaults)} vaults exported.")
    print(f"═══════════════════════════════════════════════════════")

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python3 export_vault_to_json.py <input.des> <output_dir>")
        print("Example: python3 export_vault_to_json.py crawl-ref/source/dat/des/wasteland/ruins_early.des wasteland-mod/src/main/resources/rooms/")
        sys.exit(1)

    des_file = sys.argv[1]
    output_dir = sys.argv[2]

    export_vaults(des_file, output_dir)
