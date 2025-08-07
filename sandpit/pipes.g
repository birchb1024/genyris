var P (Pipe!open 23)

var I (P(.input))

print (I(.hasData))
print(I(.read))
print(I(.getline))
print (parse I)
