#Overview

This is a simple load test tool written in scala.
It runs a configurable mix of reader and writer
threads.  Data is pre loaded for the readers.
Simple validation is performed in each thread and errors
are reported to the results.csv log file without stopping
test execution.

Each agent thread puts its data into a ConcurrentLinkedQueue
while a seperate results writer thread consumes that queue and
streams results to the results.csv log file.

To run the code, 
`sbt compile`
`sbt run`

The configuration is currently in the Main function and the 
user will at minimum have to add a server secret there.
The database, collection and index will have to be set-up
ahead of time.  The expected "schema" is copied from the CRUD
documentation example.

For convenience, the output of a session is added here:
```$xslt
sbt run
[info] Loading settings from idea.sbt ...
[info] Loading global plugins from /home/daniel/.sbt/1.0/plugins
[info] Loading settings from plugins.sbt ...
[info] Loading project definition from /home/daniel/IdeaProjects/foo-build/project
[info] Loading settings from build.sbt ...
[info] Set current project to Hello (in build file:/home/daniel/IdeaProjects/foo-build/)
[info] Packaging /home/daniel/IdeaProjects/foo-build/target/scala-2.12/hello_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Running example.Main 
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
starting the results writer Thread
Waiting for threads to stop.
p99 latency (ms): 653
p95 latency (ms): 653
max latency (ms): 653
avg latency (ms): 427
throughput (req / sec): 35.1288056206089
```

The resulting results.csv is here
``` 
58,1588726087409,334,write,
70,1588726087437,313,read,
63,1588726087431,334,read,
64,1588726087430,339,read,
67,1588726087429,344,read,
66,1588726087430,346,read,
65,1588726087429,350,read,
69,1588726087429,353,read,
71,1588726087437,354,read,
68,1588726087433,374,read,
62,1588726087437,382,read,
60,1588726087417,642,write,
61,1588726087411,649,write,
59,1588726087410,651,write,
57,1588726087408,653,write,
```
The header is defined as `threadid, start (ms), latency (ms), type of agent, error`
#Next Steps 

* Use a configuration file or some other user input for parameters.
* Refactor so there is only one type of agent thread parameterized by behavior.
Agents should be able to run an arbitrary 'scenario' which could be a function
or even a script(s).
* Add in 'duration', 'num run' parameters which are mutually exclusive.
We want to be able to allow agent threads to run for either a fixed
duration of time or a fixed number of times.
* Try to make better use of scala futures.
* Find a math library to compute averages and percentiles
* Simple data visualization showing at minumum a scatter chart
of response times with agent type indicated by a color
* Summary stats broken down by type of agent (read or write)
