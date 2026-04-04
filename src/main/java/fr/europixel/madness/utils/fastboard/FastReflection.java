/*
 * This file is part of FastBoard, licensed under the MIT License.
 *
 * Copyright (c) 2019-2023 MrMicky
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package fr.europixel.madness.utils.fastboard;

import org.bukkit.Bukkit;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Small reflection utility class to use CraftBukkit and NMS.
 *
 * @author MrMicky
 */
public final class FastReflection {

    private static final String NM_PACKAGE = "net.minecraft";
    public static final String OBC_PACKAGE = "org.bukkit.craftbukkit";
    public static final String NMS_PACKAGE = NM_PACKAGE + ".server";

    public static final String VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(OBC_PACKAGE.length() + 1);

    private static final MethodType VOID_METHOD_TYPE = MethodType.methodType(void.class);
    private static final boolean NMS_REPACKAGED = optionalClass(NM_PACKAGE + ".network.protocol.Packet").isPresent();

    private static volatile Object theUnsafe;

    private FastReflection() {
        throw new UnsupportedOperationException();
    }

    public static boolean isRepackaged() {
        return NMS_REPACKAGED;
    }

    public static String nmsClassName(final String post1_17package, final String className) {
        if (NMS_REPACKAGED) {
            final String classPackage = post1_17package == null ? NM_PACKAGE : NM_PACKAGE + '.' + post1_17package;
            return classPackage + '.' + className;
        }
        return NMS_PACKAGE + '.' + VERSION + '.' + className;
    }

    public static Class<?> nmsClass(final String post1_17package, final String className) throws ClassNotFoundException {
        return Class.forName(nmsClassName(post1_17package, className));
    }

    public static Optional<Class<?>> nmsOptionalClass(final String post1_17package, final String className) {
        return optionalClass(nmsClassName(post1_17package, className));
    }

    public static String obcClassName(final String className) {
        return OBC_PACKAGE + '.' + VERSION + '.' + className;
    }

    public static Class<?> obcClass(final String className) throws ClassNotFoundException {
        return Class.forName(obcClassName(className));
    }

    public static Optional<Class<?>> obcOptionalClass(final String className) {
        return optionalClass(obcClassName(className));
    }

    public static Optional<Class<?>> optionalClass(final String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            return Optional.empty();
        }
    }

    public static Object enumValueOf(final Class<?> enumClass, final String enumName) {
        return Enum.valueOf(enumClass.asSubclass(Enum.class), enumName);
    }

    public static Object enumValueOf(final Class<?> enumClass, final String enumName, int fallbackOrdinal) {
        try {
            return enumValueOf(enumClass, enumName);
        } catch (IllegalArgumentException e) {
            final Object[] constants = enumClass.getEnumConstants();
            if (constants.length > fallbackOrdinal) return constants[fallbackOrdinal];
            throw e;
        }
    }

    static Class<?> innerClass(final Class<?> parentClass, final Predicate<Class<?>> classPredicate) throws ClassNotFoundException {
        for (Class<?> innerClass : parentClass.getDeclaredClasses()) {
            if (classPredicate.test(innerClass)) {
                return innerClass;
            }
        }
        throw new ClassNotFoundException("No class in " + parentClass.getCanonicalName() + " matches the predicate.");
    }

    public static PacketConstructor findPacketConstructor(final Class<?> packetClass, final MethodHandles.Lookup lookup) throws Exception {
        try {
            final MethodHandle constructor = lookup.findConstructor(packetClass, VOID_METHOD_TYPE);
            return constructor::invoke;
        } catch (NoSuchMethodException | IllegalAccessException e) {
            // try below with Unsafe
        }

        if (theUnsafe == null) {
            synchronized (FastReflection.class) {
                if (theUnsafe == null) {
                    final Class<?> unsafeClass = Class.forName("sun.misc.Unsafe");
                    final Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
                    theUnsafeField.setAccessible(true);
                    theUnsafe = theUnsafeField.get(null);
                }
            }
        }

        final MethodType allocateMethodType = MethodType.methodType(Object.class, Class.class);
        final MethodHandle allocateMethod = lookup.findVirtual(theUnsafe.getClass(), "allocateInstance", allocateMethodType);
        return () -> allocateMethod.invoke(theUnsafe, packetClass);
    }

    @FunctionalInterface
    interface PacketConstructor {
        Object invoke() throws Throwable;
    }
}
