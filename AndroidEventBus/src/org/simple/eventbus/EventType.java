/*
 * Copyright (C) 2015 Mr.Simple <bboyfeiyu@gmail.com>
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

package org.simple.eventbus;

import android.text.TextUtils;

/**
 * <p/>
 * 该类是描述一个函数唯一性的对象，参数类型、tag两个条件保证了对象的唯一性.通过该类的对象来查找注册了相应类型和tag的所有订阅者{@see
 * Subscription}, 并且在接到消息时调用所有订阅者对应的函数.
 *
 * @author mrsimple
 */
public final class EventType {
    public static final String DEFAULT_TAG = "def";
    /**
     * 参数类型
     */
//    Class<?> paramClass;
    Class<?>[] paramClass;
    /**
     * 函数的tag
     */
    public String tag = DEFAULT_TAG;

    /**
     * @param aClass
     */
    public EventType(Class<?>[] aClass) {
        this(aClass, DEFAULT_TAG);
    }

    public EventType(Class<?> aClass[], String aTag) {
        paramClass = aClass;
        tag = aTag;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paramClass.length; i++) {
            sb.append(paramClass[i].getName());
            sb.append(",");
        }
        return "EventType tag=" + tag + ",pa:" + sb;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((paramClass == null) ? 0 : paramClass.hashCode());
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof EventType))
            return false;
        EventType other = (EventType) obj;

        if (!TextUtils.equals(tag, other.tag)) {//tag不等
            return false;
        }

        int paLen1 = paramClass == null ? 0 : paramClass.length;
        int paLen2 = other.paramClass == null ? 0 : other.paramClass.length;
        if (paLen1 != paLen2) {
            return false;
        }

        boolean equals = true;
        for (int i = 0; i < paramClass.length; i++) {
            if (paramClass[i] == null || other.paramClass[i] == null) {
                continue;
            }
            equals = paramClass[i].equals(other.paramClass[i]);
            if (!equals) {
                break;
            }
        }

        return equals;
    }

    public boolean contains(EventType other) {
        if (other == null) {
            return false;
        }

        if (!TextUtils.equals(tag, other.tag)) {//tag不等
            return false;
        }

        int paLen1 = paramClass == null ? 0 : paramClass.length;
        int paLen2 = other.paramClass == null ? 0 : other.paramClass.length;
        if (paLen1 != paLen2) {
            return false;
        }
        for (int i = 0; i < paLen1; i++) {
            if (other.paramClass[i] == null) {
                continue;
            }

            if (!paramClass[i].isAssignableFrom(other.paramClass[i])) {
                return false;
            }
        }
        return true;
    }

}
