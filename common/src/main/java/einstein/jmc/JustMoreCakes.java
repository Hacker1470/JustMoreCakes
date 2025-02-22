package einstein.jmc;

import einstein.jmc.advancement.criterian.CakeEatenTrigger;
import einstein.jmc.block.cake.BaseCakeBlock;
import einstein.jmc.data.effects.CakeEffectsManager;
import einstein.jmc.init.*;
import einstein.jmc.platform.Services;
import einstein.jmc.util.CakeVariant;
import einstein.jmc.util.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

import static einstein.jmc.init.ModCommonConfigs.CAKE_BAKERY_GENERATION_WEIGHT;
import static einstein.jmc.util.Util.registerVillageBuilding;

public class JustMoreCakes {

    public static final String MOD_ID = "jmc";
    public static final String MOD_NAME = "Just More Cakes!";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static final CakeEatenTrigger CAKE_EATEN_TRIGGER = CriteriaTriggers.register(MOD_ID + ":cake_eaten", new CakeEatenTrigger());

    public static void init() {
        ModItems.init();
        ModBlocks.init();
        ModBlockEntityTypes.init();
        ModMenuTypes.init();
        ModRecipes.init();
        ModVillagers.init();
        ModPotions.init();
        ModCreativeModeTabs.init();
        ModGameEvents.init();
        ModPackets.init();
    }

    public static void commonSetup() {
        Items.CAKE.maxStackSize = 64;
        ModPotions.registerPotionRecipes();

        for (Supplier<BaseCakeBlock> cake : CakeVariant.VARIANT_BY_CAKE.keySet()) {
            if (cake != ModBlocks.CUPCAKE_VARIANT) {
                Services.HOOKS.registerCompostable(cake.get(), 1);
            }
        }

        Services.HOOKS.registerCompostable(ModBlocks.CUPCAKE_VARIANT.getCake().get(), 0.25F);
        Services.HOOKS.registerCompostable(ModItems.CREAM_CHEESE.get(), 0.65F);
    }

    public static void onServerStarting(MinecraftServer server) {
        registerVillageBuilding(server, "plains", "bakery_1", CAKE_BAKERY_GENERATION_WEIGHT.get());
        registerVillageBuilding(server, "plains", "bakery_2", CAKE_BAKERY_GENERATION_WEIGHT.get());
        registerVillageBuilding(server, "desert", "bakery_1", CAKE_BAKERY_GENERATION_WEIGHT.get());
        registerVillageBuilding(server, "savanna", "bakery_1", CAKE_BAKERY_GENERATION_WEIGHT.get());
        registerVillageBuilding(server, "snowy", "bakery_1", CAKE_BAKERY_GENERATION_WEIGHT.get());
        registerVillageBuilding(server, "snowy", "bakery_2", CAKE_BAKERY_GENERATION_WEIGHT.get());
        registerVillageBuilding(server, "taiga", "bakery_1", CAKE_BAKERY_GENERATION_WEIGHT.get());

        onDatapackSync(null, server, false);
    }

    public static void onDatapackSync(@Nullable ServerPlayer player, MinecraftServer server, boolean playerUpdate) {
        if (playerUpdate) {
            CakeEffectsManager.syncToPlayer(player);
        }
        else {
            CakeEffectsManager.loadCakeEffects();
        }

        if (ModCommonConfigs.DISABLE_DEFAULT_CAKE_RECIPE.get()) {
            Util.removeRecipe(server.getRecipeManager(), RecipeType.CRAFTING, mcLoc("cake"));
        }
    }

    public static ResourceLocation loc(String string) {
        return new ResourceLocation(MOD_ID, string);
    }

    public static ResourceLocation mcLoc(String string) {
        return new ResourceLocation("minecraft", string);
    }
}