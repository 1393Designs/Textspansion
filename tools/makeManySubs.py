#!/bin/python2
import sys

f = open("manyEntries.xml", "w")

stdout_save = sys.stdout
sys.stdout = f

print "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?>"
print "<Textspansion>"

for i in range(9000):
	print "\t<Subs>"
	print "\t\t<Short>Title", i, "</Short>"
	print "\t\t<Long>Phrase", i, "</Long>"
	print "\t\t<Private>0</Private>"
	print "\t</Subs>"

print "</Textspansion>"

sys.stdout = stdout_save
