# user guide

## installation

Download the [jar](https://en.wikipedia.org/wiki/JAR_(file_format)) file of the [latest release](https://github.com/nigoshh/huff-n-puff/releases/latest). To execute the app you must have [Java 8](https://java.com/en/download/) installed in your computer.

## how to use the app

To execute the app open your favorite [command-line interface](https://en.wikipedia.org/wiki/Command-line_interface) (like [Bash](https://en.wikipedia.org/wiki/Bash_(Unix_shell)) or [Command Prompt](https://en.wikipedia.org/wiki/Cmd.exe)) and run

```shell
java -jar path_to_jar_file
```

where _path_to_jar_file_ is the [path](https://en.wikipedia.org/wiki/Path_(computing)) pointing to the jar file you downloaded as instructed in the [installation](https://github.com/nigoshh/huff-n-puff/blob/master/docs/user-guide.md#installation) section. If the jar file is in your [working directory](https://en.wikipedia.org/wiki/Cd_(command)), _path_to_jar_file_ is simply the file's name (including the extension, _.jar_).

Once the app starts, you'll see a welcoming screen, after which you can select the desired functionality by typing the corresponding keyword. Regardless of the functionality you choose, at some point you'll be prompted to enter the path of a file (or a directory, AKA folder). As mentioned in the previous paragraph, if the file (or folder) is in your working directory, its path will simply be its full name (including possible file extensions). Here is a brief description of each available functionality.

### compare

You can compare the performance of the available compression algorithms. You can select either _simple_ mode or _thorough_ mode.

### simple

In simple mode the algorithms will be tested on a single file, which means that the file will be compressed and decompressed once with each algorithm; the compressed files will be saved in the same directory of the original uncompressed file. Then the following stats will be displayed:
-   bits/symbol: bits per symbol (bps), which means the size of the compressed file (in bits) divided by the size of the original uncompressed file (in bytes), as explained [here](http://www.data-compression.info/Corpora/index.html);
-   d. c. ratio: data compression ration, which means the size of the original uncompressed file (in bits) divided by the size of the compressed file (in bits), as explained [here](https://en.wikipedia.org/wiki/Data_compression_ratio);
-   compr. time: time elapsed during file compression, in nanoseconds;
-   decompr. time: time elapsed during file decompression, in nanoseconds.

### thorough

In thorough mode the algorithms will be tested on each file of the given directory. You can choose how many times the files will be compressed and decompressed. By performing the same operation many times, the values for compression time and decompression time will be statistically more significant, because of the randomness associated with the duration of each execution in a [multitasking operating system](https://en.wikipedia.org/wiki/Computer_multitasking). Each file in the chosen directory will be compressed and decompressed the chosen amount of times with each of the available compression algorithms. The compressed files will be saved in the same directory as the original uncompressed files. Each compressed file will be saved only once, regardless of the number of times it was compressed (meaning there will be no duplicates). Then comparison stats will be displayed, similar to the ones produced by [simple](https://github.com/nigoshh/huff-n-puff/blob/master/docs/user-guide.md#simple) mode, with the following differences:
-   bits/symbol is the [unweighted mean](https://en.wikipedia.org/wiki/Arithmetic_mean) of each file's bps, meaning the total size of the compressed files (in bits) divided by the total size of the original uncompressed files (in bytes);
-   d. c. ratio is the unweighted mean of each file's d. c. ratio, meaning the total size of the original uncompressed files (in bits) divided by the total size of the compressed files (in bits);
-   compr. time is the total time elapsed during file compression, in nanoseconds (meaning that if each file was compressed _n_ times, compr. time is the total time it took to compress all the files _n_ times);
-   like compr. time, decompr. time is the total time elapsed during file decompression, in nanoseconds.

## what are the acceptable inputs

HUFF n PUFF reads files as bytes, so file type doesn't matter; you can compress whatever file you like, up to a size limit of little more than 1 GB (or less, depending on how much memory your system allocates to the command-line process). If you use it on file types which are already compressed (like [EPUB](https://en.wikipedia.org/wiki/EPUB), [MP4](https://en.wikipedia.org/wiki/MPEG-4_Part_14), [Matroska](https://en.wikipedia.org/wiki/Matroska) and many others), you most likely won't get any reduction in size.

## where to find the files needed for testing

[Here](http://www.data-compression.info/Corpora/index.html) you can get some data compression corpora to test the app, if you wish. For more information about how I tested the app see the [software test documentation](https://github.com/nigoshh/huff-n-puff/blob/master/docs/software-test-documentation.md).
