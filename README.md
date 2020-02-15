# ImageScale - A tool for Batch converting Images

## Information

Was created to convert Images on the fly for using them on Websites.
Done in JavaFX for learning JavaFX. You can also use other Tools like Gimp, Photoshop etc.

## How to work with it
1. All Configuration is made in config.xml
2. All Images for converting are in \<image-dir\>
3. A Backup of the original File is made in \<backup-dir\>
4. \<max\> is the maximum dimension of the biggest side of the image
5. Thumbnails are created in \<thumb-dir\> with max Size \<thumb\> pixels


### The Format of config.xml
```
<config>
  <thumb>600</thumb>
  <max>1024</max>
  <image-dir>_Bilder</image-dir>
  <backup-dir>_Originale</backup-dir>
  <thumb-dir>_thumbs</thumb-dir>
</config>
```

## Finally
This software can be improved. Right now its working.
So it is provided as it is. *Enjoy*

##Copyright (C) 2020 Mag. Stefan Hagmann

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
