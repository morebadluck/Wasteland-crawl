package com.wasteland.entity;

import com.wasteland.WastelandMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registry for all Wasteland mod entities (robots, monsters, etc.)
 */
public class ModEntities {
    private static final Logger LOGGER = LogManager.getLogger();

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, WastelandMod.MOD_ID);

    // ========================================
    // ROBOT ENTITIES
    // ========================================

    public static final RegistryObject<EntityType<EyebotEntity>> EYEBOT = ENTITY_TYPES.register("eyebot",
        () -> EntityType.Builder.of(EyebotEntity::new, MobCategory.MONSTER)
            .sized(0.6F, 0.8F) // Small floating drone
            .clientTrackingRange(8)
            .build("eyebot"));

    public static final RegistryObject<EntityType<ProtectronEntity>> PROTECTRON = ENTITY_TYPES.register("protectron",
        () -> EntityType.Builder.of(ProtectronEntity::new, MobCategory.MONSTER)
            .sized(0.8F, 1.8F) // Humanoid robot
            .clientTrackingRange(8)
            .build("protectron"));

    public static final RegistryObject<EntityType<MrHandyEntity>> MR_HANDY = ENTITY_TYPES.register("mr_handy",
        () -> EntityType.Builder.of(MrHandyEntity::new, MobCategory.MONSTER)
            .sized(0.9F, 1.2F) // Floating utility robot
            .clientTrackingRange(8)
            .build("mr_handy"));

    public static final RegistryObject<EntityType<SentryBotEntity>> SENTRY_BOT = ENTITY_TYPES.register("sentry_bot",
        () -> EntityType.Builder.of(SentryBotEntity::new, MobCategory.MONSTER)
            .sized(1.2F, 2.0F) // Large heavy robot
            .clientTrackingRange(10)
            .fireImmune() // Sentry bots are immune to fire
            .build("sentry_bot"));

    // ========================================
    // MID-GAME ROBOTS (Tier 3-5)
    // ========================================

    public static final RegistryObject<EntityType<SecurityBotEntity>> SECURITY_BOT = ENTITY_TYPES.register("security_bot",
        () -> EntityType.Builder.of(SecurityBotEntity::new, MobCategory.MONSTER)
            .sized(0.8F, 1.8F) // Humanoid robot (like Protectron)
            .clientTrackingRange(8)
            .build("security_bot"));

    public static final RegistryObject<EntityType<AssaultronEntity>> ASSAULTRON = ENTITY_TYPES.register("assaultron",
        () -> EntityType.Builder.of(AssaultronEntity::new, MobCategory.MONSTER)
            .sized(0.7F, 1.9F) // Sleek humanoid robot
            .clientTrackingRange(10)
            .build("assaultron"));

    public static final RegistryObject<EntityType<MrGustyEntity>> MR_GUSTY = ENTITY_TYPES.register("mr_gusty",
        () -> EntityType.Builder.of(MrGustyEntity::new, MobCategory.MONSTER)
            .sized(0.9F, 1.2F) // Floating robot (like Mr. Handy)
            .clientTrackingRange(8)
            .fireImmune() // Military flamer unit
            .build("mr_gusty"));

    public static final RegistryObject<EntityType<RobobrainEntity>> ROBOBRAIN = ENTITY_TYPES.register("robobrain",
        () -> EntityType.Builder.of(RobobrainEntity::new, MobCategory.MONSTER)
            .sized(1.0F, 1.6F) // Brain-tank hybrid
            .clientTrackingRange(10)
            .build("robobrain"));

    // ========================================
    // LATE-GAME ROBOTS (Tier 6+)
    // ========================================

    public static final RegistryObject<EntityType<AnnihilatorSentryBotEntity>> ANNIHILATOR_SENTRY_BOT = ENTITY_TYPES.register("annihilator_sentry_bot",
        () -> EntityType.Builder.of(AnnihilatorSentryBotEntity::new, MobCategory.MONSTER)
            .sized(1.4F, 2.2F) // Larger than regular Sentry Bot
            .clientTrackingRange(12)
            .fireImmune()
            .build("annihilator_sentry_bot"));

    public static final RegistryObject<EntityType<QuantumAssaultronEntity>> QUANTUM_ASSAULTRON = ENTITY_TYPES.register("quantum_assaultron",
        () -> EntityType.Builder.of(QuantumAssaultronEntity::new, MobCategory.MONSTER)
            .sized(0.7F, 1.9F) // Same size as Assaultron
            .clientTrackingRange(12)
            .build("quantum_assaultron"));

    public static final RegistryObject<EntityType<ExperimentalBotEntity>> EXPERIMENTAL_BOT = ENTITY_TYPES.register("experimental_bot",
        () -> EntityType.Builder.of(ExperimentalBotEntity::new, MobCategory.MONSTER)
            .sized(1.0F, 1.8F) // Medium size
            .clientTrackingRange(10)
            .build("experimental_bot"));

    // ========================================
    // BOSS ROBOTS
    // ========================================

    public static final RegistryObject<EntityType<OverlordBotEntity>> OVERLORD_BOT = ENTITY_TYPES.register("overlord_bot",
        () -> EntityType.Builder.of(OverlordBotEntity::new, MobCategory.MONSTER)
            .sized(1.6F, 2.5F) // Huge boss robot
            .clientTrackingRange(16)
            .fireImmune()
            .build("overlord_bot"));

    /**
     * Register this to the mod event bus
     */
    public static void register(IEventBus modEventBus) {
        ENTITY_TYPES.register(modEventBus);
        LOGGER.info("Registered {} Wasteland entities", ENTITY_TYPES.getEntries().size());
    }

    /**
     * Event handler for registering entity attributes
     */
    @Mod.EventBusSubscriber(modid = WastelandMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class EntityAttributeHandler {
        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            // Register basic robot attributes
            event.put(EYEBOT.get(), EyebotEntity.createAttributes().build());
            event.put(PROTECTRON.get(), ProtectronEntity.createAttributes().build());
            event.put(MR_HANDY.get(), MrHandyEntity.createAttributes().build());
            event.put(SENTRY_BOT.get(), SentryBotEntity.createAttributes().build());

            // Register mid-game robot attributes
            event.put(SECURITY_BOT.get(), SecurityBotEntity.createAttributes().build());
            event.put(ASSAULTRON.get(), AssaultronEntity.createAttributes().build());
            event.put(MR_GUSTY.get(), MrGustyEntity.createAttributes().build());
            event.put(ROBOBRAIN.get(), RobobrainEntity.createAttributes().build());

            // Register late-game robot attributes
            event.put(ANNIHILATOR_SENTRY_BOT.get(), AnnihilatorSentryBotEntity.createAttributes().build());
            event.put(QUANTUM_ASSAULTRON.get(), QuantumAssaultronEntity.createAttributes().build());
            event.put(EXPERIMENTAL_BOT.get(), ExperimentalBotEntity.createAttributes().build());

            // Register boss robot attributes
            event.put(OVERLORD_BOT.get(), OverlordBotEntity.createAttributes().build());

            LOGGER.info("Registered entity attributes for all robots");
        }
    }
}
