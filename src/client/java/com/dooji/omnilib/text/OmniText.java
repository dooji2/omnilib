package com.dooji.omnilib.text;

import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.TextContent;
import net.minecraft.util.Identifier;

import java.util.List;

public class OmniText implements Text {
    private final Text originalText;
    private final Identifier textureIdentifier;
    private final int textureWidth;
    private final int textureHeight;

    public OmniText(Text originalText, Identifier textureIdentifier, int textureWidth, int textureHeight) {
        this.originalText = originalText;
        this.textureIdentifier = textureIdentifier;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
    }

    @Override
    public Style getStyle() {
        return originalText.getStyle();
    }

    @Override
    public TextContent getContent() {
        return originalText.getContent();
    }

    @Override
    public List<Text> getSiblings() {
        return originalText.getSiblings();
    }

    @Override
    public String getString() {
        return MarkdownParser.stripMarkdown(originalText.getString());
    }

    @Override
    public net.minecraft.text.OrderedText asOrderedText() {
        return originalText.asOrderedText();
    }

    public Text getOriginalText() {
        return originalText;
    }

    public Identifier getTextureIdentifier() {
        return textureIdentifier;
    }

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public static OmniText of(String text, Identifier textureIdentifier, int textureWidth, int textureHeight) {
        return new OmniText(Text.literal(text), textureIdentifier, textureWidth, textureHeight);
    }

    public static OmniText of(Text text) {
        return new OmniText(text, null, 0, 0);
    }
}