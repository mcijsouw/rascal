@license{
  Copyright (c) 2009-2011 CWI
  All rights reserved. This program and the accompanying materials
  are made available under the terms of the Eclipse Public License v1.0
  which accompanies this distribution, and is available at
  http://www.eclipse.org/legal/epl-v10.html
}
@contributor{Paul Klint - Paul.Klint@cwi.nl - CWI}

@doc{
Synopsis: Functions for reading and writing Comma-Separated Values (CSV) files.
Description:
The [CSV format](http://tools.ietf.org/html/rfc4180) is used for exchanging
information between spreadsheets and databases. A CSV file has the following structure:

* An optional header line consisting of field names separated by comma's.
* One or more lines consisting of values separated by comma's.

The following functions are provided:
<toc Rascal/Library/lang/csv/IO 1>

Examples:
<listing>
field_name1,field_name2,field_name3
aaa,bbb,ccc CRLF
zzz,yyy,xxx CRLF
</listing>



}
module lang::csv::IO

@doc{
Synopsis: Read a relation from a CSV (Comma Separated Values) file.

Description:

Read a CSV file and return a value of a required type.

The `result` argument is the required type of the value that is produced by reading the CSV
that is found at `location`.
Optionally, a map of options can be given:
* `"header" : "true"` specifies that a header is present (default).
* `"header" : "false"` specifies that no header is present.
* `"separator" : ","` specifies that `,` is the separator character between fields (default).


The type of the resulting value is _inferred_ in three steps:

_Step 1_: The type of each field occurrence is inferred from its contents using the
following rules:
* An empty value is of type `void`.
* A field that contains a string that corresponds to a number is numeric.
* A field that contains `true` or `false` is of type is `bool`.
* In all other cases the field is of type `str`.


_Step 2_: The type of each field is inferred from the type of all of its occurrences:

* If all occurrences have a numeric type, then the smallest possible type is used.
* If the occurrences have a mixed type, i.e., numeric, non-numeric, boolean or string, then the type is `str`.
* If the requested type for a field is `str` and another type would be inferred by the preceeding two rules, 
its inferred type will be `str`.


_Step 3_: The inferred type should be a subtype of the requested type.

Reading the values in fields is straightforward, except for the case that the text in the field is enclosed between double quotes (`"`):
* the text may include line breaks which are represented as `\n` in the resulting string value of the field.
* the text may contain escaped double quotes (`""`) which are represented as `\"` in the resulting string value.

Examples:

Given is the follwing file `ex1.csv`:
<listing experiments/RascalTutor/Courses/Rascal/Libraries/lang/csv/ex1.csv>
We can read it in various ways:
<screen>
import lang::csv::IO;
R1 = readCSV(#rel[int position, str artist, str title, int year],  |std:///experiments/RascalTutor/Courses/Rascal/Libraries/lang/csv/ex1.csv|, ("separator" : ";"));
//Now we can, for instance, select one of the fields of `R1`:
R1.artist;
//It is also possible to use the most general type `value` as result type:
R1 = readCSV(#value,  |std:///experiments/RascalTutor/Courses/Rascal/Libraries/lang/csv/ex1.csv|, ("separator" : ";"));
</screen>

}
@javaClass{org.rascalmpl.library.lang.csv.IO}
@reflect{Uses URI Resolver Registry}
public java &T readCSV(type[&T] result, loc location);

@javaClass{org.rascalmpl.library.lang.csv.IO}
@reflect{Uses URI Resolver Registry}
public java &T readCSV(type[&T] result, loc location, map[str,str] options);


@doc{
Synopsis: Write a relation to a CSV (Comma Separated Values) file.

Description:
Write `relation` to a CSV file at `location`.
The options influence the way the actrual CSV file is written:

* `"header"`: add (`"true"`) or omit (`"false"`) a header.
* `"separator"`: defines the separator character between fields (default is `,`).



Examples:
<screen>
import lang::csv::IO;
rel[int position, str artist, str title, int year] R1 = {
  <1,"Eagles","Hotel California",1977>,
  <2,"Queen","Bohemian rhapsody",1975>,
  <3,"Boudewijn de Groot","Avond",1997>
};
writeCSV(R1, |std:///experiments/RascalTutor/Courses/Rascal/Libraries/lang/csv/ex1a.csv|);
writeCSV(R1, |std:///experiments/RascalTutor/Courses/Rascal/Libraries/lang/csv/ex1b.csv|, ("header" : "false", "separator" : ";"));
</screen>
will produce the following files:

`ex1a.csv` (with a header line and default separator `,`):
<listing experiments/RascalTutor/Courses/Rascal/Libraries/lang/csv/ex1a.csv>
`ex1b.csv` (without a header line with separator `;`):
<listing experiments/RascalTutor/Courses/Rascal/Libraries/lang/csv/ex1b.csv>

<warning>For unknown reason the field names get lost in the first example</warning>


}
@javaClass{org.rascalmpl.library.lang.csv.IO}
@reflect{Uses type parameter.}
public java void writeCSV(&T relation, loc location);

@javaClass{org.rascalmpl.library.lang.csv.IO}
@reflect{Uses type parameter.}
public java void writeCSV(&T relation, loc location, map[str,str] options);
