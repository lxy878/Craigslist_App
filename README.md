# Project - *Craigslist_App*

## Purpose:

A practic of fetching contexts from web pages by using Regex and Java libraries and designing GUIs by using Javafx and FXML.

### Overview:
- Developed a Java application that follows users searching information from craigslist.org.
- Built a persistent data storage by implementing Hashtable with txt files and jpg files.
- Designed GUIs and command lines that allow users/admin searching and maintaining their data.

## Description:

### Process of Searching/Collecting data:

- Enter a search term and create a query with the target website (Craigslist.org).
- send the created URL out as request and retrieve web document (html) of the result page.
- collect result URLs from the result page by using Regex.
- send each result URL out and retrieve html of the context page.
- record collecting terms such as title, image, description and so on by using Regex.
- store collecting terms to hashTable data structure and txt files as a local storage.

### GUIs:

- User Registration:  a user account system allows new users to create their account and to log in their accounts by names and passwords.
- User Account: 
    1. allows directly to search target terms from GUI to Craigslist.org.
    2. allows offline search from the local storage.
    3. provides users' searching history, that can be edited and deleted their data from the storage,
- Admin Account: 
    1. allows directly to search target terms from GUI to Craigslist.org.
    2. allows offline search data of all/some users' data.
    3. rebuilds the system from backup data.
- Online search:  allows users to obtain data and a image through GUI and persist the data on the local storage.  
- Offline search: allows users to search terms through the local storage.  For user accounts, the program will conduct offline queries via a GUI and retrieve data from the storage.  For the admin account, it not only has the same offline method as the user accounts but with an additional option to filter one/more/all users.

### Access:
- Command Line.  The program is allowed the command lines.  See testing section.
- GUI.  [See User Manual](https://github.com/lxy878/Craigslist_App/blob/master/UserManual.pdf).

### Other features:
- The users have the option to limit the number of searching results.
- The user can an advanced search, that chooses attributes of cities and categories.

## Simple Flow Chart:

<img src='https://github.com/lxy878/Craigslist_App/blob/master/DemoJavaApp.gif' title='Flow Chart' width='400' height='300' alt='Flow Chart' />

## Testing:

Warning: Do not remove any folder in the current directory. It may cause errors.

Main_Command   -- run as command lines<br>
Main           -- run as GUI (tested by Java 8)

**Command Line:**

-i <Filename.txt>     -- load file that contains urls<br>
-o <TargerFilename.txt>    -- output the name of the local url file that pointed by target file <br>
-p <input file name> <output file name>    -- directly input and output without saving data and terminate immediately <br>
-data            -- display a list of stored data<br>
-users            -- display a list of users<br>
-servers         -- display a list of available servers<br>
-searchtype        -- display a list of search options<br>
-rebuild        -- rebuild data<br>
-q            -- quit program<br>


**Input file formats:**

serverCity=**name server**;searchType=**name options**;search=**search terms**;limit=**number of results**

Note: multiple lines of searching are allowed.<br>
Note: serverCity and searchType can be empty.

## GUI Demo Showcase:

<img src='https://github.com/lxy878/JavaApplication/blob/master/DemoJavaApp.gif' title='Video Walkthrough' width='400' height='300' alt='Video Walkthrough' />
