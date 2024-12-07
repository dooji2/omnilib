package com.dooji.omnilib.text;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MarkdownParser {
    private static final Pattern STRIKETHROUGH_PATTERN = Pattern.compile("~~(.*?)~~");
    private static final Pattern BOLD_PATTERN = Pattern.compile("\\*\\*(.*?)\\*\\*");
    private static final Pattern ITALIC_PATTERN = Pattern.compile("_(?!_)(.*?)_(?!_)");
    private static final Pattern UNDERLINE_PATTERN = Pattern.compile("__(.*?)__");
    public static final Pattern LINK_PATTERN = Pattern.compile("\\[(.*?)\\]\\((.*?)\\)");

    public static Text applyMarkdown(Text text) {
        String content = text.getString();
        MutableText result = Text.literal("");
        Style baseStyle = text.getStyle();
        Matcher linkMatcher = LINK_PATTERN.matcher(content);
        int lastIndex = 0;

        while (linkMatcher.find()) {
            if (linkMatcher.start() > lastIndex) {
                String beforeLink = content.substring(lastIndex, linkMatcher.start());
                result.append(applyInlineMarkdown(beforeLink, baseStyle));
            }
            String displayText = linkMatcher.group(1);
            MutableText linkText = Text.literal("");
            Matcher innerMatcher = Pattern.compile("\\*\\*(.*?)\\*\\*|__(.*?)__|_(?!_)(.*?)_(?!_)|~~(.*?)~~").matcher(displayText);
            int innerLast = 0;
            while (innerMatcher.find()) {
                if (innerMatcher.start() > innerLast) {
                    String plainText = displayText.substring(innerLast, innerMatcher.start());
                    linkText.append(Text.literal(plainText).fillStyle(baseStyle));
                }
                if (innerMatcher.group(1) != null) {
                    linkText.append(Text.literal(innerMatcher.group(1)).styled(s -> s.withBold(true)));
                } else if (innerMatcher.group(2) != null) {
                    linkText.append(Text.literal(innerMatcher.group(2)).styled(s -> s.withUnderline(true)));
                } else if (innerMatcher.group(3) != null) {
                    linkText.append(Text.literal(innerMatcher.group(3)).styled(s -> s.withItalic(true)));
                } else if (innerMatcher.group(4) != null) {
                    linkText.append(Text.literal(innerMatcher.group(4)).styled(s -> s.withStrikethrough(true)));
                }
                innerLast = innerMatcher.end();
            }
            if (innerLast < displayText.length()) {
                String remaining = displayText.substring(innerLast);
                linkText.append(Text.literal(remaining).fillStyle(baseStyle));
            }
            linkText.styled(s -> s
                    .withUnderline(true)
                    .withColor(0x55AAFF));
            result.append(linkText);
            lastIndex = linkMatcher.end();
        }

        if (lastIndex < content.length()) {
            String remaining = content.substring(lastIndex);
            result.append(applyInlineMarkdown(remaining, baseStyle));
        }

        return result;
    }

    private static MutableText applyInlineMarkdown(String content, Style baseStyle) {
        MutableText result = Text.literal("");
        Matcher matcher = Pattern.compile("\\*\\*(.*?)\\*\\*|__(.*?)__|_(?!_)(.*?)_(?!_)|~~(.*?)~~").matcher(content);
        int lastIndex = 0;

        while (matcher.find()) {
            if (matcher.start() > lastIndex) {
                String plainText = content.substring(lastIndex, matcher.start());
                result.append(Text.literal(plainText).fillStyle(baseStyle));
            }
            if (matcher.group(1) != null) {
                result.append(Text.literal(matcher.group(1)).styled(s -> s.withBold(true)));
            } else if (matcher.group(2) != null) {
                result.append(Text.literal(matcher.group(2)).styled(s -> s.withUnderline(true)));
            } else if (matcher.group(3) != null) {
                result.append(Text.literal(matcher.group(3)).styled(s -> s.withItalic(true)));
            } else if (matcher.group(4) != null) {
                result.append(Text.literal(matcher.group(4)).styled(s -> s.withStrikethrough(true)));
            }
            lastIndex = matcher.end();
        }

        if (lastIndex < content.length()) {
            String remaining = content.substring(lastIndex);
            result.append(Text.literal(remaining).fillStyle(baseStyle));
        }

        return result;
    }

    public static String stripMarkdown(String text) {
        return STRIKETHROUGH_PATTERN.matcher(text).replaceAll("$1")
                .replaceAll(BOLD_PATTERN.pattern(), "$1")
                .replaceAll(UNDERLINE_PATTERN.pattern(), "$1")
                .replaceAll(ITALIC_PATTERN.pattern(), "$1")
                .replaceAll(LINK_PATTERN.pattern(), "$1");
    }
}