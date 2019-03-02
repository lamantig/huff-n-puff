# software requirements specification

## what algorithms and data structures will I implement?

I'll implement the [Huffman coding](https://en.wikipedia.org/wiki/Huffman_coding) algorithm and one of the algorithms based on the [LZ77 and LZ78](https://en.wikipedia.org/wiki/LZ77_and_LZ78) algorithms. If there's enough time left, I'll implement also the [Burrows–Wheeler transform](https://en.wikipedia.org/wiki/Burrows–Wheeler_transform).

## what problem am I trying to solve and why did I choose these algorithms and data structures?

All the aforementioned algorithms try to solve the problem of [lossless data compression](https://en.wikipedia.org/wiki/Lossless_compression). The Huffman coding is one of the oldest and most influential algorithms for such purpose; it's part of the [entropy encoding](https://en.wikipedia.org/wiki/Entropy_encoding) category. The Lempel-Ziv family of algorithms is also widely used (for example LZMA is used by [7-Zip](https://en.wikipedia.org/wiki/7-Zip)); they are part of the [dictionary encoding](https://en.wikipedia.org/wiki/Dictionary_coder) category. The Burrows–Wheeler transform is used to prepare data for use with other compression techniques; it is also widely used (for example in [bzip2](https://en.wikipedia.org/wiki/Bzip2)).

## what's the app's input and how is it used?

The app has a command-line textual user interface. The app's input can be a file of any type, which will be compressed with the implemented algorithms. Files compressed with this app can then be used as input again, either to be decompressed, or to be further compressed with another (or the same) algorithm; when compressing already compressed files there probably won't be a significant size reduction. Some statistics about each algorithm's performance will be calculated and displayed to the user (for example file size reduction and time elapsed during compression/decompression).

## desired time and space complexities (including big-O analysis)

According to the Wikipedia article on [Huffman coding](https://en.wikipedia.org/wiki/Huffman_coding), a Huffman tree can be built in O(n) time, with an algorithm that uses two queues and a list of symbols sorted by their weight. So, using merge sort to sort the symbols, the total time complexity for building the tree will be O(n log n) (because it's the time complexity of merge sort). Anyway this time complexity is not particularly interesting, because n is just the size of the original alphabet, which in most implementations, including mine, is always the same and very small (256, the number of possible values of an unsigned byte). So while comparing these compression algorithms it's more interesting to look at compression rates and compression/decompression times.

## sources

<https://en.wikipedia.org/wiki/Huffman_coding>

<https://en.wikipedia.org/wiki/LZ77_and_LZ78>

<https://en.wikipedia.org/wiki/Burrows–Wheeler_transform>

<https://en.wikipedia.org/wiki/Lossless_compression>

<https://en.wikipedia.org/wiki/Entropy_encoding>

<https://en.wikipedia.org/wiki/7-Zip>

<https://en.wikipedia.org/wiki/Dictionary_coder>

<https://en.wikipedia.org/wiki/Bzip2>

<https://www.cs.helsinki.fi/courses/582487/2015/K/K/1>
