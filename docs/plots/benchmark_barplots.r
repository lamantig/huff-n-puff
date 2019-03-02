algor_names <- c("Huffman", "LZW12", "LZW16", "LZW20")
algor_colors <- c("deepskyblue", "springgreen", "springgreen3", "springgreen4")
corpus_names <- c("CalL", "CalS", "CanL", "CanS", "Luk", "Pro", "Sil")
smaller_ones <- c(1, 2, 3, 4, 6)
bigger_ones <- c(5, 7)

# settings margins for all plots
par(mar=c(3, 5, 3, 1))
# each plot's parameters have been tuned to a plot size of 640x360 pixels
# so to get the same results just export them as PNG files
# with width 640 and height 360 (pixels)



bps_calg_l <- c(4.501, 3.890, 3.248, 3.151)
bps_calg_s <- c(4.488, 3.874, 3.223, 3.123)
bps_cant_l <- c(3.539, 3.296, 2.631, 2.342)
bps_cant_s <- c(3.740, 2.829, 2.574, 2.569)
bps_luk <- c(5.572, 4.384, 3.743, 3.510)
bps_pro <- c(4.173, 5.154, 4.751, 4.617)
bps_sil <- c(5.217, 4.050, 3.345, 2.942)

bps <- matrix(c(bps_calg_l, bps_calg_s, bps_cant_l, bps_cant_s,
                bps_luk, bps_pro, bps_sil), ncol=7)
rownames(bps) <- algor_names
colnames(bps) <- corpus_names

barplot(height=bps, legend.text=T, beside=T, col=algor_colors,
        main="compression rate", ylab="bits per symbol (bps)",
        ylim=c(0, 8), args.legend=list(x=35, y=8.7))



ctm_calg_l <- c(347081445, 3472885883, 4404749444, 4765486501)
ctm_calg_s <- c(331513768, 3419079035, 4327180645, 4702157423)
ctm_cant_l <- c(1022882184, 7322812408, 9821283201, 16613391828)
ctm_cant_s <- c(267318524, 3096753708, 3991482899, 4159156953)
ctm_luk <- c(47387862877, 488337428960, 1032746336318, 1880838725188)
ctm_pro <- c(629386551, 4741837070, 5765694922, 11439094243)
ctm_sil <- c(22483464204, 159180468397, 233765949034, 456925237007)

# since compression times are in nanoseconds, and each file of each corpus has
# been compressed 10 times, by dividing by 1e10 we get the average time (in
# seconds) it took to compress all the files of a given corpus
ctm <- matrix(c(ctm_calg_l, ctm_calg_s, ctm_cant_l, ctm_cant_s,
                ctm_luk, ctm_pro, ctm_sil), ncol=7) / 1e10
rownames(ctm) <- algor_names
colnames(ctm) <- corpus_names

barplot(height=ctm[, smaller_ones], legend.text=T, beside=T,
        col=algor_colors, main=("compression time (smaller file sizes)"),
        ylab="seconds", ylim=c(0, 2), args.legend=list(x=8, y=2))
barplot(height=ctm[, bigger_ones], legend.text=T, beside=T,
        col=algor_colors, main=("compression time (bigger file sizes)"),
        ylab="seconds", ylim=c(0, 200), args.legend=list(x=10, y=200))



dtm_calg_l <- c(761192640, 698031969, 582089200, 640521047)
dtm_calg_s <- c(735718738, 691780356, 554099475, 602372496)
dtm_cant_l <- c(2028125656, 2058836989, 1632597126, 2134893197)
dtm_cant_s <- c(461432334, 465476982, 392455399, 409230578)
dtm_luk <- c(91376601892, 107318938418, 86926328217, 106802175702)
dtm_pro <- c(1511396537, 1800256831, 1560486641, 2319383214)
dtm_sil <- c(46980730514, 46624876720, 35829187494, 46081607784)

# since decompression times are in nanoseconds, and each file of each corpus has
# been decompressed 10 times, by dividing by 1e10 we get the average time (in
# seconds) it took to decompress all the files of a given corpus
dtm <- matrix(c(dtm_calg_l, dtm_calg_s, dtm_cant_l, dtm_cant_s,
                dtm_luk, dtm_pro, dtm_sil), ncol=7) / 1e10
rownames(dtm) <- algor_names
colnames(dtm) <- corpus_names

barplot(height=dtm[, smaller_ones], legend.text=T, beside=T,
        col=algor_colors, main=("decompression time (smaller file sizes)"),
        ylab="seconds", ylim=c(0, 0.25), args.legend=list(x=8, y=0.25))
barplot(height=dtm[, bigger_ones], legend.text=T, beside=T,
        col=algor_colors, main=("decompression time (bigger file sizes)"),
        ylab="seconds", ylim=c(0, 12), args.legend=list(x=10, y=12))
