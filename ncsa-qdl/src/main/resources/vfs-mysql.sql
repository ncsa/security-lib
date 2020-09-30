/*
  Create a database for the mysql-backed virtual file system.
  This stores the path (always start at the root /) and name separately
  so they can be indexed more efficiently for things like showing a directory

  Note the sizes of the path and name add up to 1024. This is the maximum key size allowed.
  The whole database must be either UTF-8 or not and MySQL restricts the total primary key
  size to 3072 bytes = 1024 * 3, since each UTF-8 character is 3 bytes long.
  To get longer paths means we'd have to turn the whole database into another character set
  (like latin 1) but that also means we can't store arbitrary text without encoding/decoding it.

  The actual contents are
  ea = extended attributes. So all the accounting information.
  content = the contents of the file as a text blob.
  Just put this in the right database and scheme as needed.
  
  Be SURE you set the grant to the right user or there will be no access to this database!
  You may need to create the user for this database too, but that is not part of this script.
 */
 /*
  For older MariaDB abd MySQL: The total key length is 767 bytes/3 = 255 unicode chars.
 */
  */
create table qdl_vfs(
   path VARCHAR(191),
   name VARCHAR(64),
   ea TEXT,
   content TEXT,
   PRIMARY KEY(path, name)
   );


/*
  For mariaDB, the total key length is as per above

 */

 create table qdl_vfs(
    path VARCHAR(511),
    name VARCHAR(256),
    ea TEXT,
    content TEXT,
    PRIMARY KEY(path, name)
    );

GRANT All  ON qdl_vfs TO 'user'@'server';