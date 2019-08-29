# QuetexJ #


## What is QuetexJ ? ##

* **QuetexJ** is a Java Swing MVC sets library
 that supports pseudo endless [text-component][TEXTAREA].

* QuetexJ is useful for outputting adhoc text information
 like logging.

* QuetexJ automatically chops beginning of text to free memory
 according to height of text component behind [view-port][VIEWPORT].
 Of course the physical line layouts are kept.

* QuetexJ works closely with vertical [JScrollBar][SCROLLBAR]
 to keep view-port view when chopping text.

* QuetexJ supports auto-tracking mode
 that always displays last part of text component to view-port.

* QuetexJ includes log handler that complies with [java.util.logging][LOGGING].

* QuetexJ archive includes test-harness GUI for trial use.


## How to build ##

* QuetexJ needs to use [Maven 3.3.9+](https://maven.apache.org/)
 and JDK 1.8+ to be built.

* QuetexJ runtime does not depend on any other library at all.
 Just compile Java sources under `src/main/java/` if you don't use Maven.


## License ##

* Code is under [The MIT License][MIT].


## Project founder ##

* By [olyutorskii](https://github.com/olyutorskii) at 2019


[LOGGING]: https://docs.oracle.com/javase/8/docs/api/java/util/logging/package-summary.html
[VIEWPORT]: https://docs.oracle.com/javase/8/docs/api/javax/swing/JViewport.html
[SCROLLBAR]: https://docs.oracle.com/javase/8/docs/api/javax/swing/JScrollBar.html
[TEXTAREA]: https://docs.oracle.com/javase/8/docs/api/javax/swing/JTextArea.html
[MIT]: https://opensource.org/licenses/MIT


--- EOF ---
