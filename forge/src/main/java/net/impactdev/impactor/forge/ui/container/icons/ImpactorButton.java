/*
 * This file is part of Impactor, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2018-2022 NickImpact
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package net.impactdev.impactor.forge.ui.container.icons;

import ca.landonjw.gooeylibs2.api.adventure.ForgeTranslator;
import ca.landonjw.gooeylibs2.api.button.ButtonAction;
import ca.landonjw.gooeylibs2.api.button.ButtonBase;
import net.impactdev.impactor.api.ui.icons.ClickContext;
import net.impactdev.impactor.api.ui.icons.ClickProcessor;
import net.impactdev.impactor.api.utilities.Builder;
import net.impactdev.impactor.api.utilities.ComponentManipulator;
import net.impactdev.impactor.api.utilities.printing.PrettyPrinter;
import net.impactdev.impactor.forge.ForgeImpactorPlugin;
import net.impactdev.impactor.forge.adventure.RelocationTranslator;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.util.MessageSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public class ImpactorButton extends ButtonBase {

    private final Set<ClickProcessor> processors;

    private ImpactorButton(ImpactorButtonBuilder builder) {
        super(builder.display);
        this.processors = builder.processors;
    }

    public Set<ClickProcessor> getListeners() {
        return this.processors;
    }

    @Override
    public void onClick(@NotNull ButtonAction action) {
        try {
            ClickContext context = ClickContext.create().append(ButtonAction.class, action);
            this.processors.forEach(processor -> processor.process(context));
        } catch (Exception e) {
            PrettyPrinter printer = new PrettyPrinter(80);
            printer.title("Exception during Button Click");
            printer.add("Click Action Details:");
            printer.kv("Player", action.getPlayer().getName());
            printer.kv("Click Type", action.getClickType().name());
            printer.kv("Slot", action.getSlot());
            printer.kv("Inventory Title", ComponentManipulator.flatten(RelocationTranslator.nonRelocated(
                    ForgeTranslator.asAdventure(action.getPage().getTitle())
            )));

            printer.newline();
            printer.add("The tracked exception is detailed below:");
            printer.add(e);
            printer.log(ForgeImpactorPlugin.getInstance().getPluginLogger(), "UI");
        }
    }

    public static ImpactorButtonBuilder builder() {
        return new ImpactorButtonBuilder();
    }

    public static class ImpactorButtonBuilder implements Builder<ImpactorButton, ImpactorButtonBuilder> {

        private ItemStack display;
        private final Set<ClickProcessor> processors = new LinkedHashSet<>();

        public ImpactorButtonBuilder display(ItemStack display) {
            this.display = display;
            return this;
        }

        public ImpactorButtonBuilder processor(ClickProcessor processor) {
            this.processors.add(processor);
            return this;
        }

        public ImpactorButtonBuilder processors(Collection<ClickProcessor> processors) {
            this.processors.addAll(processors);
            return this;
        }

        @Override
        public ImpactorButtonBuilder from(ImpactorButton input) {
            return this;
        }

        @Override
        public ImpactorButton build() {
            return new ImpactorButton(this);
        }
    }

}
