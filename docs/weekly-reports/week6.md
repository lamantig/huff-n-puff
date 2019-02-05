# week 6

[weekly timesheet](https://github.com/nigoshh/huff-n-puff/blob/master/documentation/timesheet.md#week-6)

## what have I done

I've implemented the Compare functionality, which can be used (with various data compression corpora) as a benchmark for the two compression algorithms. I've made faster versions of two methods in BitSequence (append(int) and readNextInt). I've also made my own Dictionary implementation.

## how has the app improved

With the Compare functionality ready, the only thing left for performance testing is to run Compare's thorough mode with some data compression corpora, and plot the obtained data using R. The improvements in BitSequence and the introduction of LZWDictionary also made LZW faster (or less slow, at least).

## what did I learn

I learned more about refactoring; in Compare I had two versions of computeStats, one for simple mode and the other one for thorough mode; I then realized that I could use just one method for both of them.

## what was unclear/difficult

Once again, the most difficult thing was probably coding and debugging the new version of BitSequence. LZWDictionary wasn't as hard as that, but it was still challenging, because it's not just a basic hash table implementation (it has some sort of caching of hash values, see the software implementation documentation for more info)

## what will I do next

I will have a last shot at trying to implement variable-length LZW. Then I will write more unit tests, Javadoc, finish the rest of the documentation, make a release and celebrate I guess!
