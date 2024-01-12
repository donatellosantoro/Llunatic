<p align="center">
  <img src="http://www.db.unibas.it/projects/llunatic/images/background_center.png"/>
</p>

Llunatic
========
Llunatic is a general purpose chase-engine that can be used both for data-repairing and data-exchange applications. Notable features of the system are:

- It has been designed to guarantee very good scalability on large databases, up to millions of tuples.

- It can be used to handle a wide variety of data-cleaning constraints, including functional dependencies, conditional functional dependencies, editing rules, fixing rules, denial constraints.

- It supports user interactions in order to interactively complete the data-repairing process.

- It fully supports both source-to-target and target tgds, and provides a new semantics to handle mappings and data repairing in a unified fashion.

- It implements a fast, parallel chase algorithm, and has support for highly expressive mappings, like DEDs.

Additional material about the project can be found at the following address: http://www.db.unibas.it/projects/llunatic/

The code is distributed according to the terms of the GPLv3 license (see file LICENSE.txt)

## How to import project in NetBeans
1. In NetBeans, File -> Open projects... and select the project folder
2. Execute ant target task `gfp`, either using command-line `ant gfp`, or using NetBeans (in the projects windows, right click on build.xml -> Run Target -> Other Targets -> gfp)

## Troubleshooting
If you have trouble building / running Llunatic, you may find [these notes](https://gitlab.com/antoonbronselaer/swipe-reproducibility/-/tree/master/tools#llunatic) helpful (thanks @antoonbronselaer): 

> For Llunatic, we pulled the code of the latest version on the official GitHub repository and compiled the Llunatic engine locally,
> with the class `Main.java` as main class.
> Prior to compilation, we changed the default parameter value of `recreateDBOnStart` to `false` in `LunaticConfiguration.java`
> to avoid the source database being dropped.

To compile, run `ant build` in the `lunaticEngine` directory. Once the project is built, you can run the experiments with e.g.: 

    ./runExp.sh misc/resources/persons-dbms.xml

Note that you must have a Postgres server (version < 12, see below) running. Make sure the `login` and `password` in the XML file 
matches those of your server's. Alternatively: 

> After building, the engine can be started on the command line.
> In doing so, a configuration file must be passed as a parameter.
> The command below starts the llunatic engine for the [hospital dataset](https://gitlab.com/antoonbronselaer/swipe-reproducibility/-/blob/master/tools/llunatic/hospital-standard.xml) with a standard configuration.
> All configurations used in the paper can be found in the folder [llunatic](https://gitlab.com/antoonbronselaer/swipe-reproducibility/-/tree/master/tools/llunatic).

    java -Xmx4096m -jar lunaticEngine-2.0-complete.jar hospital-standard.xml &> hospital/hospital-standard.out


Some notes on using Llunatic:

* Llunatic relies on the `with oids` option during table creation.
This feature was abandoned as of [version 12](https://www.postgresql.org/docs/12/release-12.html).
Hence, a version of PostgreSQL prior to version 12 must be used.
* Llunatic currently deals with textual constant values only.
In particular, this means that one should make sure the information in the allergen dataset is stored as text.
* Llunatic has an issue with attribute names containing '_'. We renamed attributes prior to running Llunatic to deal with this.
* For [eudract](https://gitlab.com/antoonbronselaer/swipe-reproducibility/-/tree/master/tools/llunatic), the following functional dependencies are present: `single_blind,double_blind -> open`, `open,double_blind -> single_blind` and `single_blind,open -> double_blind`.
During repair of these dependencies, we noticed severe slowness.
Logging the PostgreSQL database during repair shows that this slowness is caused by updating the cache.
We could not confirm the exact cause of this behaviour.
In order to compute a repair for eudract, we eventually removed the three dependencies from the configuration.
