package plus.dragons.createintegratedfarming;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import plus.dragons.createintegratedfarming.common.CIFCommon;

@Mod(CreateIntegratedFarming.ID)
public class CreateIntegratedFarming {
    public static final String ID = "create_integrated_farming";

    public CreateIntegratedFarming() {
        CIFCommon.init(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
