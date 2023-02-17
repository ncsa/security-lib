
# archive

Command. <br>
Create and manage a copy of an object. This is stored with a version number appended to the <br>
identifier.

# commands

A complete list of commands supported by this component. <br>
 <br>
Syntax: <br>
/commands.

# copy

Command. <br>
Create a copy of an object, assigning it a new id.

# create

Command. <br>
Creates a new object in the store. You will then be prompted to fill in each attribute for the <br>
object in turn.

# decode

Command. <br>
Takes a string encoded in base 64 or base 32 and decodes it. <br>
 <br>
See also: encode

# description

Every object can have a description. Note that the description <br>
is intentionally really simple -- it is just a string. No format, no structure. <br>
No processing of any sort is done on it. <br>
 <br>
If you want to get a synopsis of what the main topic is, you should read the <br>
"about" entry. <br>
  

# deserialize

Command. <br>
Takes the stored XML version of the object and reads it into the current store. <br>
 <br>
See also: serialize

# edit

Command. <br>
Edit the current object's serialized form in an external editor (usually vim or nano). <br>
Exiting the editor updates the object.

# encode

Command. <br>
Takes a given string and encodes it into base 32 or 64 as per request. <br>
 <br>
See also: decode

# help

How to use help. Generally you may get help on a topic by <br>
1. Invoking <br>
  /help topic <br>
  at the command line. <br>
 <br>
2. Commands have built in help. So if /help lists an entry as a "Command." then <br>
   you may get help by issuing <br>
 <br>
command --help <br>
 <br>
E.g. <br>
archive --help <br>
 <br>
   The command should always know its help and should always be the most recent and <br>
   authoritative information available. <br>
 <br>
3. You may always get a list of properties by invoking the list_keys command <br>
   and the full set of commands can be found by typing /commands <br>
 <br>
4. In creating an object. If you are creating an object and filling in the <br>
   values, you may type "/help" (no quotes) at the prompt and print out the <br>
   help for that topic. E.g. here is a part of a session for creating an admin client. <br>
   in it, the user requests help for the id, by entering "/help", the help entry <br>
   is printed and the user is then prompted again: <br>
 <br>
admins>create <br>
enter the id of the object you want to create or return for a random one > <br>
    Object created with identifier oa4mp:/adminClient/4cae958bbc281ac36ea44caee5814e6d/1669988296009 <br>
edit [y/n]?y <br>
  Update the values. A return accepts the existing or default value in []'s <br>
enter the identifier[oa4mp:/adminClient/4cae958bbc281ac36ea44caee5814e6d/1669988296009]:/help <br>
Property: Identifier (**) <br>
The unique identifier for this administrative client. <br>
enter the identifier[oa4mp:/adminClient/4cae958bbc281ac36ea44caee5814e6d/1669988296009]: <br>
 <br>
Note that this works at creation only. <br>


# id

You may set an identifier for use in the current session and this will <br>
be used to reference any object. <br>
 <br>
See also: clear_id, get_id

# identifier

Every object (transaction, client, approval) has an identifier <br>
which is a standard URI. Tokens also have identifiers as well and <br>
unlike object identifiers, token ids will contain parameters. All <br>
generated identifiers will have a unique string in them. If you manually <br>
create an identifier (such as for a client) the only caveat is that <br>
it must be unique as a string in the current store. <br>
 <br>
Manual ids <br>
---------- <br>
If you create an id manually, you should avoid parameters and <br>
fragments, since these may be used by e.g., the versioning system. <br>
Mostly you should stick with a format like <br>
 <br>
scheme:path <br>
 <br>
E.g. localhost:test/ga4gh <br>
 <br>
that is humanly readable and succinct. Don't forget you can add <br>
a description to any object. <br>
 <br>
Token ids <br>
--------- <br>
Token ids are overloaded. Note that you should probably not create <br>
a token id manually. Once created, a token id is treated as an <br>
immutable string. It may be parsed for timestamps and such, <br>
but the contract is that the exact string must match what is <br>
stored on the server and tokens are automatically deleted <br>
(aka "garbage collection") when expired. If you create token <br>
that does not conform to this, it will be ignored in garbage collection. <br>
 <br>
Since ids generally have a unique string, a common shorthand is <br>
to use the last 5 of this as a nickname. For instance, if you had token <br>
 <br>
https://localhost:9443/oauth2/5f025cc3452be217c9c95999a27ca3a6?type=authzGrant&ts=1668811290875&version=v2.0&lifetime=750000 <br>
 <br>
you might refer to it as 5f025. This is usually more than sufficient in <br>
practice to find it in the store using a regular expression. <br>
 <br>
See also: versions <br>
  
#### Examples

Some examples of generated ids <br>
 <br>
1.   testScheme:oa4md,2018:/client_id/70e46ba17e8c4d00d30dd2345da83abe <br>
     This has a specific scheme, the path identifiers it, and it has a unique string <br>
 <br>
2.   https://localhost:9443/oauth2/f019e78e6307ca11c475eeeb61c3525?type=authzGrant&ts=1667918947236&version=v2.0&lifetime=750000 <br>
     This identifies the server that created it, has a unique string and then the parameters <br>
     tell us the type, timestamp (creation time in ms) and the lifetime (also in ms). <br>
 <br>
3.   cilogon:/client_id/399d7fq5e9500801bd0e02f803a2dab8 <br>
 <br>
Some examples of manually generated client ids <br>
 <br>
ashigaru:oa4mp.oa2.fileStore <br>
localhost:test/ncsa <br>
prod:test/functor <br>
 <br>
Each of these is unique in the store and is descriptive. <br>
   

# list_keys

Command. <br>
List the keys for all the properties of this object.

# ls

Command. <br>
List the current object or other objects.

# primary_key

The primary key for this object store is always denoted with <br>
a * (star) in the list_keys command. <br>
  
#### Examples

If you list the keys for client approvals, you would see <br>
    <br>
approvals>list_keys <br>
approval_ts  approved  approver  client_id*  description  status <br>
 <br>
which means that the client_id is the primary key for this store. <br>
   

# property

Objects have properties and these are what is stored about it. <br>
If an entry in this help is a property, then the following format is <br>
used to list it: <br>
Property: TYPE (default value) <br>
Description. <br>
 <br>
Types: <br>
   Boolean : a value of true or false <br>
Identifier : A URL that is unique and usually of the form scheme:path <br>
      JSON : a JSON blob <br>
      List : [x0, x1,...]. Lists have elements all of a given type <br>
    String : Any set of characters. UTF-8 is supported <br>
 Timestamp : An ISO 8601 timestamp. Note that these us usually generated <br>
             and managed by the system <br>
       URL :  A URL. <br>
 <br>
 <br>
Defaults are: <br>
  Booleans :  true|false <br>
      JSON :  {} (empty) or the JSON blob <br>
      List :  [] (empty) or the list of elements <br>
   Strings :  "" (empty) or "characters" <br>
   Integer :  Any signed integer. All are longs (64 bit). <br>
       ANY : ** Never empty, system managed. <br>
             -- No value <br>
 <br>
Note that as admin you can indeed change system managed properties, but <br>
that should only be done in exceptional cases. <br>
For instance, if you change the identifier, then the <br>
original version effectively ceases to exist, so every reference to the old <br>
identifier must be updated on the client side. <br>
 <br>
note that if no value is specified, then listing the client will skip that <br>
property. <br>

#### Examples

E.g.#1 <br>
 <br>
Property: JSON ({}) <br>
This property... <br>
 <br>
This means that the entry for this keyword is a property, it must be JSON <br>
and the default is an empty JSON object. <br>
 <br>
E.g. #2 <br>
Property: Identifier (**) <br>
The unique.... <br>
 <br>
This means that this is a property of type identifier and must be present. <br>
For instance, the main identifier for this object always has an entry like this <br>
and the object does not exist without its unique id. <br>
 <br>
E.g. #3 <br>
Property: URL (--) <br>
This ... <br>
 <br>
Means that this is a property, if present must be a URL but is undefined <br>
as its default.

# rename

Command. <br>
Change the id of the current object. Note that this should only be done with <br>
very good reason. <br>
 <br>
See also: copy

# results

Results are sets of output from a search that are named and may be <br>
queried separately. You create them in searches using the -rs name flag, e.g. <br>
 <br>
admins> search >admin_id -r .*caltech.* -rs ligo <br>
 <br>
would search (in the admins store) the property name admin_id for any entry including the string "calttech" <br>
and stash the results in a result set named "ligo". You may now use the rs command <br>
to navigate this result set. You may have as many result sets as you like. <br>
 <br>
You may also restrict the results to a subset of properties with the -out flag. <br>
 <br>
admins> search >admin_id -out [allow_qdl,name] -r .*caltech.* -rs qdl_check <br>
 <br>
would search on the admin_id and the result set would have the two properties <br>
listed. Showing the result set then only has those. <br>
 <br>
admins>rs -list foo <br>
  foo: 56 entries,  fields=[allow_qdl, name] <br>
 <br>
admins> rs -show [11,17] foo <br>
    11. true  | Test admin client caltech-IGWN <br>
    12. true  | Test admin client caltech/LIGO <br>
    13. false | IGWN admin for VIRGO 1 <br>
    14. true  | IGWN admin for VIRGO 2 <br>
    15. true  | LIGO issuer UW Milwaukee <br>
    16. true  | LIGO issuer UW Madison <br>
 <br>
shows the subset from index 11 in the list up to but not including index 17 (or <br>
the end of the list, whichever comes first). <br>
 <br>
The -show flag also allows for displaying subsets but be warned that if you searched for <br>
a subset of properties, then only those are available. You may restrict to a subset of those, <br>
but cannot display properties you do not have. <br>


# rm

Command. <br>
Either remove an object or remove a property from the current object.

# rs

Command. <br>
Result set management. This command allows for a variety of operations on result sets. <br>
 <br>
See also: results

# search

Command. <br>
Search the store, You may search on a given property and may do so in several <br>
ways. Consult the online help for this, in particular the --ex (note the double <br>
hyphen) option will print many examples.

# serialize

Command. <br>
Takes the current object and creates and XML version of it. This may be stored in a file <br>
or simply printed to the console. <br>
 <br>
See also: deserialize

# size

Command. <br>
The number of entries in the store.

# update

Command. <br>
A wide-ranging command that allows you to update an entire object, prompting you for <br>
each property, or update a specific property.

# versions

Every stored object can be versioned. This means that a complete <br>
copy of it is made by the system and stored with a managed id. For instance <br>
if you have a client with id <br>
 <br>
oa4md,2018:/client_id/5adabf74ba28e4234ca6fa70f87f1ffe <br>
 <br>
and list its versions you may get <br>
 <br>
testScheme:oa4md,2018:/client_id/5adabf74ba28e4234ca6fa70f87f1ffe#version=0 <br>
testScheme:oa4md,2018:/client_id/5adabf74ba28e4234ca6fa70f87f1ffe#version=1 <br>
testScheme:oa4md,2018:/client_id/5adabf74ba28e4234ca6fa70f87f1ffe#version=2 <br>
 <br>
This means that there are 3 versions. Since they are chronological, that highest <br>
number is the most recent. <br>
