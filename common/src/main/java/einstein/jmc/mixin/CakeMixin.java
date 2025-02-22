package einstein.jmc.mixin;

import einstein.jmc.JustMoreCakes;
import einstein.jmc.block.CakeEffectsHolder;
import einstein.jmc.block.cake.BaseCakeBlock;
import einstein.jmc.data.effects.CakeEffects;
import einstein.jmc.init.ModBlocks;
import einstein.jmc.util.MobEffectHolder;
import einstein.jmc.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CakeBlock.class)
public class CakeMixin implements CakeEffectsHolder {

    @Unique
    @Nullable
    private CakeEffects justMoreCakes$cakeEffects;

    @Unique
    private final CakeBlock justMoreCakes$me = (CakeBlock) (Object) this;

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        Item item = stack.getItem();

        if (stack.is(ItemTags.CANDLES) && state.getValue(CakeBlock.BITES) == 0) {
            Block block = Block.byItem(item);
            if (block instanceof CandleBlock) {
                Block.pushEntitiesUp(state, CandleCakeBlock.byCandle(block), level, pos);
            }
        }

        if (justMoreCakes$me.equals(Blocks.CAKE)) {  // Need to check that this is the default cake, so that things won't break with inheritance
            if (stack.is(Items.CAKE) && BaseCakeBlock.isUneaten(state, pos, level)) {
                BlockState newState = ModBlocks.VANILLA_CAKE_FAMILY.getTwoTieredCake().get().defaultBlockState();

                level.setBlockAndUpdate(pos, newState);
                Block.pushEntitiesUp(state, newState, level, pos);
                level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
                level.playSound(null, pos, SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 1, 1);
                player.awardStat(Stats.ITEM_USED.get(Items.CAKE));

                if (!player.isCreative()) {
                    stack.shrink(1);
                }

                cir.setReturnValue(InteractionResult.SUCCESS);
            }
        }
    }

    @Inject(method = "eat", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/food/FoodData;eat(IF)V"))
    private static void eat(LevelAccessor accessor, BlockPos pos, BlockState state, Player player, CallbackInfoReturnable<InteractionResult> cir) {
        CakeBlock cake = (CakeBlock) state.getBlock(); // Don't replace with a reference to Blocks.CAKE, so that this will work with inheritance
        CakeEffects cakeEffects = ((CakeEffectsHolder) cake).justMoreCakes$getCakeEffects();
        if (!player.level().isClientSide && cakeEffects != null) {
            for (MobEffectHolder holder : cakeEffects.mobEffects()) {
                Util.applyEffectFromHolder(holder, player);
            }
        }

        if (player instanceof ServerPlayer serverPlayer) {
            JustMoreCakes.CAKE_EATEN_TRIGGER.trigger(serverPlayer, cake);
        }
    }

    @Nullable
    @Override
    public CakeEffects justMoreCakes$getCakeEffects() {
        if (justMoreCakes$cakeEffects != null) {
            return justMoreCakes$cakeEffects;
        }

        if (justMoreCakes$me.equals(Blocks.CAKE)) {
            return ModBlocks.VANILLA_CAKE_FAMILY.justMoreCakes$getCakeEffects();
        }

        return null;
    }

    @Override
    public void justMoreCakes$setCakeEffects(CakeEffects effects) {
        justMoreCakes$cakeEffects = effects;
    }
}
