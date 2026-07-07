/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 */

package plus.dragons.createdragonsplus.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.meta.TypeQualifierDefault;
import org.jetbrains.annotations.UnknownNullability;

@UnknownNullability
@TypeQualifierDefault(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS)
public @interface FieldsNullabilityUnknownByDefault {}
