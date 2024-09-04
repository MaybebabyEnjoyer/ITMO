import numpy as np

n = 10
a = np.loadtxt("5.txt", delimiter=" ").transpose() - np.eye(n)
a[-1] = np.ones(n)
print(np.linalg.solve(a, np.array([0, 0, 0, 0, 0, 0, 0, 0, 0, 1])))
