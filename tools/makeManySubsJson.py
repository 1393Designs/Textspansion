#!/bin/python2
import sys

f = open("manyEntries.json", "w")

stdout_save = sys.stdout
sys.stdout = f

print "[ {"

for i in range(9000):
	print "\t\"subTitle\" : \"", i, "\","
	print "\t\"pasteText\" : \"", i, "\","
	print "\t\"privacy\" : \"0\""
	if i < 8999:
		print "}, {"

print "} ]"

sys.stdout = stdout_save
