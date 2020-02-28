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

 */
create table qdl_vfs(
   path VARCHAR(768),
   name VARCHAR(256),
   ea TEXT,
   content TEXT,
   PRIMARY KEY(path, name)
   );