# Virus Game
**Original creator**: CaryKH https://www.youtube.com/watch?v=o1IheoDRdGE  
Rewritten in Java, for the old processing version see the `legacy` branch

The goal of this fork is to improve original code and extend game's functionality
- General code refactoring
- Mutations (WIP)
- Better GUI (graph, divine controls, virus selctions)
- Optimisations (world tick now runs in a separate thread!)
- Settings (and map editing in `world.json`)
- Keyboard controls
- New cell types
- Codon system is now object oriented
- New DNA syntax
- Genome scrolling in editor

# Building
Use `./gradlew build` (or `./gradlew.bat build` on Windows) to compile the project. Compiled distribution with dependencies should be generated in `./build/distributions`

# Running
If you don't want to build the game yourself, you can download the latest release, extract it, and run the `VirusGameGradle-*.jar` inside, if you have problems with permissions on unix-like systems use `java -jar VirusGameGradle-*.jar` to run the jar, or give it execute permission using `chmod +x`

# License
The code (with the assets) committed to this fork is licensed under a [MIT License](https://choosealicense.com/licenses/mit),
as the original repository lacks copyright information the parts of software that are unmodified from the it are NOT covered by this license.
The source files that contain ONLY the MIT compatible code will begin with a MIT license.
