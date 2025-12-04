#!/bin/bash

# Eyebot - LIGHT
sed -i '' 's/super(entityType, level, RobotArmorClass.LIGHT, false);/super(entityType, level, RobotArmorClass.LIGHT, false, com.wasteland.loot.RobotLoot.RobotTier.LIGHT);/g' EyebotEntity.java

# Mr. Handy - LIGHT
sed -i '' 's/super(entityType, level, RobotArmorClass.LIGHT, false);/super(entityType, level, RobotArmorClass.LIGHT, false, com.wasteland.loot.RobotLoot.RobotTier.LIGHT);/g' MrHandyEntity.java

# Protectron - MEDIUM
sed -i '' 's/super(entityType, level, RobotArmorClass.MEDIUM, false);/super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.MEDIUM);/g' ProtectronEntity.java

# Security Bot - MEDIUM
sed -i '' 's/super(entityType, level, RobotArmorClass.MEDIUM, false);/super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.MEDIUM);/g' SecurityBotEntity.java

# Assaultron - COMBAT
sed -i '' 's/super(entityType, level, RobotArmorClass.MEDIUM, false);/super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.COMBAT);/g' AssaultronEntity.java

# Mr. Gusty - COMBAT
sed -i '' 's/super(entityType, level, RobotArmorClass.MEDIUM, false);/super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.COMBAT);/g' MrGustyEntity.java

# Robobrain - COMBAT
sed -i '' 's/super(entityType, level, RobotArmorClass.MEDIUM, false);/super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.COMBAT);/g' RobobrainEntity.java

# Sentry Bot - HEAVY (can self-destruct)
sed -i '' 's/super(entityType, level, RobotArmorClass.HEAVY, true);/super(entityType, level, RobotArmorClass.HEAVY, true, com.wasteland.loot.RobotLoot.RobotTier.HEAVY);/g' SentryBotEntity.java

# Experimental Bot - HEAVY
sed -i '' 's/super(entityType, level, RobotArmorClass.MEDIUM, false);/super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.HEAVY);/g' ExperimentalBotEntity.java

# Annihilator Sentry Bot - ELITE (can self-destruct)
sed -i '' 's/super(entityType, level, RobotArmorClass.HEAVY, true);/super(entityType, level, RobotArmorClass.HEAVY, true, com.wasteland.loot.RobotLoot.RobotTier.ELITE);/g' AnnihilatorSentryBotEntity.java

# Quantum Assaultron - ELITE
sed -i '' 's/super(entityType, level, RobotArmorClass.MEDIUM, false);/super(entityType, level, RobotArmorClass.MEDIUM, false, com.wasteland.loot.RobotLoot.RobotTier.ELITE);/g' QuantumAssaultronEntity.java

# Overlord Bot - BOSS
sed -i '' 's/super(entityType, level, RobotArmorClass.HEAVY, false);/super(entityType, level, RobotArmorClass.HEAVY, false, com.wasteland.loot.RobotLoot.RobotTier.BOSS);/g' OverlordBotEntity.java

echo "All robot entities updated with loot tiers"
