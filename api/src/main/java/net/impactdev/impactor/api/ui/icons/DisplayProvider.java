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

package net.impactdev.impactor.api.ui.icons;

/**
 * An IconProvider is simply a layer for providing an Icon to a UI. The purpose for this
 * functional interface is simply to provide a means for updating an icon on a refresh
 * call. As icon content might change over time, we want to provide a means for updating
 * the display of the icon, whilst also maintaining the same characteristics of the icon.
 *
 * <p>Functionally, this is the same as a {@link java.util.function.Supplier}, but this
 * interface is defined as such as a means of clarity towards its intended purpose.
 *
 * <br><br>
 * <h2>Constant Displays</h2>
 * If designing an item that is expected to not change its display overtime, then
 * consider using the subclass {@link Constant}. This interface design is meant
 * to base on the original stack throughout an Icon's lifetime, and any calls to
 * provide will return the display item as it was created. In practice, provide
 * on this interface will only be fired once, as the view implementations should
 * skip over this type during a refresh call.
 *
 * @param <T> The platform specific type for an ItemStack
 */
@FunctionalInterface
public interface DisplayProvider<T> {

    /**
     * Creates a displayable ItemStack. If an icon changes overtime, this method should consider
     * how to draft the item in a manner that allows it to be updated while maintaining
     * the desired characteristics.
     *
     * <p>For example, consider an item that features a timer within its lore. Instead
     * of showing a static time generated from the moment the player opened the view, this
     * icon should count down to 0 and display such information to the viewer. During this
     * time, the icon's listeners remain functionally the same. With this in mind, the following
     * code would be of use:
     * <pre>
     * Icon&lt;ItemStack&gt; provider = Icon.builder(ItemStack.class)
     *          .display(() -&gt; {
     *              // Create an ItemStack relative to provide request
     *              ItemStack myItem = ...
     *              return myItem;
     *          })
     *          .listener(context -&gt; System.out.println("I was clicked!"))
     *          .build();
     * }
     * </pre>
     * The item generated at the call to provide here will provide a new Icon, which features
     * an ItemStack that has a lore entry with a time relative to the time provide was issued,
     * and with a click listener that will print to the console when acted on.
     *
     * @return An icon for the particular display
     */
    T provide();

    /**
     * An implementation of the {@link DisplayProvider} which features an icon that is expected to never
     * change. From the point of creation, the icon configured will remain constant, and never
     * feature a change of icon. This provider type is primarily useful for icons where a page
     * is expected to refresh, and you wish for an icon to not actually be refreshed at all.
     *
     * @param <T> The platform specific type for an ItemStack
     */
    class Constant<T> implements DisplayProvider<T> {

        private final T display;

        public Constant(T display) {
            this.display = display;
        }

        @Override
        public T provide() {
            return this.display;
        }

    }

}
