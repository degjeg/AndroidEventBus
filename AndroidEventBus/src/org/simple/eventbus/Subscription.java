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

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * 订阅者对象,包含订阅者和目标方法
 *
 * @author mrsimple
 */
public class Subscription {
    /**
     * 订阅者对象
     */
    private WeakReference<Object> subscriber;
    /**
     * 接受者的方法
     */
    public Method targetMethod;
    /**
     * 执行事件的线程模型
     */
    public ThreadMode threadMode;

    /**
     * @param subscriber
     * @param method
     */
    public Subscription(Object subscriber, TargetMethod targetMethod) {
        this.subscriber = new WeakReference<Object>(subscriber);
        this.targetMethod = targetMethod.method;
        this.threadMode = targetMethod.threadMode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((subscriber != null && subscriber.get() == null) ? 0 : subscriber.get().hashCode());
        result = prime * result + ((targetMethod == null) ? 0 : targetMethod.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;

        if (!(obj instanceof Subscription))
            return false;

        Subscription other = (Subscription) obj;

        if (getSubscriber() == null && other.getSubscriber() == null)//等于null不需要执行任何操作
            return true;

        if (getSubscriber() != other.getSubscriber()) {
            return false;
        }

        if (targetMethod != other.targetMethod) {
            return false;
        }

        return true;
    }

    public java.lang.Object getSubscriber() {
        return (subscriber != null && subscriber.get() != null) ? subscriber.get() : null;
    }
}
