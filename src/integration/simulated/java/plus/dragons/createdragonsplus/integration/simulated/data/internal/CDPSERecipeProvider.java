package plus.dragons.createdragonsplus.integration.simulated.data.internal;

import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import dev.eriksonn.aeronautics.neoforge.index.AeroFluidsNeoForge;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import org.jetbrains.annotations.NotNull;
import plus.dragons.createdragonsplus.integration.ModIntegration;
import plus.dragons.createdragonsplus.integration.simulated.config.CDPSEConfig;

import java.util.concurrent.CompletableFuture;

import static com.simibubi.create.AllItems.COPPER_SHEET;
import static net.minecraft.world.item.Items.*;
import static plus.dragons.createdragonsplus.common.CDPCommon.REGISTRATE;
import static plus.dragons.createdragonsplus.data.recipe.VanillaRecipeBuilders.shaped;
import static plus.dragons.createdragonsplus.integration.simulated.common.registry.CDPSEBlocks.FRAGILE_FLUID_TANK;
import static plus.dragons.createdragonsplus.integration.simulated.common.registry.CDPSEBlocks.LEVITITE_FRAGILE_FLUID_TANK;

public class CDPSERecipeProvider extends RegistrateRecipeProvider {
    public CDPSERecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(REGISTRATE, output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        shaped().output(FRAGILE_FLUID_TANK, 1)
                .define('t', COPPER_SHEET)
                .define('n', BARREL)
                .define('g', GOLD_INGOT)
                .pattern(" t ")
                .pattern("gng")
                .pattern(" t ")
                .unlockedBy("has_copper_sheet", has(COPPER_SHEET))
                .withCondition(CDPSEConfig.features().fragileFluidTank)
                .withCondition(ModIntegration.AERONAUTICS.condition())
                .accept(output);

        shaped().output(LEVITITE_FRAGILE_FLUID_TANK, 8)
                .define('l', FRAGILE_FLUID_TANK)
                .define('t', AeroFluidsNeoForge.LEVITITE_BLEND.getBucket().get())
                .pattern("lll")
                .pattern("ltl")
                .pattern("lll")
                .unlockedBy("has_fragile_fluid_tank", has(AeroFluidsNeoForge.LEVITITE_BLEND.getBucket().get()))
                .withCondition(CDPSEConfig.features().fragileFluidTank)
                .withCondition(ModIntegration.AERONAUTICS.condition())
                .accept(output);
    }

    @Override
    public final @NotNull String getName() {
        return "Simulated Extension Recipes";
    }
}
