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
import java.util.function.Consumer;

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
	 * This version uses the default max height (140) and default scroll speed (25.0) when the content exceeds the max height.
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
	 * @param maxHeight         Maximum height of the tooltip before scrolling.
	 * @param scrollSpeed       Scroll speed in pixels per second.
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
			int maxHeight,
			double scrollSpeed,
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
				16,
				maxHeight,
				scrollSpeed
		);

		tooltip.render(context, textRenderer, x, y);
	}

	/**
	 * Creates an OmniText from a plain string.
	 * The returned OmniText object wraps the given string, enabling support for Markdown rendering.
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
	 *
	 * @param text The existing Text object to wrap.
	 * @return An OmniText object wrapping the given Text.
	 */
	public static OmniText createOmniText(Text text) {
		return OmniText.of(text);
	}

	/**
	 * Creates an OmniText object from a plain string with optional texture data.
	 * The returned OmniText object wraps the given string and allows for storing associated texture data.
	 * This texture data can be accessed later for rendering purposes, such as in components like OmniPopup.
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

	/**
	 * Creates a customizable OmniField widget.
	 *
	 * @param textRenderer     The text renderer to use.
	 * @param x                X-coordinate of the field.
	 * @param y                Y-coordinate of the field.
	 * @param width            Width of the field.
	 * @param height           Height of the field.
	 * @param message          Placeholder text for the field.
	 * @param searchQuery      The text value for the field.
	 * @param changedListener  Listener for text change events.
	 * @param maxLength        Maximum length of characters. Null for default (100).
	 * @param cursorColor      Color of the cursor. Null for default (white).
	 * @param backgroundColor  Background color of the field. Null for default.
	 * @param hoveredColor     Hovered background color. Null for default.
	 * @param cursorTexture    Custom cursor texture. Null for default.
	 * @param backgroundTexture Custom background texture. Null for default.
	 * @param hoveredTexture   Custom hovered background texture. Null for default.
	 * @return A new OmniField instance.
	 */
	public static OmniField createOmniField(
			TextRenderer textRenderer,
			int x,
			int y,
			int width,
			int height,
			Text message,
			String searchQuery,
			Consumer<String> changedListener,
			Integer maxLength,
			Integer cursorColor,
			Integer backgroundColor,
			Integer hoveredColor,
			Identifier cursorTexture,
			Identifier backgroundTexture,
			Identifier hoveredTexture) {
		OmniField field = new OmniField(textRenderer, x, y, width, height, message, maxLength, cursorColor, backgroundColor, hoveredColor, cursorTexture, backgroundTexture, hoveredTexture);
		if (searchQuery != null) {
			field.setText(searchQuery);
		}
		if (changedListener != null) {
			field.setChangedListener(changedListener);
		}
		return field;
	}

	/**
	 * Creates an OmniField widget.
	 *
	 * @param textRenderer    The text renderer to use.
	 * @param x               X-coordinate of the field.
	 * @param y               Y-coordinate of the field.
	 * @param width           Width of the field.
	 * @param height          Height of the field.
	 * @param message         Placeholder text for the field.
	 * @param searchQuery     The text value for the field.
	 * @param changedListener Listener for text change events.
	 * @return A new OmniField instance.
	 */
	public static OmniField createOmniField(
			TextRenderer textRenderer,
			int x,
			int y,
			int width,
			int height,
			Text message,
			String searchQuery,
			Consumer<String> changedListener) {
		OmniField field = new OmniField(textRenderer, x, y, width, height, message);
		if (searchQuery != null) {
			field.setText(searchQuery);
		}
		if (changedListener != null) {
			field.setChangedListener(changedListener);
		}
		return field;
	}

	/**
	 * Creates a text-only OmniButton widget.
	 *
	 * @param x               X-coordinate of the button.
	 * @param y               Y-coordinate of the button.
	 * @param width           Width of the button.
	 * @param height          Height of the button.
	 * @param message         Text to display.
	 * @param color           Background color of the button.
	 * @param hoverColor      Hover background color.
	 * @param textColor       Text color.
	 * @param textHoverColor  Hover text color.
	 * @param onPress         Action on button press.
	 * @return A new OmniButton instance.
	 */
	public static OmniButton createOmniButton(
			int x,
			int y,
			int width,
			int height,
			Text message,
			int color,
			int hoverColor,
			int textColor,
			int textHoverColor,
			Runnable onPress) {
		return new OmniButton(x, y, width, height, message, color, hoverColor, textColor, textHoverColor, onPress);
	}

	/**
	 * Creates an image-only OmniButton widget.
	 *
	 * @param x           X-coordinate of the button.
	 * @param y           Y-coordinate of the button.
	 * @param width       Width of the button.
	 * @param height      Height of the button.
	 * @param texture     Identifier for the button's texture.
	 * @param color       Background color of the button.
	 * @param hoverColor  Hover background color.
	 * @param onPress     Action on button press.
	 * @return A new OmniButton instance.
	 */
	public static OmniButton createOmniButton(
			int x,
			int y,
			int width,
			int height,
			Identifier texture,
			int color,
			int hoverColor,
			Runnable onPress) {
		return new OmniButton(x, y, width, height, texture, color, hoverColor, onPress);
	}

	/**
	 * Creates an image-only OmniButton widget with hover texture.
	 *
	 * @param x             X-coordinate of the button.
	 * @param y             Y-coordinate of the button.
	 * @param width         Width of the button.
	 * @param height        Height of the button.
	 * @param texture       Identifier for the button's default texture.
	 * @param hoverTexture  Identifier for the button's hover texture.
	 * @param color         Background color of the button.
	 * @param hoverColor    Hover background color.
	 * @param onPress       Action on button press.
	 * @return A new OmniButton instance.
	 */
	public static OmniButton createOmniButton(
			int x,
			int y,
			int width,
			int height,
			Identifier texture,
			Identifier hoverTexture,
			int color,
			int hoverColor,
			Runnable onPress) {
		return new OmniButton(x, y, width, height, texture, hoverTexture, color, hoverColor, onPress);
	}

	/**
	 * Creates an OmniListWidget.
	 *
	 * @param client                  The Minecraft client instance.
	 * @param listWidth               Width of the list widget.
	 * @param listHeight              Height of the list widget.
	 * @param top                     Top position of the list.
	 * @param bottom                  Bottom position of the list.
	 * @param itemWidth               Width of each entry.
	 * @param itemHeight              Height of each entry.
	 * @param buttonWidth 			  The width of the buttons displayed in each list entry.
	 * @param spacing                 Spacing between entries.
	 * @param backgroundTexture       Background texture for entries.
	 * @param hoverBackgroundTexture  Hover texture for entries.
	 * @param backgroundColor         Background color for entries.
	 * @param hoverBackgroundColor    Hover color for entries.
	 * @param scrollbarBackgroundColor Color for scrollbar background.
	 * @param scrollbarColor          Color for scrollbar handle.
	 * @param scrollbarHoverColor     Hover color for scrollbar handle.
	 * @param clickCallback           Callback triggered when an entry is clicked, providing the entry's index as an int.
	 * @return An OmniListWidget instance.
	 */
	public static OmniListWidget createOmniListWidget(
			MinecraftClient client,
			int listWidth,
			int listHeight,
			int top,
			int bottom,
			int itemWidth,
			int itemHeight,
			int buttonWidth,
			int spacing,
			Identifier backgroundTexture,
			Identifier hoverBackgroundTexture,
			int backgroundColor,
			int hoverBackgroundColor,
			int scrollbarBackgroundColor,
			int scrollbarColor,
			int scrollbarHoverColor,
			Consumer<Integer> clickCallback
	) {
		return new OmniListWidget(
				client,
				listWidth,
				listHeight,
				top,
				bottom,
				itemWidth,
				itemHeight,
				buttonWidth,
				spacing,
				backgroundTexture,
				hoverBackgroundTexture,
				backgroundColor,
				hoverBackgroundColor,
				scrollbarBackgroundColor,
				scrollbarColor,
				scrollbarHoverColor,
				clickCallback
		);
	}
}