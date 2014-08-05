#!/bin/bash


infile=res/drawable-xxxhdpi/$1
inW=$(identify -ping -format '%W' ${infile})
inH=$(identify -ping -format '%H' ${infile})

xxhdpiW=$(bc <<< $inW*0.75)
xxhdpiH=$(bc <<< $inH*0.75)

xhdpiW=$(bc <<< $inW*0.5)
xhdpiH=$(bc <<< $inH*0.5)

hdpiW=$(bc <<< $inW*0.375)
hdpiH=$(bc <<< $inH*0.375)

mdpiW=$(bc <<< $inW*0.25)
mdpiH=$(bc <<< $inH*0.25)

ldpiW=$(bc <<< $inW*0.1875)
ldpiH=$(bc <<< $inH*0.1875)

convert -resize $(printf '%.0f' ${xxhdpiW})x$(printf '%.0f' ${xxhdpiH})  res/drawable-xxxhdpi/$1 res/drawable-xxhdpi/$1

convert -resize $(printf '%.0f' ${xhdpiW})x$(printf '%.0f' ${xhdpiH})  res/drawable-xxxhdpi/$1 res/drawable-xhdpi/$1

convert -resize $(printf '%.0f' ${hdpiW})x$(printf '%.0f' ${hdpiH})  res/drawable-xxxhdpi/$1 res/drawable-hdpi/$1

convert -resize $(printf '%.0f' ${hdpiW})x$(printf '%.0f' ${hdpiH})  res/drawable-xxxhdpi/$1 res/drawable/$1

convert -resize $(printf '%.0f' ${mdpiW})x$(printf '%.0f' ${mdpiH})  res/drawable-xxxhdpi/$1 res/drawable-mdpi/$1

convert -resize $(printf '%.0f' ${ldpiW})x$(printf '%.0f' ${ldpiH})  res/drawable-xxxhdpi/$1 res/drawable-ldpi/$1
