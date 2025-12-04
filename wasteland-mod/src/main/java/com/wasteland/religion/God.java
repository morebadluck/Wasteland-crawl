package com.wasteland.religion;

import java.util.ArrayList;
import java.util.List;

/**
 * Gods from DCSS that players can worship
 * Each god has unique abilities, likes, and dislikes
 */
public enum God {
    // No god
    NONE(
        "None",
        "You worship no god.",
        GodAlignment.NEUTRAL,
        new String[0],
        new String[0]
    ),

    // Good gods
    THE_SHINING_ONE(
        "The Shining One",
        "The Shining One is a god of holy light and righteousness. Followers must fight evil and never use poison or unholy magic.",
        GodAlignment.GOOD,
        new String[] {
            "Killing evil beings",
            "Killing undead",
            "Killing demons"
        },
        new String[] {
            "Using poison",
            "Using necromancy",
            "Attacking allies",
            "Desecrating holy corpses"
        }
    ),

    ZIN(
        "Zin",
        "Zin is the god of law and order. Followers must maintain purity and avoid chaos.",
        GodAlignment.GOOD,
        new String[] {
            "Donating gold",
            "Killing chaotic creatures",
            "Killing unclean creatures"
        },
        new String[] {
            "Using mutations",
            "Cannibalism",
            "Using unclean weapons",
            "Attacking allies"
        }
    ),

    ELYVILON(
        "Elyvilon",
        "Elyvilon the Healer is a god of healing and pacifism. Followers should avoid killing when possible.",
        GodAlignment.GOOD,
        new String[] {
            "Healing yourself or allies",
            "Exploring peacefully"
        },
        new String[] {
            "Unnecessary killing",
            "Attacking allies"
        }
    ),

    // Neutral gods
    TROG(
        "Trog",
        "Trog is the god of anger and violence. Trog hates all magic and grants great power to berserkers.",
        GodAlignment.NEUTRAL,
        new String[] {
            "Killing in melee combat",
            "Berserk killing"
        },
        new String[] {
            "Casting spells",
            "Using magic items"
        }
    ),

    OKAWARU(
        "Okawaru",
        "Okawaru is a straightforward god of battle. Followers gain rewards for heroic kills.",
        GodAlignment.NEUTRAL,
        new String[] {
            "Killing strong enemies",
            "Winning battles"
        },
        new String[] {
            "Inactivity",
            "Fleeing from combat"
        }
    ),

    SIF_MUNA(
        "Sif Muna",
        "Sif Muna is the god of magic and knowledge. Followers gain increased spell power and easier memorization.",
        GodAlignment.NEUTRAL,
        new String[] {
            "Casting spells",
            "Learning new spells",
            "Exploring"
        },
        new String[] {
            "Forgetting spells voluntarily"
        }
    ),

    VEHUMET(
        "Vehumet",
        "Vehumet is the god of destructive magic. Followers gain powerful offensive spells.",
        GodAlignment.NEUTRAL,
        new String[] {
            "Killing with spells",
            "Using destructive magic"
        },
        new String[] {
            "Inactivity"
        }
    ),

    // Evil gods
    KIKUBAAQUDGHA(
        "Kikubaaqudgha",
        "Kikubaaqudgha is the god of necromancy. Followers gain undead servants and necromantic power.",
        GodAlignment.EVIL,
        new String[] {
            "Killing living creatures",
            "Using necromancy",
            "Raising undead"
        },
        new String[] {
            "Using holy magic",
            "Destroying undead"
        }
    ),

    MAKHLEB(
        "Makhleb",
        "Makhleb the Destroyer is the god of chaos and bloodshed. Followers gain demonic servants.",
        GodAlignment.EVIL,
        new String[] {
            "Killing anything",
            "Causing destruction"
        },
        new String[] {
            "Inactivity"
        }
    ),

    YREDELEMNUL(
        "Yredelemnul",
        "Yredelemnul is the god of death. Followers can enslave souls and raise powerful undead.",
        GodAlignment.EVIL,
        new String[] {
            "Killing living creatures",
            "Raising undead",
            "Enslaving souls"
        },
        new String[] {
            "Using holy magic",
            "Destroying undead"
        }
    ),

    // Chaotic gods
    XOM(
        "Xom",
        "Xom is the god of chaos. Xom acts randomly and doesn't use piety. Worship at your own risk!",
        GodAlignment.CHAOTIC,
        new String[] {
            "Doing interesting things",
            "Taking risks",
            "Causing chaos"
        },
        new String[] {
            "Being boring"
        }
    ),

    // Special gods
    GOZAG(
        "Gozag",
        "Gozag Ym Sagoz is the god of gold. Followers trade gold for divine powers.",
        GodAlignment.NEUTRAL,
        new String[] {
            "Collecting gold"
        },
        new String[] {
            "Nothing - gold is all that matters"
        }
    );

    private final String displayName;
    private final String description;
    private final GodAlignment alignment;
    private final String[] likes;
    private final String[] dislikes;

    God(String displayName, String description, GodAlignment alignment, String[] likes, String[] dislikes) {
        this.displayName = displayName;
        this.description = description;
        this.alignment = alignment;
        this.likes = likes;
        this.dislikes = dislikes;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public GodAlignment getAlignment() {
        return alignment;
    }

    public String[] getLikes() {
        return likes;
    }

    public String[] getDislikes() {
        return dislikes;
    }

    /**
     * Get all gods except NONE
     */
    public static God[] getWorshipableGods() {
        List<God> gods = new ArrayList<>();
        for (God god : values()) {
            if (god != NONE) {
                gods.add(god);
            }
        }
        return gods.toArray(new God[0]);
    }

    /**
     * Get gods by alignment
     */
    public static God[] getGodsByAlignment(GodAlignment alignment) {
        List<God> gods = new ArrayList<>();
        for (God god : values()) {
            if (god.alignment == alignment && god != NONE) {
                gods.add(god);
            }
        }
        return gods.toArray(new God[0]);
    }

    /**
     * Check if this god uses piety
     */
    public boolean usesPiety() {
        return this != XOM; // Xom doesn't use piety
    }
}
