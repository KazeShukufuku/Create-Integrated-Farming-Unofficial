/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createdragonsplus.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.CLASS)
@Repeatable(CodeReference.List.class)
public @interface CodeReference {
    Class<?>[] value() default {};

    String[] targets() default {};

    String[] source();

    String[] license();

    @Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.CLASS)
    @interface List {
        CodeReference[] value();
    }
}
