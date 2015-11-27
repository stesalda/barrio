_Active development in this project has ceased. However, some of the ideas of Barrio are now addressed in our new project, [gql4jung](http://code.google.com/p/gql4jung/)._

Barrio is a dependency analyzer for object-oriented programs implemented in Java. It discovers and visualizes community structures in the dependency graph which might represent opportunities to refactor the program into a more modular structure. For instance, a package containing two independent clusters could be split, and the new packages could be deployed in separate jar files. Barrio will also detect if there are few   dependencies that destroy the modularity of programs (i.e., glue clusters together).

For more information, see

Dietrich, J., Yakovlev, V., Mccartin, C., Jenson, G., and Duchrow, M. 2008. Cluster analysis of Java dependency graphs. In Proceedings of the 4th ACM Symposium on Software Visualization (Ammersee, Germany, September 16 - 17, 2008). Softvis08. ACM, New York, NY, 91-94. DOI: http://doi.acm.org/10.1145/1409720.1409735

The following figure shows the dependency graph of the classes in the Apache Commons Collection library analysed with the barrio Eclipse plugin. The blue and red regions show clusters and explicitly defined packages. The large package on the left side of the graph contains several clusters, and could therefore be split.

![http://barrio.googlecode.com/svn/trunk/barrio-docs/figures/packClustAggr_s.jpg](http://barrio.googlecode.com/svn/trunk/barrio-docs/figures/packClustAggr_s.jpg)

Barrio is designed as an Eclipse plugin. Barrio has the following features:

  1. Barrio can import dependency graphs stored as ODEM files (these graphs can be generated with [CDA](http://www.dependency-analyzer.org/index.html) from bytecode).
  1. Barrio can also extract the graph from project source code.
  1. Graphs extracted from source code can be exported back to ODEM files.
  1. Barrio can apply filters to the graph.
  1. Barrio can visualize the graph. Visualization is based on [Prefuse](http://prefuse.org/).
  1. Barrio can find clusters in the graph based on the [Girvan-Newman algorithm](http://en.wikipedia.org/wiki/Girvan-Newman_algorithm). The [Jung](http://jung.sourceforge.net/) implementation of the algorithm is used.
  1. Barrio allows users to set a separation level in order to remove edges destroying the modularity of the program. Those edges might present refactoring opportunities.
  1. The discovered clusters are compared with the modular structure of the program (i.e., the use of packages and jar containers).
  1. Barrio can be used to define stereotypes based on rules. For instance, UI classes can be defined as classes referencing the java.awt package.

### Usage ###

  1. The software is distributed using the Eclipse Update mechanism. The update site is http://barrio.googlecode.com/svn/trunk/barrio.update/ . Please check from time to time for new versions. **The plugin requires Eclipse version 3.3 or better!**
  1. Once installed, you will need some input data. You can download a zip file containing sample data from the download section, or create data for your own project using the [CDA](http://www.dependency-analyzer.org/index.html) tool.


### Planned features ###

  1. Detection of motifs (patterns) in the graph using a declarative graph query language.



