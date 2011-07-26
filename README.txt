Two directions to go for views:
General, regular (homogeneous) data structure:
  Objects have properties.
  The values for those properties are lists, to reflect the possible multi-valued nature of RDF.
  The values in those lists are strings, numbers, booleans, or other objects.
  Additionaly, add a special property called "uri" to act as an ID for objects that have URI's.

Simplified:
  Same as above, with some condensation rules applied:
    Reduce an object that has no properties other than its URI down to just a string of the URI itself.
    Properties with a list of one value have just that value, not the list of that value.

The general view more accurately reflects the underlying RDF structure, and permits general processinging of the data without knowing the schema ahead of time.  More possible to round-trip back to RDF.

The simplified view might look less needlessly layered to people who are used to other one-off JSON API's.
"I just want to hack on the data without learning anything about RDF/the Semantic Web."
