package com.dooji.omnilib.ui;

import com.dooji.omnilib.text.MarkdownParser;
import com.dooji.omnilib.text.OmniText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;

public class OmniPopup {
    private final MinecraftClient client;
    private List<?> suggestions;
    private int selectedIndex = 0;
    private int offset = 0;
    private final int maxVisibleSuggestions;
    private final int color;
    private final int scrollbarColor;
    private final int scrollbarOpacity;
    private final int backgroundOpacity;
    private final int selectedTextColor;
    private final int normalTextColor;
    private final int scrollbarWidth;
    private final int popupWidth;
    private final int maxWidth;
    private final float scrollSpeed = 0.2f;
    private List<Boolean> isPaused;
    private List<Float> scrollOffsets;
    private List<Float> scrollDirections;
    private final int actionKeyCode;
    private final Consumer<Object> onAction;
    private final long pauseDuration = 1000;
    private List<Long> pauseStartTimes;
    private final int rowHeight;
    private long lastArrowKeyTime = 0;
    private final long arrowKeyDelay = 150;

    public OmniPopup(
            MinecraftClient client,
            List<?> suggestions,
            int maxVisibleSuggestions,
            int color,
            int backgroundOpacity,
            int scrollbarColor,
            int scrollbarOpacity,
            int selectedTextColor,
            int normalTextColor,
            int popupWidth,
            Integer scrollbarWidth,
            Integer actionKeyCode,
            Consumer<Object> onAction,
            int rowHeight) {
        this.client = client;
        this.suggestions = suggestions;
        this.maxVisibleSuggestions = maxVisibleSuggestions;
        this.color = color;
        this.backgroundOpacity = MathHelper.clamp(backgroundOpacity, 0, 255);
        this.scrollbarColor = scrollbarColor;
        this.scrollbarOpacity = MathHelper.clamp(scrollbarOpacity, 0, 255);
        this.selectedTextColor = selectedTextColor;
        this.normalTextColor = normalTextColor;
        this.popupWidth = popupWidth;
        this.scrollbarWidth = scrollbarWidth != null ? scrollbarWidth : 4;
        this.maxWidth = popupWidth - this.scrollbarWidth;
        this.actionKeyCode = actionKeyCode != null ? actionKeyCode : GLFW.GLFW_KEY_ENTER;
        this.onAction = onAction != null ? onAction : selected -> {};
        this.rowHeight = rowHeight;
        initializeScrollData();
    }

    private void initializeScrollData() {
        int size = suggestions.size();
        isPaused = new ArrayList<>(size);
        scrollOffsets = new ArrayList<>(size);
        scrollDirections = new ArrayList<>(size);
        pauseStartTimes = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            isPaused.add(false);
            scrollOffsets.add(0.0f);
            scrollDirections.add(1.0f);
            pauseStartTimes.add(0L);
        }
    }

    public void updateSuggestions(List<?> newSuggestions) {
        this.suggestions = newSuggestions;
        this.selectedIndex = 0;
        this.offset = 0;
        initializeScrollData();
    }

    public void render(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (suggestions.isEmpty()) return;

        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 1);

        offset = MathHelper.clamp(offset, 0, Math.max(0, suggestions.size() - maxVisibleSuggestions));
        int visibleCount = Math.min(suggestions.size(), maxVisibleSuggestions);
        int totalHeight = visibleCount * rowHeight;
        int startY = y - totalHeight;
        int backgroundColorWithOpacity = (backgroundOpacity << 24) | (color & 0x00FFFFFF);
        context.fill(x, startY, x + popupWidth - scrollbarWidth, startY + totalHeight, backgroundColorWithOpacity);
        context.enableScissor(x, startY, x + popupWidth - scrollbarWidth, startY + totalHeight);
        TextRenderer textRenderer = client.textRenderer;

        for (int i = 0; i < visibleCount; i++) {
            int suggestionIndex = offset + i;
            int suggestionY = startY + (visibleCount - 1 - i) * rowHeight;
            Object suggestion = suggestions.get(suggestionIndex);

            int textColor = (suggestionIndex == selectedIndex) ? selectedTextColor : normalTextColor;
            int rowBackgroundColor = (suggestionIndex == selectedIndex) ? 0x555555 : backgroundColorWithOpacity;
            context.fill(x, suggestionY, x + popupWidth - scrollbarWidth, suggestionY + rowHeight, rowBackgroundColor);

            int baseX = x + 5 - scrollOffsets.get(suggestionIndex).intValue();

            int centerY = suggestionY + (rowHeight / 2) - (textRenderer.fontHeight / 2);

            if (suggestion instanceof OmniText omniText) {
                renderBulletAndText(context, textRenderer, omniText, baseX, centerY, textColor, suggestionIndex, textRenderer.fontHeight);
            } else if (suggestion instanceof String string) {
                renderText(context, textRenderer, string, baseX, centerY, textColor);
            } else if (suggestion instanceof Text txt) {
                renderText(context, textRenderer, txt.getString(), baseX, centerY, textColor);
            }
        }

        context.disableScissor();

        if (suggestions.size() > maxVisibleSuggestions) {
            int scrollbarX = x + popupWidth - scrollbarWidth;
            float maxOffset = suggestions.size() - maxVisibleSuggestions;
            float scrollPercent = maxOffset == 0 ? 0 : (float) offset / maxOffset;
            int scrollbarHeight = Math.max((int) (maxVisibleSuggestions / (float) suggestions.size() * totalHeight), 10);
            int scrollbarY = startY + (int) ((1 - scrollPercent) * (totalHeight - scrollbarHeight));
            int scrollbarBackground = (scrollbarOpacity << 24) | 0x000000;
            int scrollbarForeground = (scrollbarOpacity << 24) | scrollbarColor;
            context.fill(scrollbarX, startY, scrollbarX + scrollbarWidth, startY + totalHeight, scrollbarBackground);
            context.fill(scrollbarX, scrollbarY, scrollbarX + scrollbarWidth, scrollbarY + scrollbarHeight, scrollbarForeground);
        }

        context.getMatrices().pop();
    }

    private void renderBulletAndText(DrawContext context, TextRenderer textRenderer, OmniText omniText, int baseX, int y, int color, int index, int fontHeight) {
        String original = omniText.getOriginalText().getString();
        boolean dashed = original.startsWith("-");

        int iconWidth = 0;
        int iconHeight = 0;
        int lineWidth = 0;

        if (dashed) {
            lineWidth += 5 + 10;
            original = original.substring(1).trim();
        }

        if (omniText.getTextureIdentifier() != null) {
            iconWidth = omniText.getTextureWidth();
            iconHeight = omniText.getTextureHeight();
            lineWidth += iconWidth + 5;
        }

        Text processed = MarkdownParser.applyMarkdown(Text.literal(original));
        int textWidth = textRenderer.getWidth(processed.asOrderedText());
        lineWidth += textWidth;

        int visibleWidth = maxWidth - scrollbarWidth - 5;

        int currentX = baseX;

        if (omniText.getTextureIdentifier() != null) {
            Identifier iconId = omniText.getTextureIdentifier();
            int iconY = y + (fontHeight - iconHeight) / 2;
            context.drawTexture(iconId, currentX, iconY, iconWidth, iconHeight, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
            currentX += iconWidth + 5;
        }

        if (dashed) {
            int bulletSize = 5;
            int bulletY = y + (fontHeight - bulletSize) / 2;
            context.fill(currentX, bulletY, currentX + bulletSize, bulletY + bulletSize, color);
            currentX += bulletSize + 10;
        }

        context.drawText(textRenderer, processed.asOrderedText(), currentX, y, color, false);

        if (lineWidth > visibleWidth) {
            if (!isPaused.get(index)) {
                scrollOffsets.set(index, scrollOffsets.get(index) + scrollSpeed * scrollDirections.get(index));
                if (scrollOffsets.get(index) >= (lineWidth - visibleWidth)) {
                    scrollOffsets.set(index, (float) (lineWidth - visibleWidth));
                    scrollDirections.set(index, -1.0f);
                    isPaused.set(index, true);
                    pauseStartTimes.set(index, System.currentTimeMillis());
                } else if (scrollOffsets.get(index) <= 0) {
                    scrollOffsets.set(index, 0f);
                    scrollDirections.set(index, 1.0f);
                    isPaused.set(index, true);
                    pauseStartTimes.set(index, System.currentTimeMillis());
                }
            } else {
                if (System.currentTimeMillis() - pauseStartTimes.get(index) > pauseDuration) {
                    isPaused.set(index, false);
                }
            }
        } else {
            scrollOffsets.set(index, 0f);
        }
    }

    private void renderText(DrawContext context, TextRenderer textRenderer, String text, int x, int y, int color) {
        context.drawText(textRenderer, Text.literal(text), x, y, color, false);
    }

    public void handleInput(int keyCode) {
        if (suggestions.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastArrowKeyTime < arrowKeyDelay) {
            return;
        }
        lastArrowKeyTime = currentTime;

        if (keyCode == GLFW.GLFW_KEY_UP) scroll(-1);
        if (keyCode == GLFW.GLFW_KEY_DOWN) scroll(1);
        if (keyCode == actionKeyCode) triggerAction();
    }

    private void triggerAction() {
        if (onAction != null && selectedIndex >= 0 && selectedIndex < suggestions.size()) {
            onAction.accept(suggestions.get(selectedIndex));
        }
    }

    private void scroll(int direction) {
        selectedIndex = MathHelper.clamp(selectedIndex - direction, 0, suggestions.size() - 1);
        if (direction > 0 && selectedIndex < offset) {
            offset = selectedIndex;
        }
        if (direction < 0 && selectedIndex >= offset + maxVisibleSuggestions) {
            offset = selectedIndex - maxVisibleSuggestions + 1;
        }
        offset = MathHelper.clamp(offset, 0, Math.max(0, suggestions.size() - maxVisibleSuggestions));
    }

    public void handleScroll(double amount) {
        if (suggestions.isEmpty()) {
            return;
        }
        offset = MathHelper.clamp(offset + (int) amount, 0, Math.max(0, suggestions.size() - maxVisibleSuggestions));
    }

    public void handleMouseClick(double mouseX, double mouseY, int popupX, int popupY, int visibleCount, int totalHeight) {
        int startY = popupY - totalHeight;

        for (int i = 0; i < visibleCount; i++) {
            int rowY = startY + (visibleCount - 1 - i) * rowHeight;

            if (mouseX >= popupX && mouseX <= popupX + popupWidth - scrollbarWidth &&
                    mouseY >= rowY && mouseY <= rowY + rowHeight) {
                selectedIndex = offset + i;
                Object suggestion = suggestions.get(selectedIndex);

                if (suggestion instanceof OmniText omniText) {
                    if (handleLinkClick(omniText, mouseX, popupX, rowY)) {
                        return;
                    }
                }

                triggerAction();
                break;
            }
        }
    }

    private boolean handleLinkClick(OmniText omniText, double mouseX, int popupX, int rowY) {
        String original = omniText.getOriginalText().getString();
        boolean hasDash = original.startsWith("-");
        boolean hasIcon = omniText.getTextureIdentifier() != null;

        int iconWidth = hasIcon ? omniText.getTextureWidth() : 0;

        if (hasDash) {
            original = original.substring(1).trim();
        }

        Text processed = MarkdownParser.applyMarkdown(Text.literal(original));
        String processedString = processed.getString();

        double currentX = popupX + 5 - scrollOffsets.get(selectedIndex);

        if (hasIcon) {
            currentX += iconWidth + 15;
        }
        if (hasDash) {
            currentX += 5 + 10;
        }

        for (String segment : processedString.split("(?=\\s)|(?<=\\s)")) { 
            int segmentWidth = client.textRenderer.getWidth(segment);
        
            if (mouseX >= currentX && mouseX <= currentX + segmentWidth) {
                if (!segment.trim().isEmpty()) {
                    Matcher linkMatcher = MarkdownParser.LINK_PATTERN.matcher(original);
                    while (linkMatcher.find()) {
                        String displayText = linkMatcher.group(1);
                        String url = linkMatcher.group(2);
        
                        if (segment.trim().equals(displayText)) {
                            Style linkStyle = Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
        
                            if (client.currentScreen instanceof Screen) {
                                Screen screen = (Screen) client.currentScreen;
                                if (screen.handleTextClick(linkStyle)) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        
            currentX += segmentWidth;
        }        

        return false;
    }
}