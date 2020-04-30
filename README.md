JFR Leaker
==========

A reproducer for a JFR memory leak. The leak is `jdk.jfr.internal.TypeLibrary` keeps on holding to `jdk.jfr.internal.PlatformEventType` instances even when the underlying classes are unloaded.

This can for example happen when you redeploy a web application that contains custom JFR events.

The dominator tree shows the issue is `jdk.jfr.internal.TypeLibrary`.

![dominator tree](https://github.com/marschall/jfr-leaker/raw/master/src/main/javadoc/dominator_tree.png "Dominator Tree")

The histogram shows the issue is `jdk.jfr.internal.TypeLibrary` holding on to `jdk.jfr.internal.PlatformEventType`.

![histogram](https://github.com/marschall/jfr-leaker/raw/master/src/main/javadoc/histogram.png "Histogram")

The object list shows the issue is `jdk.jfr.internal.TypeLibrary#types` holding on to `jdk.jfr.internal.PlatformEventType`.

![list objects](https://github.com/marschall/jfr-leaker/raw/master/src/main/javadoc/histogram.png "List Objects")

The classloader explorer shows not ClassLoader-leaks.

![classloader explorer](https://github.com/marschall/jfr-leaker/raw/master/src/main/javadoc/classloader_explorer.png "ClassLoader Explorer")
