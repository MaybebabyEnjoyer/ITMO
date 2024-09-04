import numpy as np

# 2 task
# A = np.array([[0.1, 0, 0.3, 0.1, 0.1, 0.4],
#               [0.2, .2, .2, .1, .3, 0],
#               [.2, .2, .1, .4, .1, 0],
#               [0, .2, .1, .1, .4, .2],
#               [.1, .2, .2, .3, 0, .2],
#               [.1, .3, .3, 0, .2, .1]])
#
# print(A[-1] @ A)

# 3 task
# A = np.loadtxt("3.txt", delimiter=" ")
# n = 13
# line = 10
# k = 204557735
# vector = np.array([[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0]])
# print(vector @ np.linalg.matrix_power(A, k))

# 4 task
# n = 8
# line = 7
# A = np.loadtxt("4.txt", delimiter=" ")
#
# Q = A
# Q[1], Q[5], Q[7] = np.zeros(8), np.zeros(8), np.zeros(8)
# N = np.linalg.inv(np.eye(8, 8) - Q)
# print(np.sum(Q[-2] @ N))
#
# R = A
# for i in range(len(R)):
# 	if i not in {1, 5, 7}:
# 		R[i] = np.zeros(8)
# R[1] = np.array([0, 1, 0, 0, 0, 0, 0, 0])
# R[5] = np.array([0, 0, 0, 0, 0, 1, 0, 0])
# R[7] = np.array([0, 0, 0, 0, 0, 0, 0, 1])
# print((N @ R)[-2])

# 5 task
# n = 10
# A = np.loadtxt("5.txt", delimiter=" ").transpose() - np.eye(10, 10)
# A[-1] = np.ones(n)
# print(np.linalg.solve(A, np.array([0, 0, 0, 0, 0, 0, 0, 0, 0, 1])))