Llunatic
========

Llunatic is a general purpose chase-engine that can be used both for data-repairing and data-exchange applications. Notable features of the system are:

- It has been designed to guarantee very good scalability on large databases, up to millions of tuples.

- It can be used to handle a wide variety of data-cleaning constraints, including functional dependencies, conditional functional dependencies, editing rules, fixing rules, denial constraints.

- It supports user interactions in order to interactively complete the data-repairing process.

- It fully supports both source-to-target and target tgds, and provides a new semantics to handle mappings and data repairing in a unified fashion.

- It implements a fast, parallel chase algorithm, and has support for highly expressive mappings, like DEDs.

Additional material about the project can be found at the following address: http://www.db.unibas.it/projects/llunatic/

===
### How to import project in NetBeans ####
1. In NetBeans, File -> Open projects... and select the project folder
2. Execute ant target task `gfp`, either using command-line `ant gfp`, or using NetBeans (in the projects windows, right click on build.xml -> Run Target -> Other Targets -> gfp)
