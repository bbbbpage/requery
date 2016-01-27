/*
 * Copyright 2016 requery.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.requery.meta;

import io.requery.util.function.Supplier;

import java.util.Collections;

final class ImmutableType<T> extends BaseType<T> {

    public ImmutableType(TypeBuilder<T> builder) {
        this.type = builder.classType();
        this.baseType = builder.baseType();
        this.name = builder.name();
        this.attributes = Collections.unmodifiableSet(builder.attributes());
        this.cacheable = builder.isCacheable();
        this.readOnly = builder.isReadOnly();
        this.factory = builder.factory();
        this.proxyProvider = builder.proxyProvider();
        this.tableCreateAttributes = builder.tableCreateAttributes();

        for (Attribute<T, ?> attribute : attributes) {
            if (attribute instanceof BaseAttribute) {
                BaseAttribute baseAttribute = (BaseAttribute) attribute;
                baseAttribute.declaringType = this;
            }
        }
        if (factory == null) {
            // factory will be set by the processor this is a fallback
            factory = new Supplier<T>() {
                @Override
                public T get() {
                    try {
                        return classType().newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
    }
}
