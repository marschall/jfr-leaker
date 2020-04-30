package com.github.marschall.jfr.leaker;

import static com.github.marschall.jfr.leaker.JfrLeaker.JFR_RUNNABLE;
import static com.github.marschall.jfr.leaker.JfrLeaker.RUNNABLE_EVENT;

import org.junit.jupiter.api.Test;

import com.github.marschall.jfr.leaker.JfrLeaker.PredefinedClassLoader;

class JfrCrasherTest {

  @Test
  void test() {
    byte[] runnableClass = JfrLeaker.loadBytecode(JFR_RUNNABLE);
    byte[] eventClass = JfrLeaker.loadBytecode(RUNNABLE_EVENT);

    ClassLoader classLoader = new PredefinedClassLoader(runnableClass, eventClass);

    Runnable runnable = JfrLeaker.loadJfrRunnable(classLoader);
    runnable.run();
  }

}
