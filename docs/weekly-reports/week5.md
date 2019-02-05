# week 5

[weekly timesheet](https://github.com/nigoshh/huff-n-puff/blob/master/docs/timesheet.md#week-5)

## what have I done

I somehow managed to code a (seemingly) working version of LZW, with fixed-length codewords (12 bits) and dictionary resets. I had to make some new data structures for that: ByteSequence (to represent a list of bytes) and Dictionary, which now has only an implementation that simply wraps Java's HashMap. I have already planned how to implement a hash table myself, so that shouldn't take long. I

## how has the app improved

The app has now two (seemingly) working compression algorithms, so for the first time it is possible to compare their performances. This can be done manually by checking the compressed files' sizes, but I intend to develop the Compare command of the ui.

## what did I learn

I learned that it's way better (and easier) to modify and reuse old (tested) code, instead of writing new code.

When working on the LZW algorithm I initially made a new class, LZWSequence, which was supposed to handle 12-bit fixed-length codeword sequences faster than BitSequence would have. Then when I started debugging LZW, I noticed most of the problems where in LZWSequence, so I was going to write a lot of unit tests to debug LZWSequence.

But then I thought: maybe I should just write a couple of new methods for BitSequence, which is already thoroughly tested. These new methods could simply use older methods, even though that's slower; for example appending a 12-bit codeword to a bit sequence bit-by-bit requires 12 bit-shift operations, while a faster version could do it with just 3. Once I get the whole LZW algorithm working I can start to consider writing faster versions of the new methods in BitSequence.

This second approach worked, so I got rid of LZWSequence.

## what was unclear/difficult

The most difficult thing (which I haven't yet solved) was trying to modify the basic LZW so that variable-length codewords and dictionary resets are employed. I found many good pseudocode versions for the basic LZW algorithm; in some of them it's mentioned how to implement variable-length codewords and dictionary resets, but there isn't any pseudocode to show precisely how it should be done. So I tried many approaches, but so far none worked. Now I've found a version coded in C++, but so far I haven't deciphered it enough to be able to modify my own code in the right places.

## what will I do next

I will try to implement variable-length codewords and dictionary resets in LZW, by using pencil and paper and thinking about the extreme cases (like when the dictionary is reset). I suspect that in some of the most extreme cases (codeword not in dictionary while decoding, at the same time as a dictionary reset) could fail even with the fixed-length codeword version I already have.

I will update the Compare command, and use it to implement performance testing on some standard corpora for data compression. I will make more unit tests.

I will make my own dictionary implementation (a hash table).
