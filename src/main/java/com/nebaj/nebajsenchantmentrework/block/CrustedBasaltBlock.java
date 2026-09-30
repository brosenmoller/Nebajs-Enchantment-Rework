package com.nebaj.nebajsenchantmentrework.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

// Lava counterpart of vanilla's FrostedIceBlock, minus its light-level check (lava is always bright enough to pass it)
public class CrustedBasaltBlock extends Block
{
    public static final MapCodec<CrustedBasaltBlock> CODEC = simpleCodec(CrustedBasaltBlock::new);
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_3;
    private static final int NEIGHBORS_TO_AGE = 4;
    private static final int NEIGHBORS_TO_MELT = 2;

    public CrustedBasaltBlock(Properties properties)
    {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public MapCodec<CrustedBasaltBlock> codec()
    {
        return CODEC;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston)
    {
        level.scheduleTick(pos, this, Mth.nextInt(level.getRandom(), 60, 120));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        boolean shouldAge = random.nextInt(3) == 0 || fewerNeighboursThan(level, pos, NEIGHBORS_TO_AGE);
        if (shouldAge && slightlyMelt(state, level, pos))
        {
            BlockPos.MutableBlockPos neighbourPos = new BlockPos.MutableBlockPos();
            for (Direction direction : Direction.values())
            {
                neighbourPos.setWithOffset(pos, direction);
                BlockState neighbourState = level.getBlockState(neighbourPos);
                if (neighbourState.is(this) && !slightlyMelt(neighbourState, level, neighbourPos))
                {
                    level.scheduleTick(neighbourPos, this, Mth.nextInt(random, 20, 40));
                }
            }
            return;
        }

        level.scheduleTick(pos, this, Mth.nextInt(random, 20, 40));
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block neighbourBlock, @Nullable Orientation orientation, boolean movedByPiston)
    {
        if (neighbourBlock.defaultBlockState().is(this) && fewerNeighboursThan(level, pos, NEIGHBORS_TO_MELT))
        {
            melt(level, pos);
        }

        super.neighborChanged(state, level, pos, neighbourBlock, orientation, movedByPiston);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AGE);
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state, boolean includeData)
    {
        return ItemStack.EMPTY;
    }

    private boolean slightlyMelt(BlockState state, Level level, BlockPos pos)
    {
        int age = state.getValue(AGE);
        if (age < MAX_AGE)
        {
            level.setBlock(pos, state.setValue(AGE, age + 1), Block.UPDATE_CLIENTS);
            return false;
        }

        melt(level, pos);
        return true;
    }

    private static void melt(Level level, BlockPos pos)
    {
        level.setBlockAndUpdate(pos, Blocks.LAVA.defaultBlockState());
        level.neighborChanged(pos, Blocks.LAVA, null);
    }

    private boolean fewerNeighboursThan(BlockGetter level, BlockPos pos, int limit)
    {
        int count = 0;
        BlockPos.MutableBlockPos neighbourPos = new BlockPos.MutableBlockPos();
        for (Direction direction : Direction.values())
        {
            neighbourPos.setWithOffset(pos, direction);
            if (!level.getBlockState(neighbourPos).is(this)) continue;
            if (++count >= limit) return false;
        }

        return true;
    }
}
