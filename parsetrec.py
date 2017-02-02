f = open("lines-trec45.txt","r")
g = open("goodtrec.xml","w")

g.write('<add>\n')
for line in f:
	stuff = line.split("\t")
	g.write('<doc>\n')

	g.write('\t<field name="DOCNO">')
	g.write(stuff[0])
	g.write('</field>\n')

	g.write('\t<field name="TITLE">')
	g.write(stuff[1])
	g.write('</field>\n')

	g.write('\t<field name="BODY">')
	g.write(stuff[2].strip('\n')) # body ends with a newline char
	g.write('\t</field>\n')

	g.write('</doc>\n')

g.write('</add>')

f.close()
g.close()