import numpy as np

myMatrix = np.loadtxt("3.txt", delimiter=" ")

# Берем индикатор по состоянию в котором изначально
vector = np.array([[0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0]])

myMatrix = np.linalg.matrix_power(myMatrix, 242404135)
vector = np.dot(vector, myMatrix)

print(vector)