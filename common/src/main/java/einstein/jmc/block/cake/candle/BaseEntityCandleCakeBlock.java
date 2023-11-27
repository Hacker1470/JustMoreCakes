package einstein.jmc.block.cake.candle;

import einstein.jmc.block.cake.BaseCakeBlock;
import einstein.jmc.util.BlockEntitySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class BaseEntityCandleCakeBlock<T extends BlockEntity> extends BaseCandleCakeBlock implements EntityBlock {

    protected final BlockEntitySupplier<T> blockEntity;

    public BaseEntityCandleCakeBlock(BaseCakeBlock originalCake, Block candle, Properties properties, BlockEntitySupplier<T> blockEntity) {
        super(originalCake, candle, properties);
        this.blockEntity = blockEntity;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return blockEntity.create(pos, state);
    }
}
