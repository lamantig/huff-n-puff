# software implementation documentation

## app's general structure

The app has a command-line interface, through which the following operation can be performed: compression, decompression and algorithm comparison. For more information about the CLI see the [user guide](https://github.com/nigoshh/huff-n-puff/blob/master/docs/user-guide.md).

## Huffman

The HUFF n PUFF implementation of the Huffman algorithm was developed after reading the Wikipedia articles about [Huffman coding](https://en.wikipedia.org/wiki/Huffman_coding) and [canonical Huffman code](https://en.wikipedia.org/wiki/Canonical_Huffman_code). First a Huffman tree is built using a variation of the Huffman algorithm (from [this paper](http://www.staff.science.uu.nl/~leeuw112/huffman.pdf), as referenced by the Wikipedia article) that uses two queues and a list containing the symbols sorted by weight. The tree is then converted to a canonical tree, so that it can be stored more compactly in the compressed file. The biggest challenge was coding the BitSequence class (used also by LZW), which is necessary for storing and appending bit sequences. For more technical details see the Huffman class's [Javadoc](https://nigoshh.github.io/huff-n-puff/javadoc/).

## LZW

The HUFF n PUFF implementation of variable-length LZW was developed after reading the Wikipedia article about [Lempel–Ziv–Welch](https://en.wikipedia.org/wiki/Lempel%E2%80%93Ziv%E2%80%93Welch) and many other sources, listed [at the bottom of this page](https://github.com/nigoshh/huff-n-puff/blob/master/docs/software-implementation-documentation.md#sources), including several code examples like [this by Juha Nieminen](http://warp.povusers.org/EfficientLZW/index.html) and [these from Rosetta Code](https://rosettacode.org/wiki/LZW_compression). In particular [this explanation by Steve Blackstock](https://www.cs.cmu.edu/~cil/lzw.and.gif.txt) helped me understand how to handle codeword length changes and dictionary resets during decompression.

The HUFF n PUFF implementation of LZW is substantially different from all the code examples listed as sources. No single example was taken as a model, and some features were not present in any of the examples I found. Notably, the few examples that used variable-length codewords and/or dictionary resets all used reserved codewords to mark dictionary resets or other special circumstances (like end of data) while the HUFF n PUFF implementation doesn't use any reserved codeword, since they're not actually needed if the dictionary is reset only when it becomes full.

The dictionary is implemented as a hash table. The hash function is a [rolling hash](https://en.wikipedia.org/wiki/Rolling_hash#Polynomial_rolling_hash), developed after reading from various sources (listed below). Perhaps the most interesting thing about LZWDictionary is that it caches hash values. Since usually the next sequence (dictionary key) that will be searched is equal to the previous one, plus one symbol, and since the hash function is a rolling hash, by caching the last hash value the new one can be obtained just by multiplying the cached hash by the hash factor and adding the new symbol (instead of performing this operation again for all the symbols in the sequence).

## performance and comparison

As can be seen in the [software test documentation](https://github.com/nigoshh/huff-n-puff/blob/master/docs/software-test-documentation.md#performance-testing), Both algorithms perform well in terms of compression rate, achieving values as low as 3 bits per symbol. LZW is painfully slow during compression. For more info about performance and comparison see the paragraph about [performance testing](https://github.com/nigoshh/huff-n-puff/blob/master/docs/software-test-documentation.md#performance-testing) in the software test documentation.

## shortcomings and room for improvement

Probably the reason why the LZW dictionary implementation is somewhat slow has something to do with the fact that each sequence is saved separately, while they could be linked together, as pointed out in [this implementation by Juha Nieminen](http://warp.povusers.org/EfficientLZW/part2.html).

The LZW implementation could be further optimized by monitoring the compression efficiency while encoding, resetting the dictionary whenever it does no longer match the input well, as pointed out by [the Wikipedia article](https://en.wikipedia.org/wiki/Lempel%E2%80%93Ziv%E2%80%93Welch).

## sources

<https://en.wikipedia.org/wiki/Huffman_coding>

<https://en.wikipedia.org/wiki/Canonical_Huffman_code>

<http://www.staff.science.uu.nl/~leeuw112/huffman.pdf>

<https://en.wikipedia.org/wiki/Lempel%E2%80%93Ziv%E2%80%93Welch>

<https://www.geeksforgeeks.org/lzw-lempel-ziv-welch-compression-technique/>

<http://web.mit.edu/6.02/www/s2012/handouts/3.pdf>

<https://marknelson.us/posts/2011/11/08/lzw-revisited.html>

<https://algs4.cs.princeton.edu/lectures/55DataCompression-2x2.pdf>

<https://rosettacode.org/wiki/LZW_compression>

<http://warp.povusers.org/EfficientLZW/index.html>

<https://www.cs.cmu.edu/~cil/lzw.and.gif.txt>

<https://en.wikipedia.org/wiki/Rolling_hash#Polynomial_rolling_hash>

<https://www.cs.helsinki.fi/group/tirapaja/k18/vk5_huomioita.html>

<https://cp-algorithms.com/string/string-hashing.html>
