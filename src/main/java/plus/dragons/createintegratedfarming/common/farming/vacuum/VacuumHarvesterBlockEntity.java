package plus.dragons.createintegratedfarming.common.farming.vacuum;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.item.ItemHandlerWrapper;
import com.simibubi.create.foundation.item.ItemHelper;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.Containers;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;
import plus.dragons.createintegratedfarming.api.harvester.AreaHarvestContext;
import plus.dragons.createintegratedfarming.config.CIFConfig;

public class VacuumHarvesterBlockEntity extends KineticBlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(18) {
        @Override protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null) level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
        }
    };
    private final IItemHandler outputHandler = new ItemHandlerWrapper(inventory) {
        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) { return stack; }
    };
    private double chargeProgress;
    private int releaseTicks;
    private float previousHeadOffset;
    private float headOffset;

    public VacuumHarvesterBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    public @Nullable IItemHandler getItemHandler(@Nullable Direction side) { return side == Direction.DOWN ? null : outputHandler; }

    @Override public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            IItemHandler handler = getItemHandler(side);
            if (handler != null) return LazyOptional.of(() -> handler).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override public void tick() {
        super.tick();
        if (level == null)
            return;
        if (level.isClientSide) {
            tickClientCycle();
            return;
        }
        if (releaseTicks > 0) {
            if (!isPowered())
                return;
            if (--releaseTicks == 0)
                chargeProgress = 0;
            setChanged();
            if (releaseTicks == 0)
                sendData();
            return;
        }
        if (!isPowered())
            return;
        chargeProgress = VacuumHarvesterCycle.advanceCharge(
                chargeProgress, VacuumHarvesterCycle.stationaryIncrement(getSpeed()));
        if (chargeProgress < 1) {
            setChanged();
            return;
        }
        releaseTicks = VacuumHarvesterCycle.RELEASE_DURATION;
        AreaHarvestContext context = new AreaHarvestContext(level, true, false, ItemStack.EMPTY,
                this::collectOrDrop, this::extractSeed);
        var result = VacuumHarvesterHarvesting.harvestArea(
                context, worldPosition, CIFConfig.server().vacuumHarvesterRange.get());
        VacuumHarvesterEffects.emitSuction(
                (ServerLevel) level,
                VacuumHarvesterEffects.intake(worldPosition, VacuumHarvesterCycle.MAX_HEAD_OFFSET),
                result.particleSources());
        setChanged();
        sendData();
    }

    private void tickClientCycle() {
        previousHeadOffset = headOffset;
        if (releaseTicks > 0) {
            if (isPowered()) {
                if (--releaseTicks == 0)
                    chargeProgress = 0;
            }
        } else if (isPowered()) {
            chargeProgress = VacuumHarvesterCycle.advanceCharge(
                    chargeProgress, VacuumHarvesterCycle.stationaryIncrement(getSpeed()));
        }
        headOffset = VacuumHarvesterCycle.getHeadOffset(chargeProgress, releaseTicks);
        if (releaseTicks == 0
                && isPowered()
                && Math.floorMod(level.getGameTime() + worldPosition.hashCode(), 8) == 0)
            VacuumHarvesterEffects.spawnExhaust(level, VacuumHarvesterEffects.intake(worldPosition, headOffset));
    }

    private boolean isPowered() {
        return getSpeed() != 0 && !isOverStressed();
    }

    private ItemStack extractSeed(Predicate<ItemStack> predicate, int amount) { return ItemHelper.extract(inventory, predicate, amount, false); }

    private void collectOrDrop(ItemStack stack) {
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, stack, false);
        if (!remainder.isEmpty())
            Containers.dropItemStack(level, worldPosition.getX() + .5, worldPosition.getY() + .5, worldPosition.getZ() + .5, remainder);
    }

    public boolean giveContentsTo(net.minecraft.world.entity.player.Player player) {
        boolean moved = false;
        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.extractItem(i, inventory.getSlotLimit(i), false);
            if (!stack.isEmpty()) { player.getInventory().placeItemBackInInventory(stack); moved = true; }
        }
        return moved;
    }

    @Override protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket); tag.put("Inventory", inventory.serializeNBT()); tag.putDouble(VacuumHarvesterCycle.CHARGE_PROGRESS, chargeProgress);
        tag.putInt(VacuumHarvesterCycle.RELEASE_TICKS, releaseTicks);
    }
    @Override protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket); inventory.deserializeNBT(tag.getCompound("Inventory")); chargeProgress = Mth.clamp(tag.getDouble(VacuumHarvesterCycle.CHARGE_PROGRESS), 0, 1);
        releaseTicks = Mth.clamp(tag.getInt(VacuumHarvesterCycle.RELEASE_TICKS), 0, VacuumHarvesterCycle.RELEASE_DURATION);
        headOffset = VacuumHarvesterCycle.getHeadOffset(chargeProgress, releaseTicks);
        previousHeadOffset = headOffset;
    }
    public float getRenderedHeadOffset(float partialTicks) { return Mth.lerp(partialTicks, previousHeadOffset, headOffset); }
    public void setCycleProgress(double progress, int releaseTicks) {
        chargeProgress = Mth.clamp(progress, 0, 1);
        this.releaseTicks = Mth.clamp(releaseTicks, 0, VacuumHarvesterCycle.RELEASE_DURATION);
        headOffset = VacuumHarvesterCycle.getHeadOffset(chargeProgress, this.releaseTicks);
        previousHeadOffset = headOffset;
    }
    @Override public void destroy() { super.destroy(); ItemHelper.dropContents(level, worldPosition, inventory); }
}
