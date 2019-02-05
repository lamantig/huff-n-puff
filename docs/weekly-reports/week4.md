# week 4

[weekly timesheet](https://github.com/nigoshh/huff-n-puff/blob/master/documentation/timesheet.md#week-4)

## what have I done

I have completed (and tested) the Huffman compression and decompression algorithm. I have implemented all data structures (BitSequence, ArrayQueue, TreeRepresentation) and all utility algorithms (mergeSort, arrayCopy, fill) needed for it. Of these new classes, only Utils and BitSequence have been tested thoroughly with unit tests. I have also refactored ui classes without Map and HashMap dependencies.

## how has the app improved

The app has improved drastically, because its main functionality (file compression and decompression) now works for the first time. Also many of the

## what did I learn

I learned the importance of unit tests. I thought that the BitSequence class was working correctly, because after I had tested it thoroughly I only added a couple of new methods, and tested them superficially. Then when I was debugging the Huffman compression and decompression, after a while I noticed that actually the problem was in one of the BitSequence methods I hadn't tested thoroughly.

## what was unclear/difficult

It was difficult to debug (and test) the BitSequence class, in particular its method nextSequence.

## what will I do next

I've already started researching some compression algorithms of the LZ family. I will implement the LZW, which will require writing my own implementation of some new data structures (a hash table or a trie). I'll also start some performance testing. I also have to update Javadoc in the Huffman class.
