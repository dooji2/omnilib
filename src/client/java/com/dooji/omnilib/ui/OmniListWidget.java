package com.dooji.omnilib.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

public class OmniListWidget extends ElementListWidget<OmniListWidget.OmniEntry> {
    private final int itemWidth;
    private final int itemHeight;
    private final int buttonWidth;
    private final int spacing;
    private final Identifier backgroundTexture;
    private final Identifier hoverBackgroundTexture;
    private final int backgroundColor;
    private final int hoverBackgroundColor;
    private final int scrollbarBackgroundColor;
    private final int scrollbarColor;
    private final int scrollbarHoverColor;
    private OmniEntry hoveredEntry;

    private final Consumer<Integer> clickCallback;

    public OmniListWidget(
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
        super(client, listWidth, listHeight, top, itemHeight + spacing);
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.buttonWidth = buttonWidth;
        this.spacing = spacing;
        this.backgroundTexture = backgroundTexture;
        this.hoverBackgroundTexture = hoverBackgroundTexture;
        this.backgroundColor = backgroundColor;
        this.hoverBackgroundColor = hoverBackgroundColor;
        this.scrollbarBackgroundColor = scrollbarBackgroundColor;
        this.scrollbarColor = scrollbarColor;
        this.scrollbarHoverColor = scrollbarHoverColor;
        this.clickCallback = clickCallback;
    }

    public void setItems(List<String> content, List<List<OmniButton>> buttons, List<String> footers) {
        this.clearEntries();
        for (int i = 0; i < content.size(); i++) {
            String text = content.get(i);
            List<OmniButton> itemButtons = buttons.size() > i ? buttons.get(i) : List.of();
            String footer = footers.size() > i ? footers.get(i) : "";
            this.addEntry(new OmniEntry(this, i, text, footer, itemButtons, itemWidth, itemHeight, buttonWidth, spacing));
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
    public OmniEntry getHoveredEntry() {
        return this.hoveredEntry;
    }

    public static class OmniEntry extends ElementListWidget.Entry<OmniEntry> {
        private final OmniListWidget parent;
        private final int index;
        private final String text;
        private final String footer;
        private final List<OmniButton> buttons;
        private final int entryWidth;
        private final int entryHeight;
        private final int buttonWidth;
        private final int spacing;

        public OmniEntry(OmniListWidget parent, int index, String text, String footer, List<OmniButton> buttons, int entryWidth, int entryHeight, int buttonWidth, int spacing) {
            this.parent = parent;
            this.index = index;
            this.text = text;
            this.footer = footer;
            this.entryWidth = entryWidth;
            this.entryHeight = entryHeight;
            this.buttonWidth = buttonWidth;
            this.spacing = spacing;
            this.buttons = new ArrayList<>(buttons);
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float delta) {
            hovered = this.parent.getHoveredEntry() == this;
        
            int bgColor = hovered ? parent.hoverBackgroundColor : parent.backgroundColor;
            context.fill(x, y, x + entryWidth, y + entryHeight, bgColor);            
        
            MinecraftClient client = MinecraftClient.getInstance();
            int margin = 10;
            int textStartX = x + margin;
            int textStartY = y + margin;
        
            int footerSpace = footer.isEmpty() ? 0 : client.textRenderer.fontHeight + 5;
            int contentHeight = entryHeight - footerSpace - (2 * margin);
            int textAreaWidth = entryWidth - buttonWidth - (2 * margin);
        
            List<String> wrappedText = wrapText(client, text, textAreaWidth, contentHeight, !buttons.isEmpty(), margin);
            for (String line : wrappedText) {
                context.drawText(client.textRenderer, line, textStartX, textStartY, 0xFFFFFF, false);
                textStartY += client.textRenderer.fontHeight;
            }
        
            if (!footer.isEmpty()) {
                int footerY = y + this.entryHeight - client.textRenderer.fontHeight - margin;
                context.drawText(client.textRenderer, "§o" + footer, textStartX, footerY, 0xAAAAAA, false);
            }            
        
            int buttonX = x + entryWidth - buttonWidth;
            int buttonHeight = entryHeight / 2;
        
            for (int i = 0; i < buttons.size(); i++) {
                OmniButton button = buttons.get(i);
                int buttonY = y + (i * buttonHeight);
                button.setX(buttonX);
                button.setY(buttonY);
                button.setWidth(buttonWidth);
                button.setHeight(buttonHeight);
                button.render(context, mouseX, mouseY, delta);
            }
        }        

        private List<String> wrapText(MinecraftClient client, String text, int maxWidth, int maxHeight, boolean hasButtons, int margin) {
            List<String> lines = new ArrayList<>();
            int lineHeight = client.textRenderer.fontHeight;
            int currentHeight = 0;
        
            int usableWidth = hasButtons ? maxWidth - buttonWidth - 2 * margin : maxWidth;
            StringBuilder currentLine = new StringBuilder();
        
            for (String word : text.split(" ")) {
                if (currentHeight + lineHeight > maxHeight) {
                    break;
                }
        
                while (client.textRenderer.getWidth(word) > usableWidth) {
                    int splitIndex = 0;
                    for (int i = 1; i <= word.length(); i++) {
                        if (client.textRenderer.getWidth(word.substring(0, i)) > usableWidth) {
                            break;
                        }
                        splitIndex = i;
                    }
                    lines.add(word.substring(0, splitIndex));
                    word = word.substring(splitIndex);
                    currentHeight += lineHeight;
        
                    if (currentHeight + lineHeight > maxHeight) {
                        return lines;
                    }
                }
        
                String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
                if (client.textRenderer.getWidth(testLine) > usableWidth) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                    currentHeight += lineHeight;
        
                    if (currentHeight + lineHeight > maxHeight) {
                        return lines;
                    }
                } else {
                    currentLine = new StringBuilder(testLine);
                }
            }
        
            if (!currentLine.isEmpty()) {
                lines.add(currentLine.toString());
            }
        
            return lines;
        }             

        @Override
        public List<? extends Element> children() {
            return buttons;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
            return buttons;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (OmniButton omniButton : buttons) {
                if (omniButton.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            if (button == 0 && !isButtonClicked(mouseX, mouseY)) {
                parent.clickCallback.accept(index);
                return true;
            }
            return false;
        }

        private boolean isButtonClicked(double mouseX, double mouseY) {
            for (OmniButton button : buttons) {
                if (button.isMouseOver(mouseX, mouseY)) {
                    return true;
                }
            }
            return false;
        }
    }
}