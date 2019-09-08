# Project - *Craigslist_App*
Warning: Do not remove any folder in the current directory. It may cause errors.

Main_Command   -- run as command lines
Main           -- run as GUI (tested by Java 8)

Command Line:
-i <Filename.txt>     -- load file that contains urls
-o <TargerFilename.txt>    -- output the name of the local url file that pointed by target file  
-p <input file name> <output file name>    -- directly input and output without saving data and terminate immediately 
-data            -- display a list of stored data
-users            -- display a list of users
-servers         -- display a list of available servers
-searchtype        -- display a list of search options
-rebuild        -- rebuild data
-q            -- quit program

Input file formats:
serverCity=<name server>;searchType=<name options>;search=<search terms>;limit=<number of results>
Note: multiple lines of searching are allowed.
Note: serverCity and searchType can be empty.

GUI Demo Showcase:
<img src='https://github.com/lxy878/week2/blob/master/FlixApp.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

