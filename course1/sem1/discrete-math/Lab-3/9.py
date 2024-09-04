import numpy as np
import scipy.linalg as sp
import sympy as sy


def bracket(n):
    if n == 0:
        return ['']
    else:
        return ['(' + x + ')' + y for i in range(n) for x in bracket(i) for y in bracket(n - i - 1)]


if __name__ == '__main__':
    # n = int(input())
    # answers = sorted(bracket(n))
    # for answer in answers:
    #     print(answer)

    u = sy.Matrix([[1, 0, 0, 0]])
    v = sy.Matrix([[1], [1], [1], [0]])
    D = sy.Matrix([[1, 1, 0, 0], [1, 0, 1, 0], [1, 0, 0, 1], [0, 0, 0, 2]])
    t = sy.symbols("t")

    print(u @ sy.invert((np.eye(4, 4) - t*D)) @ v)
