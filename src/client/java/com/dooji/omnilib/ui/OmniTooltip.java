package com.dooji.omnilib.ui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper.Argb;

import java.util.Collections;
import java.util.List;

public class OmniTooltip {
    private final String categoryTitle;
    private final List<ItemStack> itemStacks;
    private final List<Text> textList;

    private static final int DEFAULT_ICON_SIZE = 16;
    private static final int DEFAULT_PADDING = 8;
    private static final int DEFAULT_LINE_SPACING = 4;
    private static final int DEFAULT_BACKGROUND_COLOR = Argb.getArgb(150, 60, 60, 60);
    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFF;

    private final int iconSize;
    private final int padding;
    private final int lineSpacing;
    private final int backgroundColor;
    private final Identifier backgroundTexture;
    private final int textColor;
    private final Identifier customIconTexture;
    private final int customIconWidth;
    private final int customIconHeight;

    public OmniTooltip(
            String categoryTitle,
            List<ItemStack> itemStacks,
            List<Text> textList,
            int iconSize,
            int padding,
            int lineSpacing,
            int backgroundColor,
            Identifier backgroundTexture,
            int textColor,
            Identifier customIconTexture,
            int customIconWidth,
            int customIconHeight) {
        if (backgroundColor == 0 && backgroundTexture == null) {
            throw new IllegalArgumentException("Either backgroundColor or backgroundTexture must be specified.");
        }
        this.categoryTitle = categoryTitle;
        this.itemStacks = itemStacks != null ? itemStacks : Collections.emptyList();
        this.textList = textList != null ? textList : Collections.emptyList();
        this.iconSize = iconSize > 0 ? iconSize : DEFAULT_ICON_SIZE;
        this.padding = padding > 0 ? padding : DEFAULT_PADDING;
        this.lineSpacing = lineSpacing > 0 ? lineSpacing : DEFAULT_LINE_SPACING;
        this.backgroundColor = backgroundColor > 0 ? backgroundColor : DEFAULT_BACKGROUND_COLOR;
        this.backgroundTexture = backgroundTexture;
        this.textColor = textColor > 0 ? textColor : DEFAULT_TEXT_COLOR;
        this.customIconTexture = customIconTexture;
        this.customIconWidth = customIconWidth > 0 ? customIconWidth : iconSize;
        this.customIconHeight = customIconHeight > 0 ? customIconHeight : iconSize;
    }

    public void render(DrawContext context, TextRenderer textRenderer, int x, int y) {
        int tooltipWidth = getTooltipWidth(textRenderer);
        int tooltipHeight = getTooltipHeight();

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 1);

        drawBackground(context, x, y, tooltipWidth, tooltipHeight);

        int yOffset = padding;

        context.drawTextWithShadow(
                textRenderer,
                Text.literal(categoryTitle).styled(style -> style.withBold(true)),
                x + padding,
                y + yOffset,
                textColor
        );
        yOffset += iconSize + lineSpacing;

        for (int i = 0; i < textList.size(); i++) {
            if (customIconTexture != null) {
                drawCustomIcon(context, x + padding, y + yOffset);
            } else if (i < itemStacks.size()) {
                ItemStack itemStack = itemStacks.get(i);
                context.drawItem(itemStack, x + padding, y + yOffset);
            }

            context.drawTextWithShadow(
                    textRenderer,
                    textList.get(i),
                    x + iconSize + padding * 2,
                    y + yOffset + (iconSize / 2 - textRenderer.fontHeight / 2),
                    textColor
            );

            yOffset += iconSize + lineSpacing;
        }

        context.getMatrices().pop();
    }

    private int getTooltipWidth(TextRenderer textRenderer) {
        int maxWidth = textRenderer.getWidth(Text.literal(categoryTitle).styled(style -> style.withBold(true)));
        for (Text text : textList) {
            int textWidth = textRenderer.getWidth(text);
            if (textWidth > maxWidth) {
                maxWidth = textWidth;
            }
        }
        return maxWidth + iconSize + padding * 3;
    }

    private int getTooltipHeight() {
        return (textList.size() + 1) * (iconSize + lineSpacing) - lineSpacing + padding * 2;
    }

    private void drawBackground(DrawContext context, int x, int y, int width, int height) {
        if (backgroundTexture != null) {
            context.drawTexture(backgroundTexture, x - padding, y - padding, 0, 0, width + padding * 2, height + padding * 2);
        } else {
            context.fill(x - padding, y - padding, x + width + padding, y + height + padding, backgroundColor);
        }
    }

    private void drawCustomIcon(DrawContext context, int x, int y) {
        context.drawTexture(
                customIconTexture,
                x,
                y,
                0,
                0,
                customIconWidth,
                customIconHeight,
                customIconWidth,
                customIconHeight
        );
    }
}