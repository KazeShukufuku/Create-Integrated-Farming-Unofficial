package plus.dragons.createintegratedfarming.common.farming.vacuum;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.item.ItemHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import plus.dragons.createintegratedfarming.common.registry.CIFBlockEntities;

public class VacuumHarvesterBlock extends KineticBlock implements IBE<VacuumHarvesterBlockEntity> {
    private static final VoxelShape SHAPE = Shapes.or(box(1, 0, 1, 15, 2, 15), box(2, 2, 2, 14, 14, 14), box(3, 14, 3, 13, 16, 13));

    public VacuumHarvesterBlock(Properties properties) { super(properties); }

    @Override
    public boolean hasShaftTowards(LevelReader level, BlockPos pos, BlockState state, Direction face) { return face == Direction.DOWN; }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) { return Direction.Axis.Y; }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide)
            return InteractionResult.SUCCESS;
        return onBlockEntityUse(level, pos, harvester -> harvester.giveContentsTo(player) ? InteractionResult.SUCCESS : InteractionResult.PASS);
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) { return true; }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) { return ItemHelper.calcRedstoneFromBlockEntity(this, level, pos); }

    @Override
    public Class<VacuumHarvesterBlockEntity> getBlockEntityClass() { return VacuumHarvesterBlockEntity.class; }

    @Override
    public BlockEntityType<? extends VacuumHarvesterBlockEntity> getBlockEntityType() { return CIFBlockEntities.VACUUM_HARVESTER.get(); }
}
