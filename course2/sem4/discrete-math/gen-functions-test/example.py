from solver import *


for ind, f in enumerate(div_fast([1, -1, -1], [1, -2, 0, 2])):
	print(ind, f)
print(rec([[-1, -1], [-2, -2], [-3, 1]], [-2, 0, 7]))
print(asymptotic_by_rec([[-1, 5], [-2, 8], [-3, -12]], [1, 5, 33]))

c = Combinatorics([Combinatorics.CombObject(weight=1, quantity=2), Combinatorics.CombObject(weight=2, quantity=1)])
print(c.composition([Combinatorics.Comb.Seq_plus.value, Combinatorics.Comb.MSet_plus.value]))

f = FormalProduct([[0, 0, 0, 0], [1, 0, 0, 0], [2, 3, 0, 0], [0, 0, 1, 0]])
print(f.solve())
