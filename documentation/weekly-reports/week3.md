# week 3

[weekly timesheet](https://github.com/nigoshh/huff-n-puff/blob/master/documentation/timesheet.md#week-3)

## what have I done

I have made some new classes. The BitSequence class was needed to manipulate single bits. It has also been tested to some extent. Since it was suggested that we first use Java's ready made data structures, I considered using BitSet; but I didn't think it would fit the purpose (leading zeros are ignored if I understood correctly), and it seemed like it would be complicated to substitute my own implementation for it later.

I adopted the suggested approach with the queue data structure, needed in the Huffman algorithm. I created the interface SimpleQueue, and a temporary class that masks Java's implementation (ArrayDeque). Later on I will make my own SimpleQueue implementation (with its unit tests).

The last class I made is TreeRepresentation, which facilitates the coding of a canonical Huffman tree into a byte array (to be written to the beginning of the compressed file). This class hasn't been tested yet.

I also almost finished the structure of the Huffman algorithm, both encoding and decoding. None of it has been tested with unit tests though, so there's still a lot of work to be done there.

## how has the app improved

The app functionality hasn't improved yet, but with the new classes (in particular BitSequence) now all the tools are there, and the only thing left for the Huffman algorithm is to debug each one of its steps.

## what did I learn

I had to research a lot, and I learned many things about Java, in particular about bitwise operations. I also learned to split methods into smaller methods, which makes it easier to grasp the structure of longer algorithms, like those in the Huffman class.

## what was unclear/difficult

The most difficult thing was probably the BitSequence class; it was difficult to design, code and debug. Also, I still haven't done much debugging on the Huffman compression and decompression algorithms, but the fact that the vast majority of the methods in the Huffman class are private will probably make it difficult to debug it one piece at a time.

## what will I do next

I will write more unit tests, debug the Huffman algorithm, update some Javadoc and hopefully start working on another compression algorithm.
