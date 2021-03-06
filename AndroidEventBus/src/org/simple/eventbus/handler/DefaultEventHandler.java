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

package org.simple.eventbus.handler;

import android.util.Log;

import org.simple.eventbus.EventBus;
import org.simple.eventbus.Subscription;

import java.lang.reflect.InvocationTargetException;

/**
 * 事件在哪个线程post,事件的接收就在哪个线程
 *
 * @author mrsimple
 */
public class DefaultEventHandler implements EventHandler {
    /**
     * handle the event
     *
     * @param subscription
     * @param event
     */
    public void handleEvent(Subscription subscription, Object event1) {
        if (subscription == null
                || subscription.getSubscriber() == null) {
            return;
        }
        try {
            Object event[] = (Object[]) event1;

            if (EventBus.LOG_ON) {
                Log.d(EventBus.TAG, "handleEvent:" + subscription + ",event:" + event1);
            }
            // 执行
            if (event == null || event.length == 0) {
                subscription.targetMethod.invoke(subscription.getSubscriber());
            } else if (event.length == 1) {
                subscription.targetMethod.invoke(subscription.getSubscriber(), event[0]);
            } else if (event.length == 2) {
                subscription.targetMethod.invoke(subscription.getSubscriber(), event[0], event[1]);
            } else if (event.length == 3) {
                subscription.targetMethod.invoke(subscription.getSubscriber(), event[0], event[1], event[2]);
            } else if (event.length == 4) {
                subscription.targetMethod.invoke(subscription.getSubscriber(), event[0], event[1], event[2], event[3]);
            } else if (event.length == 5) {
                subscription.targetMethod.invoke(subscription.getSubscriber(), event[0], event[1], event[2], event[3], event[4]);
            } else if (event.length == 6) {
                subscription.targetMethod.invoke(subscription.getSubscriber(), event[0], event[1], event[2], event[3], event[4], event[5]);
            } else {
                Log.e("EventBus", "too much parameters:" + event.length);
            }

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
