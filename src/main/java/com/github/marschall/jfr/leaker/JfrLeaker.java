package com.github.marschall.jfr.leaker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;

import jdk.jfr.Category;
import jdk.jfr.Description;
import jdk.jfr.Event;
import jdk.jfr.Label;

public final class JfrLeaker {

  static final String RUNNABLE_EVENT = JfrLeaker.class.getName() + "$" + RunnableEvent.class.getSimpleName();

  static final String JFR_RUNNABLE = JfrLeaker.class.getName() + "$" + JfrRunnable.class.getSimpleName();

  public void leak() {
    byte[] runnableClass = loadBytecode(JFR_RUNNABLE);
    byte[] eventClass = loadBytecode(RUNNABLE_EVENT);

    while (!Thread.currentThread().isInterrupted()) {
      this.loadAndRun(runnableClass, eventClass);
    }
  }

  private void loadAndRun(byte[] runnableClass, byte[] eventClass) {
    ClassLoader loader = new PredefinedClassLoader(runnableClass, eventClass);
    Runnable runnable = loadJfrRunnable(loader);
    runnable.run();
  }

  static Runnable loadJfrRunnable(ClassLoader classLoader) {
    try {
      return Class.forName(JFR_RUNNABLE, true, classLoader).asSubclass(Runnable.class).getConstructor().newInstance();
    } catch (ReflectiveOperationException e) {
      throw new RuntimeException("could not load runnable", e);
    }
  }

  static byte[] loadBytecode(String className) {
    String resource = toResourceName(className);
    ByteArrayOutputStream output = new ByteArrayOutputStream();
    try (InputStream inputStream = JfrLeaker.class.getClassLoader().getResourceAsStream(resource)) {
      byte[] buffer = new byte[8192];
      int read;
      while ((read = inputStream.read(buffer)) >= 0) {
        output.write(buffer, 0, read);
      }
    } catch (IOException e) {
      throw new UncheckedIOException("could not get bytecode of class:" + className, e);
    }
    return output.toByteArray();
  }

  private static String toResourceName(String className) {
    return className.replace('.', '/') + ".class";
  }

  public static void main(String[] args) {
    new JfrLeaker().leak();
  }

  static final class PredefinedClassLoader extends ClassLoader {

    static {
      registerAsParallelCapable();
    }

    private final byte[] runnableClass;

    private final byte[] eventClass;

    PredefinedClassLoader(byte[] runnableClass, byte[] eventClass) {
      super(null); // null parent
      this.runnableClass = runnableClass;
      this.eventClass = eventClass;
    }

    @Override
    protected Class<?> loadClass(String className, boolean resolve) throws ClassNotFoundException {
      // Check if we have already loaded it..
      Class<?> loadedClass = this.findLoadedClass(className);
      if (loadedClass != null) {
        if (resolve) {
          this.resolveClass(loadedClass);
        }
        return loadedClass;
      }

      if (className.equals(JFR_RUNNABLE)) {
        return this.loadClassFromByteArray(className, resolve, this.runnableClass);
      } else if (className.equals(RUNNABLE_EVENT)) {
        return this.loadClassFromByteArray(className, resolve, this.eventClass);
      } else {
        return super.loadClass(className, resolve);
      }
    }

    private Class<?> loadClassFromByteArray(String className, boolean resolve, byte[] byteCode) throws ClassNotFoundException {
      Class<?> clazz;
      try {
        clazz = this.defineClass(className, byteCode, 0, byteCode.length);
      } catch (LinkageError e) {
        // we lost the race, somebody else loaded the class
        clazz = this.findLoadedClass(className);
      }
      if (resolve) {
        this.resolveClass(clazz);
      }
      return clazz;
    }

  }

  public static final class JfrRunnable implements Runnable {

    @Override
    public void run() {
      RunnableEvent event = new RunnableEvent();
      event.setRunnableClassName("JfrRunnable");
      event.begin();
      event.end();
      event.commit();
    }
  }

  @Label("Runnable")
  @Description("An executed Runnable")
  @Category("Custom JFR Events")
  static class RunnableEvent extends Event {

    @Label("Class Name")
    @Description("The name of the Runnable class")
    private String runnableClassName;

    String getRunnableClassName() {
      return this.runnableClassName;
    }

    void setRunnableClassName(String operationName) {
      this.runnableClassName = operationName;
    }

  }

}
