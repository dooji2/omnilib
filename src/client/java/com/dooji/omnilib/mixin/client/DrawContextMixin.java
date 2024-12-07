package com.dooji.omnilib.mixin.client;

import com.dooji.omnilib.text.MarkdownParser;
import com.dooji.omnilib.text.OmniText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DrawContext.class)
public abstract class DrawContextMixin {

    @Inject(method = "drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)I", at = @At("HEAD"), cancellable = true)
    private void onDrawText(TextRenderer textRenderer, Text text, int x, int y, int color, boolean shadow, CallbackInfoReturnable<Integer> cir) {
        if (text instanceof OmniText omniText) {
            String content = omniText.getOriginalText().getString();
            int currentX = x;
            int currentY = y;

            for (String line : content.split("\n")) {
                renderMarkdownSegment((DrawContext) (Object) this, textRenderer, line, currentX, currentY, color, shadow);
                currentY += 10;
                currentX = x;
            }

            cir.setReturnValue(0);
        }
    }

    private void renderMarkdownSegment(DrawContext context, TextRenderer textRenderer, String segment, int x, int y, int color, boolean shadow) {
        Text processedSegment = MarkdownParser.applyMarkdown(Text.literal(segment));
        OrderedText orderedText = processedSegment.asOrderedText();
        context.drawText(textRenderer, orderedText, x, y, color, shadow);
    }
}