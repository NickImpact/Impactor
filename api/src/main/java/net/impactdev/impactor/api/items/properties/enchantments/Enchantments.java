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

package net.impactdev.impactor.api.items.properties.enchantments;

import net.kyori.adventure.key.Key;

public class Enchantments {

    // All Purpose
    public static final Key MENDING = Enchantments.minecraft("mending");
    public static final Key UNBREAKING = Enchantments.minecraft("unbreaking");
    public static final Key CURSE_OF_VANISHING = Enchantments.minecraft("vanishing_curse");

    // Armor
    public static final Key AQUA_AFFINITY = Enchantments.minecraft("aqua_affinity");
    public static final Key BLAST_PROTECTION = Enchantments.minecraft("blast_protection");
    public static final Key CURSE_OF_BINDING = Enchantments.minecraft("binding_curse");
    public static final Key DEPTH_STRIDER = Enchantments.minecraft("depth_strider");
    public static final Key FEATHER_FALLING = Enchantments.minecraft("feather_falling");
    public static final Key FIRE_PROTECTION = Enchantments.minecraft("fire_protection");
    public static final Key FROST_WALKER = Enchantments.minecraft("frost_walker");
    public static final Key PROJECTILE_PROTECTION = Enchantments.minecraft("projectile_protection");
    public static final Key PROTECTION = Enchantments.minecraft("protection");
    public static final Key RESPIRATION = Enchantments.minecraft("respiration");
    public static final Key SOUL_SPEED = Enchantments.minecraft("soul_speed");
    public static final Key THORNS = Enchantments.minecraft("thorns");

    // Melee Weapons
    public static final Key BANE_OF_ARTHROPODS = Enchantments.minecraft("bane_of_arthropods");
    public static final Key EFFICIENCY = Enchantments.minecraft("efficiency");
    public static final Key FIRE_ASPECT = Enchantments.minecraft("fire_aspect");
    public static final Key LOOTING = Enchantments.minecraft("looting");
    public static final Key IMPALING = Enchantments.minecraft("impaling");
    public static final Key KNOCKBACK = Enchantments.minecraft("knockback");
    public static final Key SHARPNESS = Enchantments.minecraft("sharpness");
    public static final Key SMITE = Enchantments.minecraft("smite");
    public static final Key SWEEPING_EDGE = Enchantments.minecraft("sweeping_edge");

    // Ranged Weapons
    public static final Key CHANNELING = Enchantments.minecraft("channeling");
    public static final Key FLAME = Enchantments.minecraft("flame");
    public static final Key INFINITY = Enchantments.minecraft("infinity");
    public static final Key LOYALTY = Enchantments.minecraft("loyalty");
    public static final Key RIPTIDE = Enchantments.minecraft("riptide");
    public static final Key MULTISHOT = Enchantments.minecraft("multishot");
    public static final Key PIERCING = Enchantments.minecraft("piercing");
    public static final Key POWER = Enchantments.minecraft("power");
    public static final Key PUNCH = Enchantments.minecraft("punch");
    public static final Key QUICK_CHARGE = Enchantments.minecraft("quick_charge");

    // Tools
    public static final Key FORTUNE = Enchantments.minecraft("fortune");
    public static final Key LUCK_OF_THE_SEA = Enchantments.minecraft("luck_of_the_sea");
    public static final Key LURE = Enchantments.minecraft("lure");
    public static final Key SILK_TOUCH = Enchantments.minecraft("silk_touch");

    @SuppressWarnings("PatternValidation")
    private static Key minecraft(final String id) {
        return Key.key(Key.MINECRAFT_NAMESPACE, id);
    }

}
