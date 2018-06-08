# LibGDXTilesetBuilder

LibGDXTilesetBuilder is a automated utility for simplifying the workflow between creating tileset images of various sizes and types
and generating the backend files for both Tiled map editor, and GDX Texture Packer. LibGDXTilesetBuilder will also perform several 
other convenience methods.

# Installation
1) Download the jar [here](https://github.com/wdavies973/LibGDXTilesetBuilder/releases/download/1.0/build.jar).
2) Create a `.bat` script with the following content (with quotes included):  
`java -jar "path-to-jar" "working-directory" "backup-directory"`  
An example `.bat` script might look like:  
`java -jar "C:\\Users\\Will\\Desktop\\eclipse\\workspace\\build.jar" "C:\\Users\\Will\\Desktop\\Assets" "E:\\Backup"`  
Add `PAUSE` to the end if you don't want the prompt to close after program execution.

# How it works
Because the tileset builder is fully automated, aside from the `.bat` script, the program takes no input or parameters. 
The program will attempt to smartly understand your working directly. Here's what it does:

1) Any files ending in `.blend1` are hidden so they won't appear in Windows Explorer. (I found these files kind of annoying.)
2) The program will search for directories named `Rendered`. All directories named `Rendered` should contain a bunch of images of the same type
and size. Each `Rendered` directory must also contain a `.meta` file (more on this later) specifying some hints to the program executor.
3) The program will search for a directory called `Output`, this must be a direct child folder of the working directory folder. 
4) The program will load images from each `Rendered` directory, resize them, rename them (automatically generating valid IDs), and
 copy them to the `Output` directory. A sprite-sheet is generated for each `Rendered` directory and also saved to output. The sprite-sheets
 are used by Tiled, while the collection of files should be packed by [GDXTexturePacker](https://github.com/crashinvaders/gdx-texture-packer-gui).
5) The entire working directory is backed up to the specified backup directory. Old backups are overwritten by new ones.
 
 # .meta Files
 1) These files specify hints to the program. The name of the `.meta` file doesn't matter, so long as it ends in `.meta`. Each `.meta` file should
  contain 4 lines:  
  `tileset-name`  (the name of the tile-set these images belong to)  
  `resize-px-width` (the pixel width these tiles should be resized to)  
  `resize-px-height` (the pixel height these tiles should be resized to)  
  `build-order` (the order this tile-sheet should be processed, this will affect its IDs)  
  
  An example `.meta` file:  
  ```terrain  
  width: 258  
  height: 194  
  1
  ```
  
# Other
For questions, email me at wdavies973@gmail.com 
  
