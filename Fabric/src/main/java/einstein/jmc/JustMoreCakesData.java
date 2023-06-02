package einstein.jmc;

import einstein.jmc.data.providers.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class JustMoreCakesData implements DataGeneratorEntrypoint {

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator) {
        FabricDataGenerator.Pack pack = generator.createPack();
        FabricTagProvider.BlockTagProvider blockTags = pack.addProvider(ModBlockTagsProvider::new);
        pack.addProvider((output, registriesFuture) -> new ModItemTagsProvider(output, registriesFuture, blockTags));
        pack.addProvider(ModPOITagsProvider::new);
        pack.addProvider(ModAdvancementProvider::new);
        pack.addProvider(ModRecipeProvider::new);
        pack.addProvider(ModModelProvider::new);
        pack.addProvider(ModBlockLootTableProvider::new);
        pack.addProvider((FabricDataGenerator.Pack.Factory<ModCakeEffectsProvider>) ModCakeEffectsProvider::new);
    }
}
