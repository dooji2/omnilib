package com.dooji.omnilib.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OmniFieldListWidget extends ElementListWidget<OmniFieldListWidget.OmniFieldEntry> {
    private final int itemWidth;
    private final int itemHeight;
    private final int fieldWidth;
    private final int fieldSpacing;
    private final int spacing;
    private final Identifier backgroundTexture;
    private final Identifier hoverBackgroundTexture;
    private final int backgroundColor;
    private final int hoverBackgroundColor;
    private final int scrollbarBackgroundColor;
    private final int scrollbarColor;
    private final int scrollbarHoverColor;
    private OmniFieldEntry hoveredEntry;

    public OmniFieldListWidget(
            MinecraftClient client,
            int listWidth,
            int listHeight,
            int top,
            int bottom,
            int itemWidth,
            int itemHeight,
            int fieldWidth,
            int fieldSpacing,
            int spacing,
            Identifier backgroundTexture,
            Identifier hoverBackgroundTexture,
            int backgroundColor,
            int hoverBackgroundColor,
            int scrollbarBackgroundColor,
            int scrollbarColor,
            int scrollbarHoverColor
    ) {
        super(client, listWidth, listHeight, top, itemHeight + spacing);
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.fieldWidth = fieldWidth;
        this.fieldSpacing = fieldSpacing;
        this.spacing = spacing;
        this.backgroundTexture = backgroundTexture;
        this.hoverBackgroundTexture = hoverBackgroundTexture;
        this.backgroundColor = backgroundColor;
        this.hoverBackgroundColor = hoverBackgroundColor;
        this.scrollbarBackgroundColor = scrollbarBackgroundColor;
        this.scrollbarColor = scrollbarColor;
        this.scrollbarHoverColor = scrollbarHoverColor;
    }

    public void setItemsWithFields(List<ItemStack> itemStacks, List<String> content, List<OmniField> fields) {
        this.clearEntries();
        for (int i = 0; i < content.size(); i++) {
            ItemStack itemStack = (itemStacks != null && itemStacks.size() > i) ? itemStacks.get(i) : ItemStack.EMPTY;
            String text = content.get(i);
            OmniField field = (fields != null && fields.size() > i) ? fields.get(i) : null;
            this.addEntry(new OmniFieldEntry(this, i, itemStack, text, field, itemWidth, itemHeight, fieldWidth, fieldSpacing, spacing));
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        this.hoveredEntry = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;
        this.drawMenuListBackground(context);
        this.enableScissor(context);

        if (this.getEntryCount() > 0 && this.getY() >= 0) {
            int headerX = this.getRowLeft();
            int headerY = this.getY() + 4 - (int) this.getScrollAmount();
            this.renderHeader(context, headerX, headerY);
        }

        this.renderList(context, mouseX, mouseY, delta);

        context.disableScissor();
        this.drawHeaderAndFooterSeparators(context);
        this.renderScrollbar(context, mouseX, mouseY);
    }

    private void renderScrollbar(DrawContext context, int mouseX, int mouseY) {
        if (isScrollbarVisible()) {
            int scrollbarX = getScrollbarX();
            int scrollbarY = getY();
            int scrollbarHeight = getHeight();
            int handleHeight = MathHelper.clamp((int) ((float) height * height / getMaxPosition()), 32, height - 8);
            int handleY = (int) getScrollAmount() * (height - handleHeight) / getMaxScroll() + getY();
            handleY = MathHelper.clamp(handleY, getY(), getBottom() - handleHeight);

            context.fill(scrollbarX, scrollbarY, scrollbarX + 6, scrollbarY + scrollbarHeight, scrollbarBackgroundColor);

            boolean isHovered = mouseX >= scrollbarX && mouseX < scrollbarX + 6
                    && mouseY >= handleY && mouseY < handleY + handleHeight;
            int handleColor = isHovered ? scrollbarHoverColor : scrollbarColor;

            context.fill(scrollbarX, handleY, scrollbarX + 6, handleY + handleHeight, handleColor);
        }
    }

    @Override
    protected int getScrollbarX() {
        return this.width - 6;
    }

    @Override
    public int getRowWidth() {
        return this.itemWidth;
    }

    @Nullable
    public OmniFieldEntry getHoveredEntry() {
        return this.hoveredEntry;
    }

    public static class OmniFieldEntry extends ElementListWidget.Entry<OmniFieldEntry> {
        private final OmniFieldListWidget parent;
        private final int index;
        private final ItemStack itemStack;
        private final String content;
        private final OmniField field;
        private final int entryWidth;
        private final int entryHeight;
        private final int fieldWidth;
        private final int fieldSpacing;
        private final int spacing;

        public OmniFieldEntry(OmniFieldListWidget parent, int index, @Nullable ItemStack itemStack, String content, OmniField field, int entryWidth, int entryHeight, int fieldWidth, int fieldSpacing, int spacing) {
            this.parent = parent;
            this.index = index;
            this.itemStack = itemStack;
            this.content = content;
            this.field = field;
            this.entryWidth = entryWidth;
            this.entryHeight = entryHeight;
            this.fieldWidth = fieldWidth;
            this.fieldSpacing = fieldSpacing;
            this.spacing = spacing;
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            hovered = this.parent.getHoveredEntry() == this;

            int bgColor = hovered ? this.parent.hoverBackgroundColor : this.parent.backgroundColor;
            context.fill(x, y, x + this.entryWidth, y + this.entryHeight, bgColor);

            int itemStackX = x + 10;
            int contentStartX = this.itemStack != null ? itemStackX + 20 + 10 : itemStackX;

            if (this.itemStack != null) {
                context.drawItem(this.itemStack, itemStackX, y + (this.entryHeight - 16) / 2);
            }

            int fieldX = x + this.entryWidth - this.fieldWidth - this.fieldSpacing;

            int textEndX = fieldX - 10;
            drawScrollableText(context, MinecraftClient.getInstance().textRenderer, Text.of(this.content), contentStartX, y, textEndX, y + this.entryHeight, 0xFFFFFF);

            this.field.setX(fieldX);
            this.field.setY(y + (this.entryHeight - this.field.getHeight()) / 2);
            this.field.setWidth(this.fieldWidth);
            this.field.render(context, mouseX, mouseY, delta);
        }

        @Override
        public List<? extends Element> children() {
            return List.of(this.field);
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return List.of(this.field);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return this.field.mouseClicked(mouseX, mouseY, button);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            return this.field.isFocused() && this.field.keyPressed(keyCode, scanCode, modifiers);
        }

        @Override
        public boolean charTyped(char chr, int modifiers) {
            return this.field.isFocused() && this.field.charTyped(chr, modifiers);
        }

        private static void drawScrollableText(DrawContext context, TextRenderer textRenderer, Text text, int startX, int startY, int endX, int endY, int color) {
            int textWidth = textRenderer.getWidth(text);
            int availableWidth = endX - startX;

            if (textWidth > availableWidth) {
                int overflowWidth = textWidth - availableWidth;

                double currentTime = (double) System.currentTimeMillis() / 1000.0;
                double scrollPeriod = Math.max(overflowWidth * 0.5, 3.0);
                double scrollFactor = Math.sin(Math.PI * 0.5 * Math.cos(2 * Math.PI * currentTime / scrollPeriod)) / 2.0 + 0.5;
                double scrollOffset = MathHelper.lerp(scrollFactor, 0.0, overflowWidth);

                context.enableScissor(startX, startY, endX, endY);
                context.drawTextWithShadow(textRenderer, text, startX - (int) scrollOffset, startY + (endY - startY - textRenderer.fontHeight) / 2, color);
                context.disableScissor();
            } else {
                context.drawTextWithShadow(textRenderer, text, startX, startY + (endY - startY - textRenderer.fontHeight) / 2, color);
            }
        }
    }
}