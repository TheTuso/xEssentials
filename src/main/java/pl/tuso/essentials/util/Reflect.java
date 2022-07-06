package pl.tuso.essentials.util;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Objects;

public class Reflect {
    public static void setField(final Field field, final Object holder, final Object object) throws NoSuchFieldException, IllegalAccessException {
        final Field theUnsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafeField.setAccessible(true);
        final Unsafe unsafe = (Unsafe) theUnsafeField.get(null);
        Objects.requireNonNull(field, "field");
        final Object ufo = holder != null ? holder : unsafe.staticFieldBase(field);
        final long offset = unsafe.staticFieldOffset(field);
        unsafe.putObject(ufo, offset, object);
    }
}