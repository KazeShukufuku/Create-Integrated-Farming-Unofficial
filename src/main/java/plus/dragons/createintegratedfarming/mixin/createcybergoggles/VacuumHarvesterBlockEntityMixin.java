/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createintegratedfarming.mixin.createcybergoggles;

import com.simibubi.create.AllSpecialTextures;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import io.github.forgestove.create_cyber_goggles.CCG;
import io.github.forgestove.create_cyber_goggles.core.api.OutlineRenderable;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import plus.dragons.createintegratedfarming.common.farming.vacuum.VacuumHarvesterBlockEntity;
import plus.dragons.createintegratedfarming.config.CIFConfig;
import plus.dragons.createintegratedfarming.integration.ModIntegration.Mods;

/**
 * Lets Create: Cyber Goggles render the vacuum harvester's harvest area when the
 * block entity is looked at. The area matches {@code VacuumHarvesterHarvesting}:
 * a cube extending {@code vacuumHarvesterRange} blocks horizontally on the X and
 * Z axes and one block above and below the machine on the Y axis.
 */
@Mixin(VacuumHarvesterBlockEntity.class)
@Restriction(require = @Condition(Mods.CREATE_CYBER_GOGGLES))
public abstract class VacuumHarvesterBlockEntityMixin extends KineticBlockEntity implements OutlineRenderable {
    public VacuumHarvesterBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void ccg$render() {
        int range = CIFConfig.server().vacuumHarvesterRange.get();
        var bounds = new AABB(
                worldPosition.getX() - range, worldPosition.getY() - 1, worldPosition.getZ() - range,
                worldPosition.getX() + range + 1, worldPosition.getY() + 2, worldPosition.getZ() + range + 1);
        Outliner.getInstance()
                .showAABB("VacuumHarvesterAreaBox" + this, bounds)
                .withFaceTextures(AllSpecialTextures.CHECKERED, AllSpecialTextures.HIGHLIGHT_CHECKERED)
                .lineWidth(1 / 16f)
                .colored(CCG.config.outliner.inColor);
    }
}
