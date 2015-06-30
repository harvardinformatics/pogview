pogview
=======

Genome scale multiple alignment tool


For distribution

tar cvf  - jars/pogvue.jar pogview README.md perl/Maf2.pm perl/index_maf perl/get_maf.pl data/strCam.* maf data/ratites.nh|gzip -c > pogview.tgz

1. tar zxvf pogview.tgz

2. cd pogview

3. Copy your maf files into the maf directory (They’re sitting in /scratch/mclamp/ratites/ on sandy2)

4. Index your maf files

      cd  perl;  perl ./index_maf

You should see lines like 

    Align ../maf/strCam.superscaffold21.maf ../maf/strCam.superscaffold21.maf.index

Check the index file isn’t empty

    ls -l ../maf

5. Back to the top dir

   cd ..

6. Test you can fetch the alignment pieces

    perl perl/get_maf.pl strCam.superscaffold21 1 1000

7.  Run pogview

    ./pogview data/strCam.GeneWise.gff

I’m getting out of memory errors if I load up all the exons - maybe need to filter them.


Karyotype view keystrokes

ctrl left click - zoom in
ctrl right click - zoom out

click - select features

Click the pogvue button to bring up the alignment window

With the mouse in the bottom window click s to fetch the sequence (keep an eye on the terminal window for wierdness)

Other keys

arrow keys <- ->  scroll left and right
click drag to scroll too

right clikc in top window to move the displayed region in the bottom window

i - increase track height
d - decrease track height
f - base level view
+ - zoom in
- - zoom out

`,1,2,3,4 are different levels of zoom.

y - brings up tree

There are many other display options that aren’t hooked up yet.


ctrl-F is a find box but this is uber flaky

The display is a bit effed on my machine (I haven’t run this since 2009 or so) so I use i or d to refresh the screen.

It may be too buggy to use for too long in its current state.  It needs some TLC.

