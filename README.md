# odata POC


This is the last external part of this POC, to be continued in an internal repo.
-------------

Both the open-types functionality and the POJO-annotation support work wonderfully wel on SDL.
Now, believe it or not, a *JEE* app includes a *Spring's DispatcherServlet* to handle web-requests. The *ContextLoaderListener* creates a second context to load DSL's Spring beans. DSL's *Spring*/*Java* code is just a wrapper, the core code is written in *Scala* and runs on *Akka*.

For back-up/administration: https://github.com/omniavincitlabor/odata4-test and https://github.com/sdl/odata/blob/master/odata_test/src/main/java/com/sdl/odata/test/model/Customer.java
