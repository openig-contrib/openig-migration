ForgeRock OpenIG Migration Tool
===================================

[![Build Status](https://travis-ci.org/sauthieg/openig-migration.svg)](https://travis-ci.org/sauthieg/openig-migration)

This simple command line project aims to ease the migration of OpenIG 3.0 configuration files to OpenIG 3.1.

It requires Java 8 to compile and run.

Usage
-----------

    >$ java -jar target/openig-migration-1.0-SNAPSHOT-jar-with-dependencies.jar <JSON filename to migrate>

This tool write the migrated JSON on `System.out`

Supported actions
------------------------

* `heap/objects` array has been simplified to just `heap`
* Heap object declaration inline (when possible)
* Remove `"config": {}` (empty element)
* Rename `RedirectFilter` to `LocationHeaderFilter`
