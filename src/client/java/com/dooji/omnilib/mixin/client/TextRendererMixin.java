package com.dooji.omnilib.mixin.client;

import com.dooji.omnilib.text.MarkdownParser;
import com.dooji.omnilib.text.OmniText;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TextRenderer.class)
public abstract class TextRendererMixin {

    @Inject(method = "drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I", at = @At("HEAD"), cancellable = true)
    public void onDrawText(MatrixStack matrices, Text text, float x, float y, int color, CallbackInfoReturnable<Integer> cir) {
        if (text instanceof OmniText omniText) {
            String content = omniText.getOriginalText().getString();
            int currentX = (int) x;
            int currentY = (int) y;

            for (String line : content.split("\n")) {
                renderMarkdownSegment(matrices, MinecraftClient.getInstance().textRenderer, line, currentX, currentY, color);
                currentY += 10;
                currentX = (int) x;
            }

            cir.setReturnValue(0);
        }
    }

    private void renderMarkdownSegment(MatrixStack matrices, TextRenderer textRenderer, String segment, int x, int y, int color) {
        Text processedSegment = MarkdownParser.applyMarkdown(Text.literal(segment));
        OrderedText orderedText = processedSegment.asOrderedText();
        textRenderer.draw(matrices, orderedText, x, y, color);
    }
}