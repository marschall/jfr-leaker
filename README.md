JFR Leaker
==========

A reproducer for a JFR memory leak. The leak is `jdk.jfr.internal.TypeLibrary` keeps on holding to `jdk.jfr.internal.PlatformEventType` instances even when the underlying classes are unloaded.

This can for example happen when you redeploy a web application that contains custom JFR events.

