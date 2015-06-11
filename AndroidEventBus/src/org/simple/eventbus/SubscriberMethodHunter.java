/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Umeng, Inc
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
 */

package org.simple.eventbus;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * the subscriber method hunter, find all of the subscriber's methods which
 * annotated with @Subcriber.
 *
 * @author mrsimple
 */
public class SubscriberMethodHunter {

    /**
     * the event bus's subscriber's map
     */
    Map<EventType, CopyOnWriteArrayList<Subscription>> mSubscriberMap;

    private static final Map<String, List<TargetMethod>> methodCache = new HashMap<String, List<TargetMethod>>();

    /**
     * @param subscriberMap
     */
    public SubscriberMethodHunter(Map<EventType, CopyOnWriteArrayList<Subscription>> subscriberMap) {
        mSubscriberMap = subscriberMap;
    }

    /**
     * @param subscriber
     * @return
     */
    public void findSubscribeMethods(Object subscriber) {
        if (mSubscriberMap == null) {
            throw new NullPointerException("the mSubscriberMap is null. ");
        }
        Class<?> clazz = subscriber.getClass();
        // 查找类中符合要求的注册方法,直到Object类
        String clsName = clazz.getName();
        List<TargetMethod> subscriberMethods = null;


        synchronized (methodCache) {
            if (methodCache.containsKey(clsName)) {
                subscriberMethods = methodCache.get(clsName);
            }
        }

        if (subscriberMethods == null) {
            subscriberMethods = new ArrayList<>();
            while (clazz != null && !isSystemClass(clazz.getName())) {
                final Method[] allMethods = clazz.getDeclaredMethods();
                for (int i = 0; i < allMethods.length; i++) {
                    Method method = allMethods[i];
                    // 根据注解来解析函数
                    Subscriber annotation = method.getAnnotation(Subscriber.class);

                    if (annotation != null) {
                        // 获取方法参数
                        Class<?>[] paramsTypeClass = method.getParameterTypes();
                        // just only one param
                        int len = paramsTypeClass == null ? 0 : paramsTypeClass.length;
                        for (int midx = 0; midx < len; midx++) {
                            paramsTypeClass[midx] = convertType(paramsTypeClass[midx]);
                        }

                        EventType eventType = new EventType(paramsTypeClass, annotation.tag());
                        TargetMethod subscribeMethod = new TargetMethod(method, eventType, annotation.mode());
                        subscriberMethods.add(subscribeMethod);

                    } else {
                        if (EventBus.LOG_ON) {
                            Log.d(EventBus.TAG, "annotation is null:" + method);
                        }
                    }
                } // end for

                // 获取父类,以继续查找父类中复合要求的方法
                clazz = clazz.getSuperclass();
            }

            methodCache.put(clsName, subscriberMethods);
        }

        for (TargetMethod subscribeMethod : subscriberMethods) {
            subscribe(subscribeMethod.eventType, subscribeMethod, subscriber);
        }

        if (EventBus.LOG_ON) {
            StringBuilder sb = new StringBuilder();
            for (EventType eventType : mSubscriberMap.keySet()) {
                sb.append("event:(");
                sb.append(mSubscriberMap.get(eventType).size());
                sb.append("event:)->");
                sb.append(eventType);

                sb.append("\n");
            }
            Log.d(EventBus.TAG, "after findSubscribeMethods:\n" + subscriber + "\n" + sb);
        }
    }

    /**
     * @param event
     * @param method
     * @param subscriber
     */
    private void subscribe(EventType event, TargetMethod method, Object subscriber) {
        CopyOnWriteArrayList<Subscription> subscriptionLists = mSubscriberMap.get(event);
        if (subscriptionLists == null) {
            subscriptionLists = new CopyOnWriteArrayList<Subscription>();
        }

        Subscription newSubscription = new Subscription(subscriber, method);
        if (subscriptionLists.contains(newSubscription)) {
            return;
        }

        subscriptionLists.add(newSubscription);
        // 将事件类型key和订阅者信息存储到map中
        mSubscriberMap.put(event, subscriptionLists);
    }

    /**
     * remove subscriber methods from map
     *
     * @param subscriber
     */
    public void removeMethodsFromMap(Object subscriber) {
        Iterator<CopyOnWriteArrayList<Subscription>> iterator = mSubscriberMap
                .values().iterator();
        while (iterator.hasNext()) {
            CopyOnWriteArrayList<Subscription> subscriptions = iterator.next();
            if (subscriptions != null) {
                List<Subscription> foundSubscriptions = new LinkedList<Subscription>();
                Iterator<Subscription> subIterator = subscriptions.iterator();
                while (subIterator.hasNext()) {
                    Subscription subscription = subIterator.next();
                    if (subscription.getSubscriber() == null ||
                            (subscriber != null && subscription.getSubscriber().equals(subscriber))) {

                        if (EventBus.LOG_ON && subscriber != null) {
                            Log.d("", "### 移除订阅 " + subscriber.getClass().getName());
                        }

                        foundSubscriptions.add(subscription);
                    }
                }

                // 移除该subscriber的相关的Subscription
                subscriptions.removeAll(foundSubscriptions);
            }

            // 如果针对某个Event的订阅者数量为空了,那么需要从map中清除
            if (subscriptions == null || subscriptions.size() == 0) {
                iterator.remove();
            }
        }
    }

    /**
     * if the subscriber method's type is primitive, convert it to corresponding
     * Object type. for example, int to Integer.
     *
     * @param eventType origin Event Type
     * @return
     */
    private Class<?> convertType(Class<?> eventType) {
        Class<?> returnClass = eventType;
        if (eventType.equals(boolean.class)) {
            returnClass = Boolean.class;
        } else if (eventType.equals(int.class)) {
            returnClass = Integer.class;
        } else if (eventType.equals(float.class)) {
            returnClass = Float.class;
        } else if (eventType.equals(double.class)) {
            returnClass = Double.class;
        }

        return returnClass;
    }

    private boolean isSystemClass(String name) {
        return name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("android.");
    }

}
