package dev.hephaestus.glowcase.block;

import dev.hephaestus.glowcase.Glowcase;
import dev.hephaestus.glowcase.block.entity.TextBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtString;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TextBlock extends GlowcaseBlock implements BlockEntityProvider {
	public TextBlock() {
		super();
		this.setDefaultState(this.getDefaultState().with(Properties.ROTATION, 0));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		super.appendProperties(builder);
		builder.add(Properties.ROTATION);
	}

	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		return this.getDefaultState().with(Properties.ROTATION, MathHelper.floor((double) ((180.0F + ctx.getPlayerYaw()) * 16.0F / 360.0F) + 0.5D) & 15);
	}

	@Override
	protected boolean openEditScreen(BlockPos pos) {
		Glowcase.proxy.openTextBlockEditScreen(pos);
		return true;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (world.isClient && placer instanceof PlayerEntity player && canEditGlowcase(player, pos)) {
			//load any ctrl-picked NBT clientside
			NbtComponent blockEntityTag = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
			if (blockEntityTag != null && world.getBlockEntity(pos) instanceof BlockEntity be) blockEntityTag.applyToBlockEntity(be, world.getRegistryManager());
			openEditScreen(pos);
		}
		if (world.getBlockEntity(pos) instanceof TextBlockEntity be) { // Wish we had ctx.side right now...
			if (be.zOffset == TextBlockEntity.ZOffset.CENTER && Math.abs(placer.getPitch()) < 30) {
				be.zOffset = TextBlockEntity.ZOffset.BACK;
			} else if (be.zOffset == TextBlockEntity.ZOffset.BACK && Math.abs(placer.getPitch()) > 60) {
				be.zOffset = TextBlockEntity.ZOffset.CENTER;
			}
			be.markDirty();
		}
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new TextBlockEntity(pos, state);
	}

	@Override
	public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
		tooltip.add(Text.translatable("block.glowcase.text_block.tooltip.0").formatted(Formatting.GRAY));
		tooltip.add(Text.translatable("block.glowcase.generic.tooltip").formatted(Formatting.DARK_GRAY));
		tooltip.add(Text.translatable("block.glowcase.text_block.tooltip.1").formatted(Formatting.DARK_GRAY));
		NbtComponent component = stack.get(DataComponentTypes.BLOCK_ENTITY_DATA);
		if (component == null) return;
		NbtCompound nbt = component.getNbt();
		if (nbt == null) return;
		for (NbtElement element : nbt.getList("lines", NbtElement.STRING_TYPE)) {
			if (element instanceof NbtString line && !line.asString().isBlank()) {
				tooltip.add(Text.literal((line.asString().length() > 20 ? "%s...\"" : "%s").formatted(line.asString().substring(0, Math.min(line.asString().length(), 20)))).formatted(Formatting.DARK_PURPLE));
			}
		}
	}
}
