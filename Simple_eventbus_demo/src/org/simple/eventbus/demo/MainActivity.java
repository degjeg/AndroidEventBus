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

package org.simple.eventbus.demo;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import java.lang.reflect.Method;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Method method[] = Test.class.getDeclaredMethods();
        Method method1[] = Test1.class.getDeclaredMethods();

        dump(method);
        dump(method1);

        Test test = new Test() {
            @Override
            void test3() {

            }
        };
        Test1 test1 = new Test1();
        method = test.getClass().getDeclaredMethods();
        method1 = test1.getClass().getDeclaredMethods();

        Log.d(TAG, "test instance of Test" + Test.class.isInstance(test));
        Log.d(TAG, "test1 instance of Test" + Test.class.isInstance(test1));

        Log.d(TAG, "test instance of Test1" + Test1.class.isInstance(test));
        Log.d(TAG, "test1 instance of Test1" + Test1.class.isInstance(test1));
        dump(method);
        dump(method1);

        test = new Test() {
            @Override
            void test3() {

            }
        };


        test1 = new Test1();

        Log.d(TAG, "test instance of Test" + Test.class.isInstance(test));
        Log.d(TAG, "test1 instance of Test" + Test.class.isInstance(test1));

        Log.d(TAG, "test instance of Test1" + Test1.class.isInstance(test));
        Log.d(TAG, "test1 instance of Test1" + Test1.class.isInstance(test1));

        method = test.getClass().getDeclaredMethods();
        method1 = test1.getClass().getDeclaredMethods();
        dump(method);
        dump(method1);
        Test1.class.isLocalClass()

    }

    void dump(Method method[]) {
        for (int i = 0; i < method.length; i++) {
            Log.d("Methods", "" + i + "=" + method[i]);
        }

    }


    abstract class Test {
        void test1() {

        }

        void test2() {

        }

        abstract void test3();
    }

    class Test1 extends Test {
        void test2() {

        }

        void test3() {

        }
    }

}
