# timesheet

Total time (hours:minutes): 198:30.

## week 1

|             date |  time | tasks |
| ---------------: | ----: | :---- |
|       2018-12-20 |  2:00 | research (possible project subjects) |
|       2018-12-21 |  4:00 | <span>research (possible project subjects)<br>GitHub repo<br>Gradle init<br>Checkstyle</span> |
|       2018-12-22 |  4:00 | <span>Travis<br>Codecov<br>Codacy<br>documentation skeleton<br>research (compression algorithms)<br>week 1 report<br>software requirements specification<br>timesheet</span> |
| **weekly total** | 10:00 | お疲れ様ぜぇ～！ |

## week 2

|             date |  time | tasks    |
| ---------------: | ----: | :------- |
|       2018-12-23 |  5:45 | <span>research (Huffman coding)<br>fix Codacy issues (markdown)<br>object-oriented and algorithm design<br>created some classes</span> |
|       2018-12-24 |  3:00 | TUI skeleton |
|       2018-12-26 |  3:40 | <span>research (Java language)<br>Compress class skeleton<br>Huffman class skeleton</span> |
|       2018-12-27 |  6:35 | <span>Compress class ready<br>Huffman class skeleton</span> |
|       2018-12-28 |  2:30 | <span>Decompress class ready<br>ui classes refactored</span> |
|       2018-12-29 |  7:30 | <span>research (Java, ways to read bytes from files)<br>Huffman compression algorithm (not ready yet)<br>week 2 report</span> |
| **weekly total** | 29:00 | お疲れ様ぜぇ～！ |

## week 3

|             date |  time | tasks    |
| ---------------: | ----: | :------- |
|       2018-12-30 |  4:45 | <span>research (Java, BitSet and bitwise operations)<br>BitSequence class</span> |
|       2018-12-31 |  9:30 | <span>research (Java, ways to read file to byte[] and long[])<br>BitSequence class<br>BitSequenceTest and debug</span> |
|       2019-01-01 |  6:10 | <span>BitSequenceTest and debug<br>upgraded dependency to junit5 (jupiter)</span> |
|       2019-01-02 |  4:15 | <span>Checkstyle suppressions<br>BitSequence class<br>Huffman class</span> |
|       2019-01-03 |  6:30 | <span>Huffman class<br>BitSequence.nextSequence()</span> |
|       2019-01-04 |  8:40 | <span>Huffman class<br>TreeRepresentation class</span> |
| **weekly total** | 39:50 | お疲れ様ぜぇ～！ |

## week 4

|             date |  time | tasks    |
| ---------------: | ----: | :------- |
|       2019-01-06 |  3:40 | Huffman and BitSequence debugging |
|       2019-01-07 |  8:00 | <span>Huffman and BitSequence debugging<br>BitSequence refactoring<br>BitSequenceTest<br>HuffmanTest<br>written some Javadoc</span> |
|       2019-01-08 | 10:30 | <span>research (LZ77, LZ78, LZW)<br>handled special case in TreeRepresentation<br>refactored ui without Map and HashMap dependencies<br>merge sort implementation, removed Arrays.sort<br>written some Javadoc</span> |
|       2019-01-09 |  5:15 | <span>week 4 report and timesheet<br>research (performance testing)<br>substituted Arrays.fill and System.arraycopy with own implementations<br>made some unit tests for Utils class<br>made test text file for HuffmanTest and pushed it to GitHub<br>updated software requirements specification<br>started software implementation documentation<br>started software test documentation<br>written some Javadoc</span> |
| **weekly total** | 27:25 | お疲れ様ぜぇ～！ |

## week 5

|             date |  time | tasks    |
| ---------------: | ----: | :------- |
|       2019-01-10 |  8:20 | <span>research (LZW)<br>new data structure classes for LZW<br>refactored CommandUtils (removed Map and HashMap dependencies)</span> |
|       2019-01-11 |  3:05 | worked on LZW and related classes (ByteSequence, LZWSequence) |
|       2019-01-13 |  2:15 | <span>LZW debugging<br>got rid of LZWSequence, made new methods in BitSequence instead</span> |
|       2019-01-14 |  5:35 | <span>CompressionAlgorithmTest<br>tried (unsuccessfully) to make LZW version with variable-length codewords (9 to 12 bits)<br>research (LZW, variable-length codewords)<br>week 5 report and timesheet</span> |
| **weekly total** | 19:15 | お疲れ様ぜぇ～！ |

## week 6

|             date |  time | tasks    |
| ---------------: | ----: | :------- |
|       2019-01-15 |  1:20 | testing extreme cases of fixed-length LZW |
|       2019-01-16 |  6:50 | <span>thinking about variable-length LZW dictionary reset<br>Compare Command: simple mode<br>Compare Command: thorough mode (for benchmarks)</span> |
|       2019-01-18 |  3:55 | Compare Command: thorough mode and refactoring |
|       2019-01-19 |  3:50 | BitSequence: faster append(int) and readNextInt |
|       2019-01-20 |  2:40 | <span>BitSequence: faster readNextInt<br>LZWDictionary and LZWDictEntry<br>research (Java generic array creation and warnings)</span> |
|       2019-01-21 |  3:55 | <span>LZWDictionaryTest and debugging<br>substituted LZWDictionary to JavasDict in LZW (slight improvement in speed!)<br>testing fixed-length LZW extreme case (long file whose bytes have all the same value)<br>week 6 report and timesheet</span> |
| **weekly total** | 22:30 | お疲れ様ぜぇ～！ |

## after week 6

|             date |  time | tasks    |
| ---------------: | ----: | :------- |
|       2019-01-22 |  8:45 | <span>LZW debugging<br>working implementation of variable-length LZW<br>CompressionAlgorithmTest<br>cleaning up (removing unused code)</span> |
|       2019-01-23 |  4:10 | <span>refactor Stats and related methods (out of Compare into own package)<br>cleaning up (removing unused code)<br>unit tests</span> |
|       2019-01-24 |  3:00 | <span>unit tests<br>BitSequence debugging<br>added support for different maximum codeword lengths</span> |
|       2019-01-25 |  2:30 | <span>unit tests<br>fixed compiler warnings<br>HuffNodeTest<br>TreeRepresentationTest</span> |
|       2019-01-26 |  3:05 | testing and debugging TreeRepresentation (buildLeafNodes method) |
|       2019-01-27 |  3:05 | <span>cleaning up code<br>writing Javadoc</span> |
|       2019-01-28 |  0:40 | research on publishing Javadoc |
|       2019-01-29 |  3:05 | <span>writing Javadoc (Huffman and LZW)<br>experimenting with different hash table sizes<br>minor fix for overflow in hashCode</span> |
|       2019-01-30 |  0:50 | <span>writing Javadoc<br>researching GitHub Pages</span> |
|       2019-01-31 |  2:55 | <span>writing Javadoc<br>made LZW customizable (hashTableSize and maxCodewordLength)</span> |
|       2019-02-04 |  2:35 | writing Javadoc |
|       2019-02-05 |  2:00 | <span>writing Javadoc<br>publishing Javadoc<br>thinking about changes in Stats (compression rate in bps)</span> |
|       2019-02-06 |  2:30 | <span>added bits per symbol to Stats and Compare<br>written R script for drawing plots</span> |
|       2019-02-08 |  0:50 | benchmark testing and making plots |
|       2019-02-09 |  2:05 | benchmark testing and making plots |
|       2019-02-17 |  1:20 | writing user guide |
|       2019-02-18 |  1:45 | <span>writing user guide<br>correcting software requirements specification</span> |
|       2019-02-26 |  0:55 | writing software test documentation |
|       2019-03-01 |  3:05 | writing software test documentation and software implementation documentation |
|       2019-03-02 |  1:20 | <span>finishing up documentation and timesheet<br>release</span> |
| **period total** | 50:30 | お疲れ様ぜぇ～！ |
