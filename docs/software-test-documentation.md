# software test documentation

## unit tests and integration tests

Classes in ui and io packages haven't been tested at all.

Unit tests and integration tests have been made for most classes in the domain package. The code coverage of the domain package is 99%, but effectively 100%; the only things that are not tested are some CompressionAlgorithm methods, which return strings needed in the ui classes, and the constructor of the Utils class, which is useless since Utils contains only static methods.

Some classes don't have a test file named after them, because they're effectively a subcomponent of another class and don't offer any functionality by themselves; one such example is LZWDictEntry, which is tested together with LZWDictionary. Also, there isn't always a clear distinction between unit tests and integration tests, so I'll describe briefly what each test file does.

### ArrayQueueTest

Unit tests for ArrayQueue. They test the basic queue operations, nothing particular to point out here.

### BitSequenceTest

Unit tests for BitSequence. This class was the hardest to code and debug, and thus has been tested thoroughly. To write tests more comprehensibly, the value of a given BitSequence is usually checked from its toString method, which itself is simple enough that it doesn't need any testing.

### ByteSequenceTest

Unit tests for ByteSequence. This class may sound similar to BitSequence but it wasn't nearly as complicated to code, so testing is also much simpler.

### CompressionAlgorithmTest

Tests for public methods of CompressionAlgorithm subclasses (Huffman and LZW); they can be considered as integration tests, since the tested methods use other classes of the domain package. These tests try to cover many corner cases. Some examples:
-   for Huffman, the case when all 256 symbols' probabilities are equal;
-   for LZW, the case when the "special case" (when the encoder emits a codeword right after it was put into the dictionary, and thus the decoder sees a codeword which isn't yet in its dictionary) happens right before (and/or right after) each codeword length change and dictionary reset;
-   for LZW, the case when the file to compress contains only one byte value (repeated a huge number of times), and thus the aforementioned "special case" happens at each step of the encoding/decoding; this means that the dictionary will contain longer and longer sequences, and thus the input's size needs to be really large for the compression/decompression process to reach a dictionary reset. Because of the large input size this particular test takes a very long time to complete (about half a minute), so it is disabled by default; to execute it just remove the _@Disabled_ annotation.

Please note that only LZW with maximum codeword length of 12 (_lzw12_) is tested; other variations with different maximum lengths can be tested (and should work) by using the corresponding arguments in the LZW constructor, but the corner cases just described have been tuned specifically for lzw12 (in particular the chosen _LENGTH_CHANGE_INDEXES_ work only with lzw12).

### HuffNodeTest

Unit tests for HuffNode; the two HuffNode Comparators are also tested.

### LZWDictionaryTest

Technically these tests use also LZWDictEntry, but they can be considered as unit tests for LZWDictionary, since LZWDictEntry is just a container and doesn't have any functionality by itself.

### TreeRepresentationTest

Unit test for TreeRepresentation, which is a data structure used by Huffman.

### UtilsTest

Unit tests for Utils, to check that its methods work like their counterparts from java.util.Arrays.

## performance testing

Performance testing was carried out on the corpora for data compression found [here](http://www.data-compression.info/Corpora/index.html). The tests can be replicated by downloading the desired corpus, extracting it into a folder, and selecting the [thorough](https://github.com/nigoshh/huff-n-puff/blob/master/docs/user-guide.md#thorough) comparison mode from the command-line interface, as described in the [user guide](https://github.com/nigoshh/huff-n-puff/blob/master/docs/user-guide.md). The graphs displayed here can be obtained using [this R script](https://github.com/nigoshh/huff-n-puff/tree/master/docs/plots/benchmark_barplots.r); data from the comparisons have to be manually copied into the R script.

Compression rate is measured in bits per symbol (bps), which is equal to the quotient of the size of the compressed data in bits to the size of the uncompressed data in bytes. LZW usually achieved better compression rates than Huffman, and LZW with longer maximum codeword length achieved better compression rates than LZW with shorter maximum codeword length (as expected).

![Compression rate, measured in bits per symbol (bps)](https://github.com/nigoshh/huff-n-puff/raw/master/docs/plots/bps.png)

Compression time and decompression time are measured in nanoseconds in the comparison tests. The comparison thorough mode was carried out with a value of 10 for the repetitions parameter, meaning that each file in each corpus was compressed and decompressed 10 times. In the R script the total time in nanoseconds is converted to seconds and divided by 10, so the measure seen in the graphs is the mean time (in seconds) that it took to compress/decompress all the files in a given corpus (once). Compression/decompression times do not include file reading/writing, since they are slow disk operations (which could theoretically take up most of compression/decompression time), and they don't depend on any specific compression algorithm (they are always called in the same way, regardless of the algorithm used for compression/decompression).

Compression times vary significantly from algorithm to algorithm, while decompression times are more similar across different algorithms. The fasts compression times are achieved by Huffman. LZW gets slower and slower as maximum codeword length is increased. This is probably due to the [dictionary implementation](https://github.com/nigoshh/huff-n-puff/blob/master/docs/software-implementation-documentation.md#lzw), which saves (and thus writes to memory) each sequence separately. Sequences could contain only a symbol (byte) and a link to the prefix sequence, since each time a new sequence is added to the dictionary the prefix part of that new sequence already exists in the dictionary, as pointed out in [this implementation by Juha Nieminen](http://warp.povusers.org/EfficientLZW/part2.html).

![Compression time, measured in seconds (bigger file sizes)](https://github.com/nigoshh/huff-n-puff/raw/master/docs/plots/ctm-b.png)

![Compression time, measured in seconds (smaller file sizes)](https://github.com/nigoshh/huff-n-puff/raw/master/docs/plots/ctm-s.png)

![Decompression time, measured in seconds (bigger file sizes)](https://github.com/nigoshh/huff-n-puff/raw/master/docs/plots/dtm-b.png)

![Decompression time, measured in seconds (smaller file sizes)](https://github.com/nigoshh/huff-n-puff/raw/master/docs/plots/dtm-s.png)

## sources

<http://www.data-compression.info/Corpora/index.html>

<http://warp.povusers.org/EfficientLZW/part2.html>
