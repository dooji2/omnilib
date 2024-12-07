package com.dooji.omnilib;

import com.dooji.omnilib.text.OmniText;
import com.dooji.omnilib.ui.*;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class OmnilibClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
	}

	/**
	 * Displays an OmniToast with optional custom icon texture or ItemStack icon.
	 *
	 * @param title             Title text of the toast.
	 * @param description       Description text of the toa	st.
	 * @param duration          Duration in milliseconds the toast will display.
	 * @param titleColor        Color of the title text.
	 * @param descriptionColor  Color of the description text.
	 * @param backgroundTexture Identifier for the toast's background texture.
	 * @param iconTexture       Identifier for the toast's icon texture. Ignored if iconItemStack is provided.
	 * @param iconItemStack     ItemStack to display as the icon. Can be null.
	 * @param iconSize          Size of the icon in pixels (default is 16).
	 * @param textureWidth      Width of the toast texture (default is 160).
	 * @param textureHeight     Height of the toast texture (default is 32).
	 */
	public static void showToast(
			Text title,
			Text description,
			long duration,
			int titleColor,
			int descriptionColor,
			Identifier backgroundTexture,
			Identifier iconTexture,
			ItemStack iconItemStack,
			int iconSize,
			int textureWidth,
			int textureHeight) {

		OmniToast toast = new OmniToast(
				title,
				description,
				duration,
				titleColor,
				descriptionColor,
				backgroundTexture,
				iconTexture,
				iconItemStack,
				iconSize,
				textureWidth,
				textureHeight
		);

		MinecraftClient.getInstance().getToastManager().add(toast);
	}

	/**
	 * Displays an OmniTooltip with fully customizable options.
	 *
	 * @param context           The draw context for rendering.
	 * @param textRenderer      The text renderer for drawing text.
	 * @param categoryTitle     Title of the tooltip category.
	 * @param itemStacks        List of ItemStacks to display. Can be null or empty.
	 * @param textList          List of text entries for the tooltip content.
	 * @param backgroundColor   Background color of the tooltip in ARGB format. Ignored if `backgroundTexture` is provided.
	 * @param backgroundTexture Identifier for a custom background texture. Can be null.
	 * @param textColor         Text color in RGB format.
	 * @param customTexture     Identifier for a custom icon texture. Can be null.
	 * @param x                 X-coordinate for the tooltip.
	 * @param y                 Y-coordinate for the tooltip.
	 */
	public static void showTooltip(
			DrawContext context,
			TextRenderer textRenderer,
			String categoryTitle,
			List<ItemStack> itemStacks,
			List<Text> textList,
			int backgroundColor,
			Identifier backgroundTexture,
			int textColor,
			Identifier customTexture,
			int x,
			int y) {

		OmniTooltip tooltip = new OmniTooltip(
				categoryTitle,
				itemStacks,
				textList,
				16,
				8,
				4,
				backgroundColor,
				backgroundTexture,
				textColor,
				customTexture,
				16,
				16
		);

		tooltip.render(context, textRenderer, x, y);
	}

	/**
	 * Creates an OmniText from a plain string.
	 * The returned OmniText object wraps the given string, enabling support for Markdown rendering.
	 * This method also does not include any texture or icon.
	 *
	 * @param content The content of the text, including Markdown syntax (e.g., `**bold**`, `_italic_`).
	 * @return An OmniText object representing the given content.
	 */
	public static OmniText createOmniText(String content) {
		return OmniText.of(Text.of(content));
	}

	/**
	 * Creates an OmniText from an existing Text object.
	 * The returned OmniText object wraps the given Text, enabling support for Markdown rendering.
	 * This method does not include any texture or icon.
	 *
	 * @param text The existing Text object to wrap.
	 * @return An OmniText object wrapping the given Text.
	 */
	public static OmniText createOmniText(Text text) {
		return OmniText.of(text);
	}

	/**
	 * Creates an OmniText from a plain string with an optional texture icon.
	 * The returned OmniText object wraps the given string, enabling support for Markdown rendering.
	 * Additionally, it allows the inclusion of an icon at the beginning of the text.
	 * This method is useful for showing icons along with the text, such as in a list with custom icons.
	 *
	 * @param content       The content of the text, including Markdown syntax (e.g., `**bold**`, `_italic_`).
	 * @param textureIdentifier  An {@link Identifier} pointing to the texture to display at the beginning of the text. Can be null if no icon is needed.
	 * @param textureWidth  The width of the icon texture in pixels. Default is 16px.
	 * @param textureHeight The height of the icon texture in pixels. Default is 16px.
	 * @return An OmniText object representing the given content with the specified icon.
	 */
	public static OmniText createOmniText(String content, Identifier textureIdentifier, int textureWidth, int textureHeight) {
		return OmniText.of(content, textureIdentifier, textureWidth, textureHeight);
	}	
}