package com.dooji.omnilib.ui;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class OmniField extends ClickableWidget {
    private final TextRenderer textRenderer;
    private String text = "";
    private int maxLength;
    private Consumer<String> changedListener;
    private boolean focused;
    private int cursorPosition = 0;
    private int scrollOffset = 0;
    private int selectionStart = -1;
    private long lastBlinkTime;
    private boolean cursorVisible;

    private final int cursorColor;
    private final int backgroundColor;
    private final int hoveredColor;
    private final Identifier cursorTexture;
    private final Identifier backgroundTexture;
    private final Identifier hoveredTexture;

    public OmniField(
            TextRenderer textRenderer, 
            int x, 
            int y, 
            int width, 
            int height, 
            Text message, 
            Integer maxLength, 
            Integer cursorColor, 
            Integer backgroundColor, 
            Integer hoveredColor, 
            Identifier cursorTexture, 
            Identifier backgroundTexture, 
            Identifier hoveredTexture) {
        super(x, y, width, height, message);
        this.textRenderer = textRenderer;
        this.maxLength = maxLength != null ? maxLength : 100;
        this.cursorColor = cursorColor != null ? cursorColor : 0xFFFFFFFF;
        this.backgroundColor = backgroundColor != null ? backgroundColor : 0x88000000;
        this.hoveredColor = hoveredColor != null ? hoveredColor : 0x99000000;
        this.cursorTexture = cursorTexture;
        this.backgroundTexture = backgroundTexture;
        this.hoveredTexture = hoveredTexture;
    }

    public OmniField(TextRenderer textRenderer, int x, int y, int width, int height, Text message) {
        this(textRenderer, x, y, width, height, message, null, null, null, null, null, null, null);
    }

    public void setText(String text) {
        if (text.length() > this.maxLength) text = text.substring(0, this.maxLength);
        this.text = text;
        cursorPosition = Math.min(cursorPosition, this.text.length());
        adjustScroll();
    }

    public String getText() {
        return this.text;
    }

    public void setChangedListener(Consumer<String> listener) {
        this.changedListener = listener;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.active && this.visible && isMouseOver(mouseX, mouseY)) {
            this.setFocused(true);
            int relativeX = (int) (mouseX - this.getX() - 2);
            this.cursorPosition = this.textRenderer.trimToWidth(this.text.substring(scrollOffset), relativeX).length() + scrollOffset;
            adjustScroll();
            clearSelection();
            return true;
        }
        return false;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (this.isFocused() && this.active && this.visible) {
            if (Character.isDefined(chr) && !Character.isISOControl(chr) && this.text.length() < this.maxLength) {
                deleteSelection();
                this.text = this.text.substring(0, cursorPosition) + chr + this.text.substring(cursorPosition);
                cursorPosition++;
                adjustScroll();
                clearSelection();
                notifyChange();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.isFocused()) {
            boolean ctrl = (modifiers & 2) != 0;
            boolean shift = (modifiers & 1) != 0;

            switch (keyCode) {
                case 263:
                    if (cursorPosition > 0) {
                        if (shift) {
                            setSelection(cursorPosition - 1);
                        } else {
                            cursorPosition--;
                            clearSelection();
                        }
                        adjustScroll();
                    }
                    return true;
                case 262:
                    if (cursorPosition < this.text.length()) {
                        if (shift) {
                            setSelection(cursorPosition + 1);
                        } else {
                            cursorPosition++;
                            clearSelection();
                        }
                        adjustScroll();
                    }
                    return true;
                case 259:
                    if (selectionStart != -1) {
                        deleteSelection();
                    } else if (cursorPosition > 0) {
                        this.text = this.text.substring(0, cursorPosition - 1) + this.text.substring(cursorPosition);
                        cursorPosition--;
                        adjustScroll();
                    }
                    notifyChange();
                    return true;
                case 261:
                    if (selectionStart != -1) {
                        deleteSelection();
                    } else if (cursorPosition < this.text.length()) {
                        this.text = this.text.substring(0, cursorPosition) + this.text.substring(cursorPosition + 1);
                        adjustScroll();
                    }
                    notifyChange();
                    return true;
                case 65:
                    if (ctrl) {
                        selectAll();
                        adjustScroll();
                    }
                    return true;
                case 67:
                    if (ctrl) {
                        MinecraftClient.getInstance().keyboard.setClipboard(getSelectedText());
                    }
                    return true;
                case 88:
                    if (ctrl) {
                        MinecraftClient.getInstance().keyboard.setClipboard(getSelectedText());
                        deleteSelection();
                    }
                    return true;
                case 86:
                    if (ctrl) {
                        String clipboardText = MinecraftClient.getInstance().keyboard.getClipboard();
                        if (clipboardText != null && !clipboardText.isEmpty()) {
                            deleteSelection();
                            this.text = this.text.substring(0, cursorPosition) + clipboardText + this.text.substring(cursorPosition);
                            cursorPosition += clipboardText.length();
                            adjustScroll();
                            notifyChange();
                        }
                    }
                    return true;
                case 256:
                    this.setFocused(false);
                    return true;
            }
        }
        return false;
    }

    private void adjustScroll() {
        cursorPosition = Math.max(0, Math.min(cursorPosition, this.text.length()));
        scrollOffset = Math.max(0, Math.min(scrollOffset, this.text.length()));

        if (scrollOffset > cursorPosition) {
            scrollOffset = cursorPosition;
        }

        int visibleWidth = this.width - 4;
        int cursorPixelPos = this.textRenderer.getWidth(this.text.substring(scrollOffset, cursorPosition));

        while (cursorPixelPos > visibleWidth && scrollOffset < cursorPosition) {
            scrollOffset++;
            cursorPixelPos = this.textRenderer.getWidth(this.text.substring(scrollOffset, cursorPosition));
        }

        while (cursorPixelPos < 0 && scrollOffset > 0) {
            scrollOffset--;
            cursorPixelPos = this.textRenderer.getWidth(this.text.substring(scrollOffset, cursorPosition));
        }

        scrollOffset = Math.max(0, Math.min(scrollOffset, this.text.length()));
        if (cursorPosition < scrollOffset) {
            cursorPosition = scrollOffset;
        }
    }

    private void setSelection(int newCursorPosition) {
        if (selectionStart == -1) selectionStart = cursorPosition;
        cursorPosition = Math.max(0, Math.min(newCursorPosition, this.text.length()));
    }

    private void deleteSelection() {
        if (selectionStart == -1) return;
        int start = Math.min(cursorPosition, selectionStart);
        int end = Math.max(cursorPosition, selectionStart);
        this.text = this.text.substring(0, start) + this.text.substring(end);
        cursorPosition = start;
        clearSelection();
        adjustScroll();
    }

    private void clearSelection() {
        selectionStart = -1;
    }

    private void selectAll() {
        selectionStart = 0;
        cursorPosition = this.text.length();
    }

    private String getSelectedText() {
        if (selectionStart == -1) return "";
        int start = Math.min(cursorPosition, selectionStart);
        int end = Math.max(cursorPosition, selectionStart);
        return this.text.substring(start, end);
    }

    private void notifyChange() {
        if (this.changedListener != null) {
            this.changedListener.accept(this.text);
        }
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier currentBackground = isHovered() && hoveredTexture != null ? hoveredTexture : backgroundTexture;
    
        if (currentBackground != null) {
            context.drawTexture(currentBackground, this.getX(), this.getY(), 0, 0, this.width, this.height, this.width, this.height);
        } else {
            int bgColor = isHovered() ? hoveredColor : backgroundColor;
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bgColor);
        }
    
        String visibleText = this.textRenderer.trimToWidth(this.text.substring(scrollOffset), this.width - 8);
        int textX = this.getX() + 4;
        int textY = this.getY() + (this.height - this.textRenderer.fontHeight) / 2;
    
        if (this.text.isEmpty() && !this.isFocused() && this.getMessage() != null) {
            Text placeholder = this.getMessage().copy().styled(style -> style.withItalic(true));
            context.drawText(this.textRenderer, placeholder, textX, textY, 0x808080, false);
        } else {
            if (selectionStart != -1 && selectionStart != cursorPosition) {
                int start = Math.min(selectionStart, cursorPosition);
                int end = Math.max(selectionStart, cursorPosition);
    
                start = Math.max(start, scrollOffset);
                end = Math.min(end, this.text.length());
    
                if (start < end) {
                    int selectionStartX = textX + this.textRenderer.getWidth(this.text.substring(scrollOffset, start));
                    int selectionEndX = textX + this.textRenderer.getWidth(this.text.substring(scrollOffset, end));
    
                    selectionStartX = Math.max(selectionStartX, textX);
                    selectionEndX = Math.min(selectionEndX, textX + this.width - 4);
    
                    if (selectionStartX < selectionEndX) {
                        context.fill(selectionStartX, textY, selectionEndX, textY + this.textRenderer.fontHeight, 0x80FFFFFF);
                    }
                }
            }
    
            context.drawText(this.textRenderer, visibleText, textX, textY, 0xFFFFFF, false);
        }
    
        if (this.isFocused()) {
            long time = System.currentTimeMillis();
            if (time - lastBlinkTime > 500) {
                cursorVisible = !cursorVisible;
                lastBlinkTime = time;
            }
    
            if (cursorVisible) {
                int cursorX = Math.min(textX + this.textRenderer.getWidth(this.text.substring(scrollOffset, cursorPosition)), this.getX() + this.width - 4);

                if (cursorTexture != null) {
                    context.drawTexture(cursorTexture, cursorX, textY, 0, 0, 1, this.textRenderer.fontHeight, 1, this.textRenderer.fontHeight);
                } else {
                    context.fill(cursorX, textY, cursorX + 1, textY + this.textRenderer.fontHeight, cursorColor);
                }
            }
        }
    }    

    @Override
    public boolean isFocused() {
        return this.focused;
    }

    @Override
    public void setFocused(boolean focused) {
        this.focused = focused;
        this.cursorVisible = true;
        this.lastBlinkTime = System.currentTimeMillis();
    }

    @Override
    protected void appendClickableNarrations(net.minecraft.client.gui.screen.narration.NarrationMessageBuilder builder) {
    }
}