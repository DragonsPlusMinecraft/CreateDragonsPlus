/*
 * Copyright (C) 2025  DragonsPlus
 * SPDX-License-Identifier: LGPL-3.0-or-later
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package plus.dragons.createdragonsplus.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the annotated code is referenced from other open-source software.
 * <p>
 * When applied on package, indicates that all class under the package
 * is referenced from another package defined in {@link #targets}.
 * <p>
 * When applied on class, indicates that the annotated class
 * is referenced from other classes defined in {@link #value} and {@link #targets}.
 * <p>
 * When applied on method, indicates that the annotated method
 * is referenced from other methods from the classes defined in {@link #value}
 * with their names defined in {@link #targets}.
 */
@Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.CLASS)
@Repeatable(CodeReference.List.class)
public @interface CodeReference {
    /**
     * For class and method reference, define the referenced classes.
     * <p>
     * Use {@link #targets} if the referenced class is not available at compile.
     * 
     * @return the referenced classes
     */
    Class<?>[] value() default {};

    /**
     * For package reference, defines the package name of the referenced package.
     * <p>
     * For class reference, defines the full name of the referenced classes.
     * <p>
     * For method reference, defines the name of the referenced method,
     * full name should be used if {@link #value} is not available.
     * 
     * @return the name of the referenced targets
     */
    String[] targets() default {};

    /**
     * Defines the referenced open-source software,
     * could be a simple name of the software, or a link to the source.
     * 
     * @return the referenced open-source software
     */
    String[] source();

    /**
     * Defines the open-source license of the open-source software,
     * could be a spdx license identifier, or a link to the license file.
     * 
     * @return the open-source license of the open-source software.
     */
    String[] license();

    /**
     * Repeatable container of {@link CodeReference}.
     * <p>
     * Programmers generally do not need to write this. It is created by Java when a programmer
     * writes more than one {@link CodeReference} annotation at the same location.
     */
    @Target({ ElementType.PACKAGE, ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.CLASS)
    @interface List {
        CodeReference[] value();
    }
}
