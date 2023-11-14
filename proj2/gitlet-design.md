# Gitlet Design Document

**Name**:Gguo

## Classes and Data Structures

### Blob

#### Instance Variables
1. refs-blobID
2. content-the file's content in type of byte[]

#### Method
1. Initiate method<br>
   First get content & refs from file  `public Blob(File file)`<br>
   Get content by exist BLOBid`public Blob(String SHA)`
2. Get method<br>
    ...
3. Save blob<br>`public void saveblob()`

### staging
#### Instance Variables
1. trackedTree
2. add
#### Method
1. Intiate method<br>`public static staging fromFile()`
2. Get<br>...
3. addstage<br>Take in File and check if it's need to be add in stage`public boolean addstage(File file)`
4. Clear<br>Delete all in `add`



### Commit
#### Instance Variables
* Message - contains the message of a commit.
* Timestamp - time at which a commit was created. Assigned by the constructor.
* Parent - the parent commit of a commit object.

#### Fields

1. Field 1
2. Field 2


### Class 2

#### Fields

1. Field 1
2. Field 2


## Algorithms

## Persistence
