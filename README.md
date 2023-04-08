# CompaJ Project

[![Build](https://github.com/CrissNamon/compaj/actions/workflows/main.yml/badge.svg)](https://github.com/CrissNamon/compaj/actions/workflows/main.yml)
[![Trello](https://img.shields.io/badge/Trello-Follow%20progress-blueviolet)](https://trello.com/b/4c7FvMFI/compaj-desktop)

<img width="640" alt="Compaj logo wide" src="https://user-images.githubusercontent.com/22001123/227656456-b43f3ef3-80d0-4c36-b5ef-d8e84e40ddcf.png">

This is the home of the CompaJ Project - open source, cross-platform programming and numeric
computing platform for math modelling.

<details> 
  <summary>Screenshots</summary>

![Terminal](https://user-images.githubusercontent.com/22001123/172052428-5663f5e4-e667-4280-8099-9c0e2f482f1e.png)
![CodeEditor](https://user-images.githubusercontent.com/22001123/230720718-85b268fe-a1be-4b78-98f4-3d17b82560c9.png)
![WorkSpace](https://user-images.githubusercontent.com/22001123/172052591-0a536b12-2d52-4dde-8a73-af29c178d775.png)
</details>

### Status

___
Project is under active development. CompaJ supports only basic functions and models now, which are
not intended to use in real projects.
See project progress and feature on
public [Trello board](https://trello.com/b/4c7FvMFI/compaj-desktop).

### Structure

___
CompaJ has modular structure based on Maven modules.

- [CompaJ](https://github.com/CrissNamon/compaj/tree/main/gui)
  - CompaJ is an extendable application with GUI and many useful tools such as terminal,
    code editor with intelligent code completion and widgets for scientific visualization.
- [CompaJ Lang](https://github.com/CrissNamon/compaj/tree/main/lang)
    - CompaJ Lang is an object-oriented and optionally typed programming language based on Groovy
      with useful extensions to reduce redundant symbols and simplify calculations.
- [CompaJ REPL](https://github.com/CrissNamon/compaj/tree/main/repl)
    - CompaJ REPL is a console application which provides all functionality of CompaJ Lang.
- [CompaJ Plugin API](https://github.com/CrissNamon/compaj/tree/main/plugin-api)
  - CompaJ can load external plugins using Plugin API
- [CompaJ Cloud](https://github.com/CrissNamon/compaj-cloud)
    - Cloud infrastructure to run CompaJ as SaaS

### Learn

___

#### Lang

CompaJ uses Groovy under the hood with some tweaks. You can learn Groovy on its
official [site](https://groovy-lang.org/documentation.html).
Documentation about CompaJ syntax available
in [Wiki](https://github.com/CrissNamon/compaj/wiki/CompaJ-Lang).

#### Math

CompaJ
uses [Apache Commons Math](https://commons.apache.org/proper/commons-math/userguide/index.html)
library for math operations. Learn about CompaJ math features
in [Wiki](https://github.com/CrissNamon/compaj/wiki/CompaJ-Math).

#### Visualization

CompaJ uses widgets system for visualization in _WorkSpace_. Documentation available
in [Wiki](https://github.com/CrissNamon/compaj/wiki/CompaJ-WorkSpace)

#### Plugin API

Plugin API provides a way for developers to extend CompaJ system with new features.
Documentation available in [Wiki](https://github.com/CrissNamon/compaj/wiki/Plugin-API)

#### Other

All documentation and necessary information with tutorials will be released soon.

### Authors

___

* [Danila Rassokhin](https://github.com/crissnamon) [![Twitter](https://img.shields.io/twitter/follow/kpekepsalt_en?style=social)](https://twitter.com/kpekepsalt_en)

### Copyright

___
CompaJ uses [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0).
See [LICENSE.md](https://github.com/CrissNamon/compaj/blob/main/LICENSE.md) for more details.
