# software test documentation

## what is tested and how

Classes in ui and io packages haven't been tested at all.

Unit tests have been made for most classes in the domain package.

The BitSequence class has been tested thoroughly; to write tests more comprehensibly, the value of a given BitSequence is usually checked from its toString method, which is simple enough that it doesn't need any testing in itself.

The Huffman class has been tested with a very simple test which checks that decompressing a compressed file returns exactly the original file. This test can be considered an integration test, since Huffman class methods compressFile and decompressFile use also other classes.

## what inputs were used for testing (important for comparisons)

## how can the tests be replicated

## graphical display of empirical testing results

## testing is ideally an executable program (use JUnit)
