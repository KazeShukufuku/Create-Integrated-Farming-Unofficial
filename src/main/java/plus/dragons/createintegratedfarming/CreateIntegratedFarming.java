package plus.dragons.createintegratedfarming;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import plus.dragons.createintegratedfarming.client.CIFClient;
import plus.dragons.createintegratedfarming.common.CIFCommon;

@Mod(CreateIntegratedFarming.ID)
public class CreateIntegratedFarming {
    public static final String ID = "create_integrated_farming";

    public CreateIntegratedFarming() {
        CIFCommon.init(FMLJavaModLoadingContext.get().getModEventBus());
        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> CIFClient::construct);
    }
}
