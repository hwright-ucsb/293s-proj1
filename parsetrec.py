f = open("lines-trec45.txt","r")
g1 = open("goodtrec1.xml","w")
g2 = open("goodtrec2.xml","w")
g3 = open("goodtrec3.xml","w")

g1.write('<add>\n')
g2.write('<add>\n')
g3.write('<add>\n')

a = 0
for line in f:
	a = (a+1) % 3
	stuff = line.split("\t")

	if a == 0:
		g1.write('<doc>\n')

		g1.write('\t<field name="DOCNO">')
		g1.write(stuff[0])
		g1.write('</field>\n')

		g1.write('\t<field name="TITLE">')
		g1.write(stuff[1])
		g1.write('</field>\n')

		g1.write('\t<field name="BODY">')
		g1.write(stuff[2].strip('\n')) # body ends with a newline char
		g1.write('\t</field>\n')

		g1.write('</doc>\n')

	elif a == 1:
		g2.write('<doc>\n')

		g2.write('\t<field name="DOCNO">')
		g2.write(stuff[0])
		g2.write('</field>\n')

		g2.write('\t<field name="TITLE">')
		g2.write(stuff[1])
		g2.write('</field>\n')

		g2.write('\t<field name="BODY">')
		g2.write(stuff[2].strip('\n')) # body ends with a newline char
		g2.write('\t</field>\n')

		g2.write('</doc>\n')

	elif a == 2:
		g3.write('<doc>\n')

		g3.write('\t<field name="DOCNO">')
		g3.write(stuff[0])
		g3.write('</field>\n')

		g3.write('\t<field name="TITLE">')
		g3.write(stuff[1])
		g3.write('</field>\n')

		g3.write('\t<field name="BODY">')
		g3.write(stuff[2].strip('\n')) # body ends with a newline char
		g3.write('\t</field>\n')

		g3.write('</doc>\n')

g1.write('</add>')
g2.write('</add>')
g3.write('</add>')

f.close()
g1.close()
g2.close()
g3.close()