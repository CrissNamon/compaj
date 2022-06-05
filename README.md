# CompaJ Project
This is the home of the CompaJ Project - open source, cross-platform programming and numeric computing platform for math modelling.

<details> 
  <summary>Screenshots</summary>
  
   ![Terminal](https://user-images.githubusercontent.com/22001123/172052428-5663f5e4-e667-4280-8099-9c0e2f482f1e.png)
   ![CodeEditor](https://user-images.githubusercontent.com/22001123/172052540-0b28d24e-54f3-4bee-b6a7-996c4bf87538.png)
   ![WorkSpace](https://user-images.githubusercontent.com/22001123/172052591-0a536b12-2d52-4dde-8a73-af29c178d775.png)
</details>

### Status
___
Project is under active development. CompaJ supports only basic functions and models now, which are not intended to use in real projects.

### Structure
___
CompaJ has modular structure based on Maven modules.
- [CompaJ Core](https://github.com/CrissNamon/compaj/tree/main/core)
    - Core is a library with basic content for CompaJ. It contains simple functions and models.
- [CompaJ Applied](https://github.com/CrissNamon/compaj/tree/main/applied)
    - Applied module contains ready to use models.
- [CompaJ Lang](https://github.com/CrissNamon/compaj/tree/main/lang)
    - CompaJ Lang is an object oriented and optionally typed programming language based on Groovy with useful extensions to reduce redundant symbols and simplify calculations.
- [CompaJ REPL](https://github.com/CrissNamon/compaj/tree/main/repl)
    - CompaJ REPL is a console application which provides all functionality of CompaJ Lang.
- [CompaJ](https://github.com/CrissNamon/compaj/tree/main/gui)
    - CompaJ is a standalone application with GUI and many useful tools for CompaJ REPL.  

### Learn
___
#### Lang
CompaJ uses Groovy under the hood with some tweaks. You can learn Groovy on its official [site](https://groovy-lang.org/documentation.html).
Documentation about CompaJ syntax will be released soon.

#### Math
CompaJ uses [Apache Commons Math](https://commons.apache.org/proper/commons-math/userguide/index.html) library for math operations.

#### Visualization
CompaJ uses widgets system for visualization in _WorkSpace_. Documentation will be released soon.

#### Other
All documentation and necessary information with tutorials will be released soon. 

### Authors
___
* [Danila Rassokhin](https://gihub.com/crissnamon) [![Twitter](https://img.shields.io/twitter/follow/kpekepsalt?style=social)](https://twitter.com/kpekepsalt)

### Copyright
___
CompaJ uses [Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0). See [LICENSE.md](https://github.com/CrissNamon/compaj/blob/main/LICENSE.md) for more details.
